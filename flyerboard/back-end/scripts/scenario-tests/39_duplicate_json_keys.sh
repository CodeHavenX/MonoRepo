#!/usr/bin/env bash
# Scenario 39: Duplicate top-level keys in the JSON body across a couple of endpoints (not just
# create-flyer, which scenario 21 already covers).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "39: Duplicate JSON keys"

TOKEN=$(mint_token "scenario39-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"A","first_name":"B","last_name":"ThirtyNine"}'
assert_in "duplicate keys on user creation are handled without a 500" "$HTTP_STATUS" "200" "400"

req POST "/api/v1/flyers" "$TOKEN" '{"title":"Dup","description":"one","description":"two"}'
assert_in "duplicate keys on flyer creation are handled without a 500" "$HTTP_STATUS" "200" "400"
if [[ "$HTTP_STATUS" == "200" ]]; then
    FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
    req PUT "/api/v1/flyers/${FLYER_ID}" "$TOKEN" '{"request_upload":false,"request_upload":true}'
    assert_in "duplicate keys on flyer update are handled without a 500" "$HTTP_STATUS" "200" "400"
fi

summary
