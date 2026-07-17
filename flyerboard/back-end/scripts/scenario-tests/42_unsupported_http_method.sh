#!/usr/bin/env bash
# Scenario 42: HTTP methods that aren't defined on real routes.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "42: Unsupported HTTP methods on real routes"

TOKEN=$(mint_token "scenario42-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Method","last_name":"FortyTwo"}'
req POST "/api/v1/flyers" "$TOKEN" '{"title":"Scenario42 Flyer","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

req_raw DELETE "/api/v1/flyers/${FLYER_ID}" "$TOKEN"
assert_in "DELETE on a flyer (undefined route) is a clean 404/405, not a 500" "$HTTP_STATUS" "404" "405"

req_raw PATCH "/api/v1/flyers/${FLYER_ID}" "$TOKEN" '{"title":"patched"}'
assert_in "PATCH on a flyer (undefined route) is a clean 404/405, not a 500" "$HTTP_STATUS" "404" "405"

req_raw POST "/api/v1/user/me" "$TOKEN"
assert_in "POST on a GET-only route is a clean 404/405, not a 500" "$HTTP_STATUS" "404" "405"

req_raw GET "/api/v1/nonexistent-route" "$TOKEN"
assert_eq "a route that doesn't exist at all returns 404" "404" "$HTTP_STATUS"

summary
