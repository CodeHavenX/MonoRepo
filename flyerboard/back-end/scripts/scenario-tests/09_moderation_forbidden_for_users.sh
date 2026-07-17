#!/usr/bin/env bash
# Scenario 9: A regular (non-admin) user tries to access moderation endpoints.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "09: Moderation endpoints forbidden for non-admins"

EMAIL="scenario09-$$@dev.local"
TOKEN=$(mint_token "$EMAIL" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Regular","last_name":"Nine"}'

req GET "/api/v1/moderation" "$TOKEN"
assert_eq "regular user listing moderation queue returns 403" "403" "$HTTP_STATUS"

req POST "/api/v1/moderation/some-id" "$TOKEN" '{"action":"approve","reason":null}'
assert_eq "regular user moderating a flyer returns 403" "403" "$HTTP_STATUS"

summary
