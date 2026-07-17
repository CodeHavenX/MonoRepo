#!/usr/bin/env bash
# Shared helpers for the Flyerboard back-end client-simulation scenario scripts.
# See README.md in this directory for prerequisites and usage.
set -uo pipefail

BASE_URL="${BASE_URL:-http://127.0.0.1:9292}"
TOKEN_SCRIPT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../.." && pwd)/scripts/supabase_get_access_token.sh"
DB_CONTAINER="${DB_CONTAINER:-supabase_db_flyerboard}"

PASS=0
FAIL=0
FAILURES=()

ok() {
    PASS=$((PASS + 1))
    echo "  PASS: $1"
}

bad() {
    FAIL=$((FAIL + 1))
    FAILURES+=("$1")
    echo "  FAIL: $1"
}

# assert_eq <description> <expected> <actual>
assert_eq() {
    local desc="$1" expected="$2" actual="$3"
    if [[ "$expected" == "$actual" ]]; then
        ok "$desc (got $actual)"
    else
        bad "$desc (expected $expected, got $actual)"
    fi
}

# assert_in <description> <actual> <candidate1> [candidate2 ...]
# Passes if actual matches any of the candidates. Useful when the spec doesn't pin down a single
# correct status code (e.g. "must not be a 500") but a status code is still asserted deliberately.
assert_in() {
    local desc="$1" actual="$2"
    shift 2
    for candidate in "$@"; do
        if [[ "$actual" == "$candidate" ]]; then
            ok "$desc (got $actual)"
            return
        fi
    done
    bad "$desc (got $actual, expected one of: $*)"
}

# assert_not_eq <description> <not_expected> <actual>
assert_not_eq() {
    local desc="$1" not_expected="$2" actual="$3"
    if [[ "$not_expected" != "$actual" ]]; then
        ok "$desc (got $actual)"
    else
        bad "$desc (got $actual, which is exactly what must not happen)"
    fi
}

# mint_token <email> [--create|--reset-password|--signin]
mint_token() {
    local email="$1" mode="${2:---create}"
    "$TOKEN_SCRIPT" "$mode" -e "$email" 2>/dev/null
}

# promote_admin <user_uuid>
promote_admin() {
    local uuid="$1"
    db_exec "UPDATE public.user_profiles SET role = 'admin' WHERE id = '${uuid}';"
}

# db_exec <sql>
# Runs arbitrary SQL against the local Supabase Postgres container. Used only to set up state
# that isn't reachable through the API itself (e.g. simulating a profile deleted out-of-band, or
# fast-forwarding a flyer past its expiry without waiting for the hourly ExpiryService sweep).
db_exec() {
    local sql="$1"
    docker exec -i "$DB_CONTAINER" psql -U postgres -q -c "$sql" >/dev/null
}

# db_query <sql>
# Same as db_exec but prints the result (tuples only, no header/footer), for scripts that need
# to read a value back.
db_query() {
    local sql="$1"
    docker exec -i "$DB_CONTAINER" psql -U postgres -t -A -c "$sql"
}

# user_id_from_token <jwt>
user_id_from_token() {
    local jwt="$1"
    local payload
    payload=$(echo "$jwt" | cut -d. -f2)
    payload=$(echo "$payload" | tr '_-' '/+')
    local mod=$(( ${#payload} % 4 ))
    if [[ $mod -eq 2 ]]; then payload="${payload}=="; elif [[ $mod -eq 3 ]]; then payload="${payload}="; fi
    echo "$payload" | base64 -d 2>/dev/null | jq -r '.sub'
}

# req METHOD PATH [TOKEN] [JSON_BODY]
# Prints body to stdout, sets HTTP_STATUS var. Sends a standard `Content-Type: application/json`
# header whenever a body is provided. For requests that need non-default headers (missing/wrong
# Content-Type, raw non-JSON bodies, etc.), use req_raw instead.
req() {
    local method="$1" path="$2" token="${3:-}" body="${4:-}"
    local args=(-s -o /tmp/flyerboard_resp_body.$$ -w "%{http_code}" -X "$method" "${BASE_URL}${path}")
    if [[ -n "$token" ]]; then
        args+=(-H "Authorization: Bearer ${token}")
    fi
    if [[ -n "$body" ]]; then
        # Body goes through a temp file (--data-binary @file) rather than a literal -d argument
        # so large bodies (see scenario 40) don't blow the OS argument-length limit.
        printf '%s' "$body" > /tmp/flyerboard_req_body.$$
        args+=(-H "Content-Type: application/json" --data-binary "@/tmp/flyerboard_req_body.$$")
    fi
    HTTP_STATUS=$(curl "${args[@]}")
    RESP_BODY=$(cat /tmp/flyerboard_resp_body.$$ 2>/dev/null)
    rm -f /tmp/flyerboard_resp_body.$$ /tmp/flyerboard_req_body.$$
}

# req_raw METHOD PATH [TOKEN] [BODY] [EXTRA_CURL_ARGS...]
# Like req, but sends no Content-Type by default and lets the caller pass arbitrary extra curl
# args (e.g. -H "Content-Type: text/plain") so malformed-transport scenarios can be expressed.
req_raw() {
    local method="$1" path="$2" token="${3:-}" body="${4:-}"
    shift 4 2>/dev/null || shift $#
    local args=(-s -o /tmp/flyerboard_resp_body.$$ -w "%{http_code}" -X "$method" "${BASE_URL}${path}")
    if [[ -n "$token" ]]; then
        args+=(-H "Authorization: Bearer ${token}")
    fi
    if [[ -n "$body" ]]; then
        printf '%s' "$body" > /tmp/flyerboard_req_body.$$
        args+=(--data-binary "@/tmp/flyerboard_req_body.$$")
    fi
    args+=("$@")
    HTTP_STATUS=$(curl "${args[@]}")
    RESP_BODY=$(cat /tmp/flyerboard_resp_body.$$ 2>/dev/null)
    rm -f /tmp/flyerboard_resp_body.$$ /tmp/flyerboard_req_body.$$
}

# create_user_with_profile <email> [first] [last]
# Convenience: mints a token and completes signup in one call. Echoes nothing; sets TOKEN and
# USER_UUID globals.
create_user_with_profile() {
    local email="$1" first="${2:-First}" last="${3:-Last}"
    TOKEN=$(mint_token "$email" --create)
    req POST "/api/v1/user" "$TOKEN" "{\"first_name\":\"${first}\",\"last_name\":\"${last}\"}"
    USER_UUID=$(user_id_from_token "$TOKEN")
}

# create_admin <email> [first] [last]
# Convenience: mints a token, completes signup, then promotes to admin. Sets TOKEN and USER_UUID.
create_admin() {
    local email="$1" first="${2:-Admin}" last="${3:-User}"
    create_user_with_profile "$email" "$first" "$last"
    promote_admin "$USER_UUID"
}

section() {
    echo ""
    echo "=== $1 ==="
}

summary() {
    echo ""
    echo "--- Summary: $PASS passed, $FAIL failed ---"
    if [[ $FAIL -gt 0 ]]; then
        printf '  - %s\n' "${FAILURES[@]}"
    fi
    [[ $FAIL -eq 0 ]]
}
