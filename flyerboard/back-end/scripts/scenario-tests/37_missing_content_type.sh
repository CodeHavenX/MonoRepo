#!/usr/bin/env bash
# Scenario 37: POST/PUT with a JSON body but no Content-Type header at all.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "37: Missing Content-Type header"

TOKEN=$(mint_token "scenario37-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"NoType","last_name":"ThirtySeven"}'

req_raw POST "/api/v1/flyers" "$TOKEN" '{"title":"No content type","description":"desc"}'
assert_in "POST with a JSON body but no Content-Type header does not 500" "$HTTP_STATUS" "200" "400" "415"

summary
