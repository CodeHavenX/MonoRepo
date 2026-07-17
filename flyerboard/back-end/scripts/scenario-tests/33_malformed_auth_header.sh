#!/usr/bin/env bash
# Scenario 33: Malformed Authorization header variants -- missing "Bearer " prefix, lowercase
# scheme, extra whitespace, empty token.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "33: Malformed Authorization header variants"

TOKEN=$(mint_token "scenario33-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Auth","last_name":"ThirtyThree"}'

req_raw GET "/api/v1/user/me" "" "" -H "Authorization: ${TOKEN}"
assert_eq "raw token with no 'Bearer ' prefix is rejected" "401" "$HTTP_STATUS"

req_raw GET "/api/v1/user/me" "" "" -H "Authorization: bearer ${TOKEN}"
assert_in "lowercase 'bearer' scheme is handled without a 500" "$HTTP_STATUS" "200" "401"

req_raw GET "/api/v1/user/me" "" "" -H "Authorization: Bearer  ${TOKEN}"
assert_in "double space after Bearer is handled without a 500" "$HTTP_STATUS" "200" "401"

req_raw GET "/api/v1/user/me" "" "" -H "Authorization: Bearer "
assert_eq "empty bearer token is rejected" "401" "$HTTP_STATUS"

req_raw GET "/api/v1/user/me" "" "" -H "Authorization:"
assert_eq "empty Authorization header is treated as unauthenticated" "401" "$HTTP_STATUS"

summary
