#!/usr/bin/env bash
# Scenario 14: A different user tries to edit someone else's flyer.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "14: Non-owner cannot update another user's flyer"

OWNER_EMAIL="scenario14owner-$$@dev.local"
OWNER_TOKEN=$(mint_token "$OWNER_EMAIL" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"Fourteen"}'
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario14 Flyer","description":"desc","expires_at":null}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

OTHER_EMAIL="scenario14other-$$@dev.local"
OTHER_TOKEN=$(mint_token "$OTHER_EMAIL" --create)
req POST "/api/v1/user" "$OTHER_TOKEN" '{"first_name":"Other","last_name":"Fourteen"}'

req PUT "/api/v1/flyers/${FLYER_ID}" "$OTHER_TOKEN" '{"title":"Hijacked","description":"Hijacked desc","expires_at":null,"request_upload":false}'
assert_eq "non-owner update returns 403" "403" "$HTTP_STATUS"

# Confirm the flyer was NOT modified.
ADMIN_EMAIL="scenario14admin-$$@dev.local"
ADMIN_TOKEN=$(mint_token "$ADMIN_EMAIL" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Fourteen"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"
req GET "/api/v1/flyers/${FLYER_ID}" "$ADMIN_TOKEN"
TITLE=$(echo "$RESP_BODY" | jq -r '.title // empty')
assert_eq "flyer title unchanged after forbidden update attempt" "Scenario14 Flyer" "$TITLE"

summary
