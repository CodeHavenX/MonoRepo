#!/usr/bin/env bash
# Scenario 28: Applying the same moderation action twice -- approve an already-approved flyer,
# reject an already-rejected flyer.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "28: Moderation idempotency (approve-approved, reject-rejected)"

ADMIN_TOKEN=$(mint_token "scenario28admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"TwentyEight"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

OWNER_TOKEN=$(mint_token "scenario28owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"TwentyEight"}'

# approve -> approve again
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario28 Approve Twice","description":"desc"}'
FLYER_A=$(echo "$RESP_BODY" | jq -r '.flyer.id')
req POST "/api/v1/moderation/${FLYER_A}" "$ADMIN_TOKEN" '{"action":"approve"}'
assert_eq "first approve returns 200" "200" "$HTTP_STATUS"
req POST "/api/v1/moderation/${FLYER_A}" "$ADMIN_TOKEN" '{"action":"approve"}'
assert_eq "re-approving an already-approved flyer returns 200 (idempotent), not an error" "200" "$HTTP_STATUS"
STATUS_A=$(echo "$RESP_BODY" | jq -r '.status')
assert_eq "flyer remains APPROVED" "approved" "$STATUS_A"

# reject -> reject again
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario28 Reject Twice","description":"desc"}'
FLYER_B=$(echo "$RESP_BODY" | jq -r '.flyer.id')
req POST "/api/v1/moderation/${FLYER_B}" "$ADMIN_TOKEN" '{"action":"reject","reason":"first reason"}'
assert_eq "first reject returns 200" "200" "$HTTP_STATUS"
req POST "/api/v1/moderation/${FLYER_B}" "$ADMIN_TOKEN" '{"action":"reject","reason":"second reason"}'
assert_eq "re-rejecting an already-rejected flyer returns 200 (idempotent), not an error" "200" "$HTTP_STATUS"
STATUS_B=$(echo "$RESP_BODY" | jq -r '.status')
REASON_B=$(echo "$RESP_BODY" | jq -r '.rejection_reason')
assert_eq "flyer remains REJECTED" "rejected" "$STATUS_B"
assert_eq "the second rejection reason overwrites the first" "second reason" "$REASON_B"

summary
