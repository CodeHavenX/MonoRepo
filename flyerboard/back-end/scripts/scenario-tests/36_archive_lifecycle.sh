#!/usr/bin/env bash
# Scenario 36: The approved -> archived lifecycle transition and its visibility rules. The real
# ExpiryService sweep runs once an hour (too slow for a test run), so the transition itself is
# simulated directly in the DB; this test focuses on verifying the *read* paths treat ARCHIVED
# correctly once a flyer is in that state.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "36: Archive lifecycle and visibility"

ADMIN_TOKEN=$(mint_token "scenario36admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"ThirtySix"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

OWNER_TOKEN=$(mint_token "scenario36owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"ThirtySix"}'
TITLE="Scenario36-Archive-$$"
req POST "/api/v1/flyers" "$OWNER_TOKEN" "{\"title\":\"${TITLE}\",\"description\":\"desc\",\"expires_at\":\"2020-01-01T00:00:00Z\"}"
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"approve"}'
assert_eq "setup: flyer approved with an already-past expires_at" "200" "$HTTP_STATUS"

# Simulate what ExpiryService.archiveExpiredFlyers() would do on its next hourly tick.
db_exec "UPDATE public.flyers SET status = 'archived' WHERE id = '${FLYER_ID}';"

req GET "/api/v1/flyers/${FLYER_ID}" ""
assert_eq "archived flyer is still directly gettable by anonymous callers (not admin-gated)" "200" "$HTTP_STATUS"
STATUS_DIRECT=$(echo "$RESP_BODY" | jq -r '.status')
assert_eq "status reported as archived" "archived" "$STATUS_DIRECT"

req GET "/api/v1/flyers?q=${TITLE}" ""
FOUND_DEFAULT=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "archived flyer does NOT appear in the default (approved-only) public listing" "0" "$FOUND_DEFAULT"

req GET "/api/v1/flyers/archive?q=${TITLE}" ""
assert_eq "GET /flyers/archive returns 200" "200" "$HTTP_STATUS"
FOUND_ARCHIVE=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "archived flyer DOES appear in the archive listing" "1" "$FOUND_ARCHIVE"

req GET "/api/v1/flyers/mine" "$OWNER_TOKEN"
FOUND_MINE=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "archived flyer still appears in the owner's 'my flyers' list regardless of status" "1" "$FOUND_MINE"

summary
