#!/usr/bin/env bash
# Scenario 35: Role-escalation probes. The server resolves the caller's role by looking up
# user_profiles server-side (FlyerBoardContextRetriever) rather than trusting anything in the
# request, so none of these client-side tricks should work. This test exists to positively
# confirm that invariant holds, not because any of these are expected to succeed.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "35: Role escalation probes"

TOKEN=$(mint_token "scenario35-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Regular","last_name":"ThirtyFive"}'

# A naive implementation might trust a client-supplied role header.
req_raw GET "/api/v1/moderation" "$TOKEN" "" -H "X-User-Role: admin"
assert_eq "X-User-Role: admin header does not grant moderation access" "403" "$HTTP_STATUS"

req_raw GET "/api/v1/moderation" "$TOKEN" "" -H "X-Role: admin" -H "X-Is-Admin: true"
assert_eq "other role-ish headers do not grant moderation access either" "403" "$HTTP_STATUS"

# A naive implementation might trust a "role" field embedded in the request body.
req POST "/api/v1/moderation/some-id" "$TOKEN" '{"action":"approve","role":"admin"}'
assert_in "an extra 'role' field in the body doesn't bypass the admin check (400 unknown-field or 403 forbidden, never a silent approve)" \
    "$HTTP_STATUS" "400" "403"

# Directly promoting via the user-creation endpoint (there is no role field on that request type,
# so this only proves CreateUserNetworkRequest genuinely has no role field to smuggle a value
# through -- expect 403 because the profile was already created above).
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Regular","last_name":"ThirtyFive","role":"admin"}'
assert_in "role field on user-creation is either unknown-field-rejected or ignored (never grants admin)" \
    "$HTTP_STATUS" "400" "403"

req GET "/api/v1/user/me" "$TOKEN"
ROLE=$(echo "$RESP_BODY" | jq -r '.role')
assert_eq "caller's role is still 'user' after all escalation attempts" "user" "$ROLE"

summary
