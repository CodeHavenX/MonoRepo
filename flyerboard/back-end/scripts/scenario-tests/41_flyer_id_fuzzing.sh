#!/usr/bin/env bash
# Scenario 41: Path-parameter fuzzing on {flyerId} -- non-UUID strings, very long values, unicode,
# and URL-encoded path-traversal-shaped values. Flyer ids are plain strings server-side (no
# format validation visible in the OpenAPI schema), so the bar is "never 500, never leak another
# record" rather than a specific status code.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "41: Flyer id path-param fuzzing"

req GET "/api/v1/flyers/not-a-uuid" ""
assert_in "non-UUID flyer id returns a clean 4xx, not a 500" "$HTTP_STATUS" "400" "404"

LONG_ID=$(printf 'a%.0s' {1..5000})
req GET "/api/v1/flyers/${LONG_ID}" ""
assert_in "an extremely long flyer id does not 500" "$HTTP_STATUS" "400" "404" "414"

req GET "/api/v1/flyers/%E2%9C%93unicode" ""
assert_in "a unicode flyer id does not 500" "$HTTP_STATUS" "400" "404"

req GET "/api/v1/flyers/..%2F..%2Fetc%2Fpasswd" ""
assert_in "a path-traversal-shaped flyer id does not 500 and does not serve a file" "$HTTP_STATUS" "400" "404"

req GET "/api/v1/flyers/00000000-0000-0000-0000-000000000000" ""
assert_eq "a well-formed but nonexistent UUID returns a plain 404" "404" "$HTTP_STATUS"

# UUID with the wrong case should still resolve identically to the canonical form, since
# Postgres uuid comparison is case-insensitive. Use an admin token so the flyer's PENDING status
# (owner can't see their own pending flyer by id -- see scenario 08) doesn't confound this check.
ADMIN_TOKEN=$(mint_token "scenario41admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"FortyOne"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"
OWNER_TOKEN=$(mint_token "scenario41owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Fuzz","last_name":"FortyOne"}'
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario41 Flyer","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
UPPER_ID=$(echo "$FLYER_ID" | tr '[:lower:]' '[:upper:]')
req GET "/api/v1/flyers/${UPPER_ID}" "$ADMIN_TOKEN"
assert_eq "uppercased UUID resolves the same flyer" "200" "$HTTP_STATUS"

summary
