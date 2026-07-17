#!/usr/bin/env bash
# Scenario 38: A well-formed JSON body sent with the wrong Content-Type (text/plain).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "38: Wrong Content-Type header"

TOKEN=$(mint_token "scenario38-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"WrongType","last_name":"ThirtyEight"}'

req_raw POST "/api/v1/flyers" "$TOKEN" '{"title":"Wrong content type","description":"desc"}' \
    -H "Content-Type: text/plain"
assert_in "JSON body sent as text/plain does not 500" "$HTTP_STATUS" "200" "400" "415"

req_raw POST "/api/v1/flyers" "$TOKEN" '{"title":"XML-ish","description":"desc"}' \
    -H "Content-Type: application/xml"
assert_in "JSON body sent as application/xml does not 500" "$HTTP_STATUS" "200" "400" "415"

summary
