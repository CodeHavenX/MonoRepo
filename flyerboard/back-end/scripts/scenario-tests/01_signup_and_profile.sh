#!/usr/bin/env bash
# Scenario 1: A brand-new user signs up, creates their profile, and fetches it back.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "01: New user signup + fetch own profile"

EMAIL="scenario01-$$@dev.local"
TOKEN=$(mint_token "$EMAIL" --create)
[[ -n "$TOKEN" ]] && ok "minted auth token for new user" || bad "failed to mint auth token"

req POST "/api/v1/user" "$TOKEN" '{"first_name":"Ada","last_name":"Lovelace"}'
assert_eq "create profile returns 200" "200" "$HTTP_STATUS"
FIRST=$(echo "$RESP_BODY" | jq -r '.first_name // empty')
ROLE=$(echo "$RESP_BODY" | jq -r '.role // empty')
assert_eq "created profile has correct first_name" "Ada" "$FIRST"
assert_eq "new profile defaults to user role" "user" "$ROLE"

req GET "/api/v1/user/me" "$TOKEN"
assert_eq "GET /user/me returns 200 after signup" "200" "$HTTP_STATUS"
ME_FIRST=$(echo "$RESP_BODY" | jq -r '.first_name // empty')
assert_eq "fetched profile matches created profile" "Ada" "$ME_FIRST"

summary
