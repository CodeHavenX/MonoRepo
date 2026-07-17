#!/usr/bin/env bash
# Scenario 12: Admin rejects a flyer with a reason; reason is surfaced back on the flyer.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "12: Admin rejects a flyer with a reason"

OWNER_EMAIL="scenario12owner-$$@dev.local"
OWNER_TOKEN=$(mint_token "$OWNER_EMAIL" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"Twelve"}'

req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario12 Flyer","description":"desc","expires_at":null}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

ADMIN_EMAIL="scenario12admin-$$@dev.local"
ADMIN_TOKEN=$(mint_token "$ADMIN_EMAIL" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Twelve"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"reject","reason":"Inappropriate content"}'
assert_eq "admin reject action returns 200" "200" "$HTTP_STATUS"
NEW_STATUS=$(echo "$RESP_BODY" | jq -r '.status // empty')
REASON=$(echo "$RESP_BODY" | jq -r '.rejection_reason // empty')
assert_eq "flyer status becomes rejected" "rejected" "$NEW_STATUS"
assert_eq "rejection reason is stored" "Inappropriate content" "$REASON"

req GET "/api/v1/flyers/${FLYER_ID}" ""
assert_eq "rejected flyer not publicly fetchable by id (anonymous)" "404" "$HTTP_STATUS"

req GET "/api/v1/flyers?q=Scenario12" ""
FOUND=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "rejected flyer excluded from public listing" "0" "$FOUND"

summary
