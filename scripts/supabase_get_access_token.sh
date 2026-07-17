#!/usr/bin/env bash
set -euo pipefail

# Project-independent Supabase access token retrieval.
#
# Every backend in this monorepo (edifikana, flyerboard, ...) validates bearer tokens by
# forwarding them to Supabase's own /auth/v1/user endpoint rather than verifying JWTs locally,
# so a real, Supabase-issued token works against ANY of them as long as it was issued by the
# same Supabase instance the target backend is configured against. Locally, every project's
# `supabase start` uses the Supabase CLI's fixed demo JWT secret/keys/ports, so this script
# does not need to know which project you're testing -- just make sure that project's
# `supabase start` is running.
#
# Usage:
#   scripts/supabase_get_access_token.sh [OPTIONS]
#
# Prints ONLY the access token (JWT) to stdout. Everything else (diagnostics, generated
# passwords, errors) goes to stderr, so this is safe to use as:
#   TOKEN=$(scripts/supabase_get_access_token.sh -e user1@dev.local -p secret)
#   curl -H "Authorization: Bearer $(scripts/supabase_get_access_token.sh -e user1@dev.local -p secret)" ...
#
# Modes (mutually exclusive, default: --signin):
#   --signin           Sign in as a user who already has a password set.
#   --create           Create a brand-new, confirmed user via the admin API, then sign in.
#   --reset-password   Set/overwrite a password on an EXISTING user via the admin API
#                       (looked up by email, or directly via --user-id), then sign in.
#                       Use this to get a token "as" a specific seeded dev fixture, e.g.
#                       owner1.org1@dev.local from edifikana/back-end/supabase/seed.sql.
#   --list-users        Print email + user id for up to the first 100 users via the admin
#                        API, then exit (no token is produced; --email is not required).
#   --otp               Exercise the REAL OTP/magic-link flow: sends an OTP to the email
#                        (creating the user if needed), fetches the code from the local
#                        mail catcher (Mailpit, at MAILPIT_URL), verifies it, and prints
#                        the resulting token. Unlike the other modes, this needs only the
#                        anon key -- no service-role/admin access -- since it's exactly
#                        what a real client does. The received code is also printed to
#                        stderr, e.g. if you just want to key it into the app's UI by hand.
#
# Required (except for --list-users):
#   -e, --email EMAIL        Email of the user to sign in as / create / reset.
#
# Optional:
#   -p, --password PASSWORD  Password to use. For --create/--reset-password, a random one is
#                             generated and printed to stderr if omitted.
#   -i, --user-id ID         Known auth.users.id -- skips the admin list lookup in --reset-password.
#   -u, --url URL             Supabase URL (default: $SUPABASE_URL or http://127.0.0.1:54321).
#   -h, --help                Show this help and exit.
#
# --list-users is capped at 100 results (a single admin API page) and does not paginate
# further -- it's meant for eyeballing/grepping the seeded dev accounts, not bulk export.
#
# Env var overrides:
#   SUPABASE_URL, SUPABASE_ANON_KEY, SUPABASE_SERVICE_ROLE_KEY, MAILPIT_URL
#   Defaults are the well-known local Supabase CLI demo keys (identical on every machine and
#   every project's `supabase start` unless a project overrides auth.jwt_secret -- none do
#   here). Not secrets; only override when pointing at a non-default instance. MAILPIT_URL
#   defaults to http://127.0.0.1:54324 (the CLI's local mail catcher, used by --otp).

SCRIPT_NAME=$(basename "$0")

usage() {
  sed -n '4,57p' "$0" | sed 's/^# \{0,1\}//'
}

SUPABASE_URL="${SUPABASE_URL:-http://127.0.0.1:54321}"
SUPABASE_ANON_KEY="${SUPABASE_ANON_KEY:-eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS1kZW1vIiwicm9sZSI6ImFub24iLCJleHAiOjE5ODM4MTI5OTZ9.CRXP1A7WOeoJeXxjNni43kdQwgnWNReilDMblYTn_I0}"
SUPABASE_SERVICE_ROLE_KEY="${SUPABASE_SERVICE_ROLE_KEY:-eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS1kZW1vIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImV4cCI6MTk4MzgxMjk5Nn0.EGIM96RAZx35lJzdJsyH-qQwv8Hdp7fsn3W0YpN81IU}"
MAILPIT_URL="${MAILPIT_URL:-http://127.0.0.1:54324}"
LIST_USERS_LIMIT=100
OTP_WAIT_ATTEMPTS=15

MODE="signin"
EMAIL=""
PASSWORD=""
USER_ID=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --signin) MODE="signin"; shift ;;
    --create) MODE="create"; shift ;;
    --reset-password) MODE="reset-password"; shift ;;
    --list-users) MODE="list-users"; shift ;;
    --otp) MODE="otp"; shift ;;
    -e|--email) EMAIL="$2"; shift 2 ;;
    -p|--password) PASSWORD="$2"; shift 2 ;;
    -i|--user-id) USER_ID="$2"; shift 2 ;;
    -u|--url) SUPABASE_URL="$2"; shift 2 ;;
    -h|--help) usage; exit 0 ;;
    *) echo "ERROR: unknown argument: $1" >&2; usage >&2; exit 1 ;;
  esac
done

if [[ -z "$EMAIL" && "$MODE" != "list-users" ]]; then
  echo "ERROR: --email is required." >&2
  exit 1
fi

if ! command -v jq >/dev/null 2>&1; then
  echo "ERROR: this script requires jq." >&2
  exit 1
fi

# Admin (write) operations must never accidentally hit a non-local instance, and --otp
# depends on a local mail catcher that only exists for local `supabase start` anyway.
if [[ "$MODE" != "signin" ]]; then
  if [[ "$SUPABASE_URL" != *"localhost"* && "$SUPABASE_URL" != *"127.0.0.1"* ]]; then
    echo "ERROR: refusing to run '$MODE' against a non-local SUPABASE_URL ($SUPABASE_URL)." >&2
    echo "Pass --signin (read-only) if you intend to sign in against a remote instance." >&2
    exit 1
  fi
fi

random_password() {
  # 24 random alphanumeric characters -- more than enough for a throwaway test password.
  # openssl's output is finite (unlike /dev/urandom), so this can't SIGPIPE `head` under pipefail.
  openssl rand -base64 32 | LC_ALL=C tr -dc 'A-Za-z0-9' | head -c 24
}

admin_headers=(-H "apikey: $SUPABASE_SERVICE_ROLE_KEY" -H "Authorization: Bearer $SUPABASE_SERVICE_ROLE_KEY")

find_user_id_by_email() {
  local email="$1"
  local response
  response=$(curl -sf "${admin_headers[@]}" "$SUPABASE_URL/auth/v1/admin/users?per_page=1000")
  jq -r --arg email "$email" '.users[] | select(.email == $email) | .id' <<<"$response" | head -n1
}

list_users() {
  local response count
  response=$(curl -sf "${admin_headers[@]}" "$SUPABASE_URL/auth/v1/admin/users?per_page=$LIST_USERS_LIMIT")
  count=$(jq -r '.users | length' <<<"$response")

  {
    printf 'EMAIL\tUSER_ID\tCREATED_AT\tLAST_SIGN_IN_AT\n'
    jq -r '.users[] | [.email, .id, .created_at, (.last_sign_in_at // "-")] | @tsv' <<<"$response" | sort
  } | column -t -s "$(printf '\t')"

  if [[ "$count" -ge "$LIST_USERS_LIMIT" ]]; then
    echo "" >&2
    echo "NOTE: showing the first $LIST_USERS_LIMIT users (admin API page cap); there may be more." >&2
  fi
}

send_otp() {
  local email="$1"
  local response error_msg
  response=$(curl -s -X POST "$SUPABASE_URL/auth/v1/otp" \
    -H "apikey: $SUPABASE_ANON_KEY" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg email "$email" '{email: $email}')")
  error_msg=$(jq -r '.msg // .error_description // .error // empty' <<<"$response")
  if [[ -n "$error_msg" ]]; then
    echo "ERROR: failed to send OTP to '$email': $error_msg" >&2
    exit 1
  fi
}

# Polls the local mail catcher for the newest message to [email] and extracts the 6-digit
# OTP code from its body. Mailpit's default email template includes both a magic-link URL
# and "enter the code: NNNNNN"; we only need the latter.
wait_for_otp_code() {
  local email="$1"
  local attempt search_response message_id text code
  for attempt in $(seq 1 "$OTP_WAIT_ATTEMPTS"); do
    search_response=$(curl -sf --get "$MAILPIT_URL/api/v1/search" --data-urlencode "query=to:$email" 2>/dev/null) || search_response=""
    message_id=$(jq -r '.messages | sort_by(.Created) | last | .ID // empty' <<<"$search_response" 2>/dev/null || true)
    if [[ -n "$message_id" ]]; then
      text=$(curl -sf "$MAILPIT_URL/api/v1/message/$message_id" | jq -r '.Text // empty')
      code=$(grep -oE 'code: [0-9]{6}' <<<"$text" | grep -oE '[0-9]{6}' || true)
      if [[ -n "$code" ]]; then
        printf '%s' "$code"
        return 0
      fi
    fi
    sleep 1
  done
  echo "ERROR: timed out waiting for an OTP email to '$email' at $MAILPIT_URL." >&2
  echo "Is the local Supabase stack running (\`supabase start\`) and MAILPIT_URL correct?" >&2
  exit 1
}

verify_otp() {
  local email="$1"
  local code="$2"
  local response token
  response=$(curl -s -X POST "$SUPABASE_URL/auth/v1/verify" \
    -H "apikey: $SUPABASE_ANON_KEY" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg email "$email" --arg token "$code" '{type: "email", email: $email, token: $token}')")

  token=$(jq -r '.access_token // empty' <<<"$response")
  if [[ -z "$token" ]]; then
    local error_msg
    error_msg=$(jq -r '.error_description // .msg // .error // "unknown error"' <<<"$response")
    echo "ERROR: OTP verification failed for '$email': $error_msg" >&2
    exit 1
  fi
  printf '%s\n' "$token"
}

sign_in() {
  local email="$1"
  local password="$2"
  local response
  response=$(curl -s -X POST "$SUPABASE_URL/auth/v1/token?grant_type=password" \
    -H "apikey: $SUPABASE_ANON_KEY" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg email "$email" --arg password "$password" '{email: $email, password: $password}')")

  local token
  token=$(jq -r '.access_token // empty' <<<"$response")
  if [[ -z "$token" ]]; then
    local error_msg
    error_msg=$(jq -r '.error_description // .msg // .error // "unknown error"' <<<"$response")
    echo "ERROR: sign-in failed for '$email': $error_msg" >&2
    exit 1
  fi
  printf '%s\n' "$token"
}

if [[ "$MODE" == "list-users" ]]; then
  list_users
  exit 0
fi

if [[ "$MODE" == "otp" ]]; then
  send_otp "$EMAIL"
  echo "OTP sent to $EMAIL, waiting for it to arrive at $MAILPIT_URL..." >&2
  code=$(wait_for_otp_code "$EMAIL")
  echo "Received OTP code: $code" >&2
  verify_otp "$EMAIL" "$code"
  exit 0
fi

case "$MODE" in
  signin)
    if [[ -z "$PASSWORD" ]]; then
      echo "ERROR: --password is required for --signin." >&2
      exit 1
    fi
    ;;

  create)
    if [[ -z "$PASSWORD" ]]; then
      PASSWORD=$(random_password)
      echo "Generated password for $EMAIL: $PASSWORD" >&2
    fi
    response=$(curl -s -X POST "$SUPABASE_URL/auth/v1/admin/users" \
      "${admin_headers[@]}" \
      -H "Content-Type: application/json" \
      -d "$(jq -n --arg email "$EMAIL" --arg password "$PASSWORD" '{email: $email, password: $password, email_confirm: true}')")
    new_id=$(jq -r '.id // empty' <<<"$response")
    if [[ -z "$new_id" ]]; then
      error_msg=$(jq -r '.msg // .error_description // .error // "unknown error"' <<<"$response")
      echo "ERROR: failed to create user '$EMAIL': $error_msg" >&2
      exit 1
    fi
    echo "Created user $EMAIL ($new_id)" >&2
    ;;

  reset-password)
    if [[ -z "$USER_ID" ]]; then
      USER_ID=$(find_user_id_by_email "$EMAIL")
      if [[ -z "$USER_ID" ]]; then
        echo "ERROR: no existing user found for email '$EMAIL'. Pass --user-id, or use --create instead." >&2
        exit 1
      fi
    fi
    if [[ -z "$PASSWORD" ]]; then
      PASSWORD=$(random_password)
      echo "Generated password for $EMAIL: $PASSWORD" >&2
    fi
    response=$(curl -s -X PUT "$SUPABASE_URL/auth/v1/admin/users/$USER_ID" \
      "${admin_headers[@]}" \
      -H "Content-Type: application/json" \
      -d "$(jq -n --arg password "$PASSWORD" '{password: $password}')")
    updated_id=$(jq -r '.id // empty' <<<"$response")
    if [[ -z "$updated_id" ]]; then
      error_msg=$(jq -r '.msg // .error_description // .error // "unknown error"' <<<"$response")
      echo "ERROR: failed to reset password for user '$USER_ID': $error_msg" >&2
      exit 1
    fi
    echo "Reset password for $EMAIL ($USER_ID)" >&2
    ;;
esac

sign_in "$EMAIL" "$PASSWORD"
