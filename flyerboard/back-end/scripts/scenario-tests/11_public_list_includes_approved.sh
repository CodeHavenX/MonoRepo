#!/usr/bin/env bash
# Scenario 11: An approved flyer appears in the public listing and status-filtered listing.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "11: Approved flyers appear in public listing"

OWNER_EMAIL="scenario11owner-$$@dev.local"
OWNER_TOKEN=$(mint_token "$OWNER_EMAIL" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"Eleven"}'

TITLE="Scenario11-Public-$$"
req POST "/api/v1/flyers" "$OWNER_TOKEN" "{\"title\":\"${TITLE}\",\"description\":\"desc\",\"expires_at\":null}"
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

ADMIN_EMAIL="scenario11admin-$$@dev.local"
ADMIN_TOKEN=$(mint_token "$ADMIN_EMAIL" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Eleven"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"
req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"approve","reason":null}'
assert_eq "approval succeeds" "200" "$HTTP_STATUS"

req GET "/api/v1/flyers?q=${TITLE}" ""
FOUND=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "approved flyer found via public search" "1" "$FOUND"

req GET "/api/v1/flyers?status=approved&q=${TITLE}" ""
FOUND_FILTERED=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "approved flyer found with status=approved filter" "1" "$FOUND_FILTERED"

# Anonymous callers can't filter this public endpoint down to rejected/pending flyers -- the
# server silently coerces the filter to approved, so our approved flyer still matches here.
req GET "/api/v1/flyers?status=rejected&q=${TITLE}" ""
FOUND_COERCED=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "status=rejected is coerced to approved for anonymous callers" "1" "$FOUND_COERCED"

summary
