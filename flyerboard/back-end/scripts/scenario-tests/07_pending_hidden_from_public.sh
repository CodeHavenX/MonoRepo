#!/usr/bin/env bash
# Scenario 7: A pending flyer shows up under "my flyers" but not in the public listing.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "07: Pending flyer visible to owner only, not public"

EMAIL="scenario07-$$@dev.local"
TOKEN=$(mint_token "$EMAIL" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Owner","last_name":"Seven"}'

TITLE="Scenario07-Unique-$$"
req POST "/api/v1/flyers" "$TOKEN" "{\"title\":\"${TITLE}\",\"description\":\"desc\",\"expires_at\":null}"
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
[[ -n "$FLYER_ID" && "$FLYER_ID" != "null" ]] && ok "flyer created with id $FLYER_ID" || bad "flyer creation did not return an id"

req GET "/api/v1/flyers/mine" "$TOKEN"
assert_eq "GET /flyers/mine returns 200" "200" "$HTTP_STATUS"
FOUND_MINE=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "pending flyer appears in owner's own list" "1" "$FOUND_MINE"

req GET "/api/v1/flyers?q=${TITLE}" ""
assert_eq "public search returns 200" "200" "$HTTP_STATUS"
FOUND_PUBLIC=$(echo "$RESP_BODY" | jq '.flyers | length')
assert_eq "pending flyer does NOT appear in public listing (no filter)" "0" "$FOUND_PUBLIC"

# Explicit bypass attempt: an anonymous caller asking for status=pending directly must still be
# denied -- this is the exact leak that was found and fixed.
req GET "/api/v1/flyers?status=pending&q=${TITLE}" ""
FOUND_BYPASS=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "pending flyer NOT returned even when status=pending is requested explicitly" "0" "$FOUND_BYPASS"

summary
