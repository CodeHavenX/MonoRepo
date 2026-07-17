#!/usr/bin/env bash
# Scenario 15: Malformed requests and references to non-existent resources.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "15: Validation errors and not-found handling"

EMAIL="scenario15-$$@dev.local"
TOKEN=$(mint_token "$EMAIL" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Val","last_name":"Fifteen"}'

# Missing required field ("description")
req POST "/api/v1/flyers" "$TOKEN" '{"title":"No description"}'
assert_eq "create flyer missing required field returns 400" "400" "$HTTP_STATUS"

# Fetch a flyer that doesn't exist
req GET "/api/v1/flyers/00000000-0000-0000-0000-000000000000" ""
assert_eq "get nonexistent flyer returns 404" "404" "$HTTP_STATUS"

# Update a flyer that doesn't exist
req PUT "/api/v1/flyers/00000000-0000-0000-0000-000000000000" "$TOKEN" '{"title":null,"description":null,"expires_at":null,"request_upload":false}'
if [[ "$HTTP_STATUS" == "404" || "$HTTP_STATUS" == "403" ]]; then
    ok "update nonexistent flyer returns $HTTP_STATUS (404 or 403 both indicate no writable resource)"
else
    bad "update nonexistent flyer expected 404/403, got $HTTP_STATUS body=$RESP_BODY"
fi

# Admin moderates a flyer that doesn't exist
ADMIN_EMAIL="scenario15admin-$$@dev.local"
ADMIN_TOKEN=$(mint_token "$ADMIN_EMAIL" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Fifteen"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

req POST "/api/v1/moderation/00000000-0000-0000-0000-000000000000" "$ADMIN_TOKEN" '{"action":"approve","reason":null}'
assert_eq "moderate nonexistent flyer returns 404" "404" "$HTTP_STATUS"

# Invalid moderation action enum value, against a real flyer
req POST "/api/v1/flyers" "$TOKEN" '{"title":"Scenario15 Target","description":"desc","expires_at":null}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"not_a_real_action","reason":null}'
assert_eq "invalid moderation action enum returns 400" "400" "$HTTP_STATUS"

summary
