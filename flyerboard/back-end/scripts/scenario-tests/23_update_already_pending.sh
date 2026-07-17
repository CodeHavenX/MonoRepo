#!/usr/bin/env bash
# Scenario 23: Editing a flyer that's already PENDING (never got moderated yet).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "23: Update a flyer that's already PENDING"

OWNER_TOKEN=$(mint_token "scenario23owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"TwentyThree"}'
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario23 Original","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
STATUS_BEFORE=$(echo "$RESP_BODY" | jq -r '.flyer.status')
assert_eq "setup: flyer starts PENDING" "pending" "$STATUS_BEFORE"

req PUT "/api/v1/flyers/${FLYER_ID}" "$OWNER_TOKEN" '{"title":"Scenario23 Edited","request_upload":false}'
assert_eq "editing an already-pending flyer returns 200" "200" "$HTTP_STATUS"
NEW_TITLE=$(echo "$RESP_BODY" | jq -r '.flyer.title')
NEW_STATUS=$(echo "$RESP_BODY" | jq -r '.flyer.status')
assert_eq "title updated" "Scenario23 Edited" "$NEW_TITLE"
assert_eq "status remains PENDING" "pending" "$NEW_STATUS"

summary
