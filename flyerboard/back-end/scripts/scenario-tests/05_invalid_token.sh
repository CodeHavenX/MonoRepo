#!/usr/bin/env bash
# Scenario 5: A caller presents a garbage / tampered bearer token.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "05: Invalid bearer token"

req GET "/api/v1/user/me" "not.a.valid.jwt"
assert_eq "garbage token returns 401" "401" "$HTTP_STATUS"

req GET "/api/v1/user/me" "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmYWtlIn0.deadbeef"
assert_eq "well-formed but unsigned/invalid token returns 401" "401" "$HTTP_STATUS"

summary
