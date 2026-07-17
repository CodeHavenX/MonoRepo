#!/usr/bin/env bash
# Scenario 30: An admin attempts to moderate a flyer that's already ARCHIVED (simulated directly
# in the DB, since the real expiry sweep runs hourly -- see README).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "30: Moderating an ARCHIVED flyer"

ADMIN_TOKEN=$(mint_token "scenario30admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Thirty"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

OWNER_TOKEN=$(mint_token "scenario30owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"Thirty"}'
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario30 Flyer","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
db_exec "UPDATE public.flyers SET status = 'archived' WHERE id = '${FLYER_ID}';"

req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"approve"}'
assert_eq "admin can approve an archived flyer (pulls it back to APPROVED) without a 500" "200" "$HTTP_STATUS"
STATUS_AFTER=$(echo "$RESP_BODY" | jq -r '.status')
assert_eq "flyer transitions from ARCHIVED to APPROVED" "approved" "$STATUS_AFTER"

summary
