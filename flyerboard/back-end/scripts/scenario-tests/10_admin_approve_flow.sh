#!/usr/bin/env bash
# Scenario 10: Full moderation happy path -- admin sees the pending flyer in the queue and approves it.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "10: Admin approves a pending flyer"

OWNER_EMAIL="scenario10owner-$$@dev.local"
OWNER_TOKEN=$(mint_token "$OWNER_EMAIL" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"Ten"}'

TITLE="Scenario10-Approve-$$"
req POST "/api/v1/flyers" "$OWNER_TOKEN" "{\"title\":\"${TITLE}\",\"description\":\"desc\",\"expires_at\":null}"
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
ok "flyer $FLYER_ID created pending moderation"

ADMIN_EMAIL="scenario10admin-$$@dev.local"
ADMIN_TOKEN=$(mint_token "$ADMIN_EMAIL" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Ten"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

req GET "/api/v1/moderation?limit=100" "$ADMIN_TOKEN"
assert_eq "admin listing moderation queue returns 200" "200" "$HTTP_STATUS"
IN_QUEUE=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "new flyer appears in moderation queue" "1" "$IN_QUEUE"

req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"approve","reason":null}'
assert_eq "admin approve action returns 200" "200" "$HTTP_STATUS"
NEW_STATUS=$(echo "$RESP_BODY" | jq -r '.status // empty')
assert_eq "flyer status becomes approved" "approved" "$NEW_STATUS"

req GET "/api/v1/flyers/${FLYER_ID}" ""
assert_eq "approved flyer is now publicly fetchable" "200" "$HTTP_STATUS"

summary
