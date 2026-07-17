#!/usr/bin/env bash
# Scenario 2: A registered (auth-only) user who never called POST /user asks for their profile.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "02: Fetch profile before completing signup"

EMAIL="scenario02-$$@dev.local"
TOKEN=$(mint_token "$EMAIL" --create)
[[ -n "$TOKEN" ]] && ok "minted auth token" || bad "failed to mint auth token"

req GET "/api/v1/user/me" "$TOKEN"
assert_eq "GET /user/me returns 404 for user with no profile" "404" "$HTTP_STATUS"

summary
