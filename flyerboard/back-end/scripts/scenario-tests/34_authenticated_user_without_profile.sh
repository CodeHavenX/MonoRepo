#!/usr/bin/env bash
# Scenario 34: A caller with a valid Supabase auth token who never completed signup (no
# user_profiles row). Per FlyerBoardContextRetriever's own docstring, this is a deliberately
# supported path: "the caller is treated as having the USER role for this request; no profile
# row is created." So every USER-role-accessible endpoint must handle a profile-less caller
# gracefully -- most interestingly, creating a flyer, since flyers.uploader_id has a foreign-key
# constraint against user_profiles(id).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "34: Authenticated user who never completed signup (no profile row)"

# Never call POST /api/v1/user for this one -- that's the point.
TOKEN=$(mint_token "scenario34-$$@dev.local" --create)

req GET "/api/v1/user/me" "$TOKEN"
assert_eq "GET /user/me for a profile-less user returns 404" "404" "$HTTP_STATUS"

req GET "/api/v1/flyers/mine" "$TOKEN"
assert_eq "GET /flyers/mine for a profile-less user returns 200 with an empty list, not an error" "200" "$HTTP_STATUS"
MINE_COUNT=$(echo "$RESP_BODY" | jq '.flyers | length')
assert_eq "empty list for a profile-less user" "0" "$MINE_COUNT"

req POST "/api/v1/flyers" "$TOKEN" '{"title":"Orphan Flyer","description":"desc"}'
assert_in "creating a flyer without a profile row does not 500 (should be 400/403/404, not a raw FK-violation crash)" \
    "$HTTP_STATUS" "400" "403" "404"

# Also cover a user_profiles row that existed at token-issue time but was deleted out-of-band
# afterwards (e.g. an admin removed the role/authorization record). Note this is deliberately
# NOT the same as "the user was fully deleted": `users` (display name) and `user_profiles`
# (role/authorization, and the FK target for flyers.uploader_id) are two separate tables, so
# deleting only user_profiles leaves GET /user/me working (it's backed by `users`) while still
# breaking anything that depends on the user_profiles row existing.
create_user_with_profile "scenario34b-$$@dev.local" "Deleted" "Later"
DELETED_TOKEN="$TOKEN"
DELETED_UUID="$USER_UUID"
db_exec "DELETE FROM public.user_profiles WHERE id = '${DELETED_UUID}';"

req GET "/api/v1/user/me" "$DELETED_TOKEN"
assert_eq "GET /user/me still succeeds (backed by the separate users table, not user_profiles)" "200" "$HTTP_STATUS"
ROLE_AFTER_DELETE=$(echo "$RESP_BODY" | jq -r '.role')
assert_eq "role falls back to 'user' once the profile row is gone" "user" "$ROLE_AFTER_DELETE"

req POST "/api/v1/flyers" "$DELETED_TOKEN" '{"title":"Orphan Flyer 2","description":"desc"}'
assert_in "creating a flyer right after profile deletion does not 500" "$HTTP_STATUS" "400" "403" "404"

summary
