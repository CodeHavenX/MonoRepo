#!/usr/bin/env bash
# Scenario 24: The owner edits a flyer that has been ARCHIVED (simulated directly in the DB,
# since the real expiry sweep runs hourly -- see README). Does editing pull it back into the
# moderation queue?
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "24: Owner edits an ARCHIVED flyer"

OWNER_TOKEN=$(mint_token "scenario24owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"TwentyFour"}'
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario24 Flyer","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

db_exec "UPDATE public.flyers SET status = 'archived' WHERE id = '${FLYER_ID}';"

req PUT "/api/v1/flyers/${FLYER_ID}" "$OWNER_TOKEN" '{"title":"Revived Flyer","request_upload":false}'
assert_eq "owner can edit their own archived flyer" "200" "$HTTP_STATUS"
NEW_STATUS=$(echo "$RESP_BODY" | jq -r '.flyer.status')
assert_eq "editing an archived flyer re-queues it as PENDING" "pending" "$NEW_STATUS"

summary
