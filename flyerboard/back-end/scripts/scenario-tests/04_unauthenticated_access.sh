#!/usr/bin/env bash
# Scenario 4: An anonymous caller (no token) hits endpoints that require auth.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "04: Unauthenticated access to protected endpoints"

req GET "/api/v1/user/me" ""
assert_eq "GET /user/me with no token returns 401" "401" "$HTTP_STATUS"

req POST "/api/v1/flyers" "" '{"title":"t","description":"d"}'
assert_eq "POST /flyers with no token returns 401" "401" "$HTTP_STATUS"

req GET "/api/v1/flyers/mine" ""
assert_eq "GET /flyers/mine with no token returns 401" "401" "$HTTP_STATUS"

req GET "/api/v1/moderation" ""
assert_eq "GET /moderation with no token returns 401" "401" "$HTTP_STATUS"

# Public flyer listing should NOT require auth.
req GET "/api/v1/flyers" ""
assert_eq "GET /flyers (public) with no token returns 200" "200" "$HTTP_STATUS"

summary
