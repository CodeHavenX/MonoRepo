#!/usr/bin/env bash
# Scenario 32: The `action` field's case sensitivity. The spec's enum is lowercase
# ("approve"/"reject"); the controller does a literal string match, so any other casing should
# behave like an unrecognized action (400), not be silently accepted.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "32: Moderation action case sensitivity"

ADMIN_TOKEN=$(mint_token "scenario32admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"ThirtyTwo"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

OWNER_TOKEN=$(mint_token "scenario32owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"ThirtyTwo"}'
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario32 Flyer","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"Approve"}'
assert_eq "'Approve' (capitalized) is rejected as an invalid action" "400" "$HTTP_STATUS"

req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"APPROVE"}'
assert_eq "'APPROVE' (uppercase) is rejected as an invalid action" "400" "$HTTP_STATUS"

req GET "/api/v1/flyers/${FLYER_ID}" "$ADMIN_TOKEN"
STATUS_UNCHANGED=$(echo "$RESP_BODY" | jq -r '.status')
assert_eq "flyer status is untouched by the rejected mis-cased actions" "pending" "$STATUS_UNCHANGED"

summary
