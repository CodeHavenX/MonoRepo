#!/usr/bin/env bash
# Scenario 40: A very large request body (multi-MB description). The bar here is robustness --
# the server must not hang or crash with a 500; a clean rejection (400/413) or a truncated 200
# are both acceptable outcomes.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "40: Oversized request body"

TOKEN=$(mint_token "scenario40-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Big","last_name":"Forty"}'

HUGE_DESC=$(printf 'x%.0s' {1..2000000}) # ~2MB of 'x'
req POST "/api/v1/flyers" "$TOKEN" "{\"title\":\"Huge\",\"description\":\"${HUGE_DESC}\"}"
assert_in "a ~2MB description does not 500 and does not hang" "$HTTP_STATUS" "200" "400" "413"

summary
