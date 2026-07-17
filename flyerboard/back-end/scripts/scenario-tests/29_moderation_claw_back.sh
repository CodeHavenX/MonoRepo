#!/usr/bin/env bash
# Scenario 29: Reversing a moderation decision -- reject a currently-APPROVED flyer (claw-back),
# and approve a currently-REJECTED flyer (un-reject).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "29: Moderation claw-back (reject-approved, approve-rejected)"

ADMIN_TOKEN=$(mint_token "scenario29admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"TwentyNine"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

OWNER_TOKEN=$(mint_token "scenario29owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"TwentyNine"}'

# approved -> reject (claw-back)
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario29 ClawBack","description":"desc"}'
FLYER_A=$(echo "$RESP_BODY" | jq -r '.flyer.id')
req POST "/api/v1/moderation/${FLYER_A}" "$ADMIN_TOKEN" '{"action":"approve"}'
assert_eq "setup: flyer approved" "200" "$HTTP_STATUS"
req POST "/api/v1/moderation/${FLYER_A}" "$ADMIN_TOKEN" '{"action":"reject","reason":"changed my mind"}'
assert_eq "rejecting a previously-approved flyer returns 200" "200" "$HTTP_STATUS"
STATUS_A=$(echo "$RESP_BODY" | jq -r '.status')
assert_eq "flyer transitions from APPROVED to REJECTED" "rejected" "$STATUS_A"
req GET "/api/v1/flyers/${FLYER_A}" ""
assert_eq "clawed-back flyer is no longer publicly visible" "404" "$HTTP_STATUS"

# rejected -> approve (un-reject)
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario29 UnReject","description":"desc"}'
FLYER_B=$(echo "$RESP_BODY" | jq -r '.flyer.id')
req POST "/api/v1/moderation/${FLYER_B}" "$ADMIN_TOKEN" '{"action":"reject","reason":"first pass"}'
assert_eq "setup: flyer rejected" "200" "$HTTP_STATUS"
req POST "/api/v1/moderation/${FLYER_B}" "$ADMIN_TOKEN" '{"action":"approve"}'
assert_eq "approving a previously-rejected flyer returns 200" "200" "$HTTP_STATUS"
STATUS_B=$(echo "$RESP_BODY" | jq -r '.status')
REASON_B=$(echo "$RESP_BODY" | jq -r '.rejection_reason')
assert_eq "flyer transitions from REJECTED to APPROVED" "approved" "$STATUS_B"
# approveFlyer never clears a stale rejection_reason (moderateFlyer's rejectionReason param
# defaults to null = "leave unchanged", same known limitation as expires_at) -- an approved
# flyer can still carry a rejection reason from a prior rejection. Documenting actual behavior.
assert_eq "approving does NOT clear a stale rejection_reason from the earlier rejection" "first pass" "$REASON_B"
req GET "/api/v1/flyers/${FLYER_B}" ""
assert_eq "un-rejected flyer is now publicly visible" "200" "$HTTP_STATUS"

summary
