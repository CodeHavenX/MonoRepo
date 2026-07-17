#!/usr/bin/env bash
# Scenario 3: A user who already has a profile tries to create it again.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "03: Duplicate profile creation"

EMAIL="scenario03-$$@dev.local"
TOKEN=$(mint_token "$EMAIL" --create)

req POST "/api/v1/user" "$TOKEN" '{"first_name":"Grace","last_name":"Hopper"}'
assert_eq "first profile creation succeeds" "200" "$HTTP_STATUS"

req POST "/api/v1/user" "$TOKEN" '{"first_name":"Grace","last_name":"Hopper"}'
if [[ "$HTTP_STATUS" == "400" || "$HTTP_STATUS" == "403" ]]; then
    ok "duplicate profile creation rejected (got $HTTP_STATUS)"
else
    bad "duplicate profile creation should be rejected, got $HTTP_STATUS body=$RESP_BODY"
fi

summary
