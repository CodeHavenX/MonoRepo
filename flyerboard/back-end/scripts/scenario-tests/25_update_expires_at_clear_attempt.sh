#!/usr/bin/env bash
# Scenario 25: A client that set expires_at on creation later tries to clear it back to "never
# expires" by sending expires_at: null on update. FlyerDatastore.updateFlyer is documented as
# having no way to distinguish "leave unchanged" from "clear to null" (both arrive as Kotlin
# null), so this is expected to silently NOT clear the field. This test exists to catch a
# regression either way -- if this ever starts silently clearing, or ever starts crashing.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "25: Attempt to clear expires_at back to null on update"

TOKEN=$(mint_token "scenario25-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Expiry","last_name":"TwentyFive"}'
req POST "/api/v1/flyers" "$TOKEN" '{"title":"Scenario25 Flyer","description":"desc","expires_at":"2099-06-01T00:00:00Z"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
EXPIRES_BEFORE=$(echo "$RESP_BODY" | jq -r '.flyer.expires_at')
assert_eq "setup: flyer created with an expiry" "2099-06-01T00:00:00Z" "$EXPIRES_BEFORE"

req PUT "/api/v1/flyers/${FLYER_ID}" "$TOKEN" '{"expires_at":null,"request_upload":false}'
assert_eq "update with expires_at:null returns 200" "200" "$HTTP_STATUS"
EXPIRES_AFTER=$(echo "$RESP_BODY" | jq -r '.flyer.expires_at')
assert_eq "expires_at is NOT cleared (known limitation: null is indistinguishable from omitted)" "2099-06-01T00:00:00Z" "$EXPIRES_AFTER"

summary
