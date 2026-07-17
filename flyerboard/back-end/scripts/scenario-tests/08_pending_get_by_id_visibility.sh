#!/usr/bin/env bash
# Scenario 8: GET /flyers/{id} on a pending flyer -- anonymous and owner get 404, admin gets 200.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "08: Pending flyer direct-fetch visibility by role"

OWNER_EMAIL="scenario08owner-$$@dev.local"
OWNER_TOKEN=$(mint_token "$OWNER_EMAIL" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"Eight"}'

req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario08 Flyer","description":"desc","expires_at":null}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

req GET "/api/v1/flyers/${FLYER_ID}" ""
assert_eq "anonymous GET of pending flyer returns 404" "404" "$HTTP_STATUS"

req GET "/api/v1/flyers/${FLYER_ID}" "$OWNER_TOKEN"
assert_eq "owner GET of their own pending flyer returns 404 (per spec, only admin bypasses status gate)" "404" "$HTTP_STATUS"

ADMIN_EMAIL="scenario08admin-$$@dev.local"
ADMIN_TOKEN=$(mint_token "$ADMIN_EMAIL" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Eight"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

req GET "/api/v1/flyers/${FLYER_ID}" "$ADMIN_TOKEN"
assert_eq "admin GET of pending flyer returns 200" "200" "$HTTP_STATUS"

summary
