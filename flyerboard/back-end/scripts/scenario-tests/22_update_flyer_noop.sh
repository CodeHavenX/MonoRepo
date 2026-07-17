#!/usr/bin/env bash
# Scenario 22: A no-op update -- title/description/expires_at all null, request_upload=false.
# Nothing actually changes, but per the API's documented semantics ("Any edit ... resets status
# to PENDING"), even a no-op call re-queues an APPROVED flyer for moderation.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "22: Update flyer -- no-op update still re-queues for moderation"

OWNER_TOKEN=$(mint_token "scenario22owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"TwentyTwo"}'
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario22 Flyer","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

ADMIN_TOKEN=$(mint_token "scenario22admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"TwentyTwo"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"
req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"approve"}'
assert_eq "setup: flyer approved" "200" "$HTTP_STATUS"

req PUT "/api/v1/flyers/${FLYER_ID}" "$OWNER_TOKEN" '{"request_upload":false}'
assert_eq "no-op update (all fields null) returns 200" "200" "$HTTP_STATUS"
NEW_TITLE=$(echo "$RESP_BODY" | jq -r '.flyer.title')
NEW_STATUS=$(echo "$RESP_BODY" | jq -r '.flyer.status')
assert_eq "title is unchanged by the no-op update" "Scenario22 Flyer" "$NEW_TITLE"
assert_eq "even a no-op edit resets status to PENDING (documented behavior)" "pending" "$NEW_STATUS"

summary
