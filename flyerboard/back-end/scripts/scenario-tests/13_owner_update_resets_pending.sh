#!/usr/bin/env bash
# Scenario 13: Owner edits their approved flyer; edit re-queues it for moderation (status -> pending).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "13: Owner edit resets flyer to pending"

OWNER_EMAIL="scenario13owner-$$@dev.local"
OWNER_TOKEN=$(mint_token "$OWNER_EMAIL" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"Thirteen"}'

req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario13 Original","description":"desc","expires_at":null}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

ADMIN_EMAIL="scenario13admin-$$@dev.local"
ADMIN_TOKEN=$(mint_token "$ADMIN_EMAIL" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Thirteen"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"
req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"approve","reason":null}'
assert_eq "flyer approved before edit" "200" "$HTTP_STATUS"

req PUT "/api/v1/flyers/${FLYER_ID}" "$OWNER_TOKEN" '{"title":"Scenario13 Edited","description":"Scenario13 desc","expires_at":null,"request_upload":false}'
assert_eq "owner update returns 200" "200" "$HTTP_STATUS"
NEW_TITLE=$(echo "$RESP_BODY" | jq -r '.flyer.title // empty')
NEW_STATUS=$(echo "$RESP_BODY" | jq -r '.flyer.status // empty')
assert_eq "title was updated" "Scenario13 Edited" "$NEW_TITLE"
assert_eq "edit resets status to pending" "pending" "$NEW_STATUS"

summary
