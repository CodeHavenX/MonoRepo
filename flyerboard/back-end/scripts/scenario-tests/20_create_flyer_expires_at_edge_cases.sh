#!/usr/bin/env bash
# Scenario 20: Creating a flyer with a malformed expires_at, and with an expires_at already in
# the past.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "20: Create flyer -- expires_at edge cases"

TOKEN=$(mint_token "scenario20-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Expiry","last_name":"Twenty"}'

req POST "/api/v1/flyers" "$TOKEN" '{"title":"Bad expiry","description":"desc","expires_at":"not-a-date"}'
assert_eq "malformed expires_at returns 400, not 500" "400" "$HTTP_STATUS"

req POST "/api/v1/flyers" "$TOKEN" '{"title":"Bad expiry number","description":"desc","expires_at":12345}'
assert_eq "non-string expires_at returns 400, not 500" "400" "$HTTP_STATUS"

req POST "/api/v1/flyers" "$TOKEN" '{"title":"Past expiry","description":"desc","expires_at":"2020-01-01T00:00:00Z"}'
assert_eq "expires_at already in the past is accepted (creation itself succeeds)" "200" "$HTTP_STATUS"
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
FLYER_STATUS=$(echo "$RESP_BODY" | jq -r '.flyer.status')
assert_eq "flyer with past expires_at still starts PENDING (expiry sweep doesn't touch pending flyers)" "pending" "$FLYER_STATUS"
[[ -n "$FLYER_ID" && "$FLYER_ID" != "null" ]] && ok "flyer with past expires_at was created ($FLYER_ID)" || bad "flyer creation with past expires_at failed unexpectedly"

req POST "/api/v1/flyers" "$TOKEN" '{"title":"Future expiry","description":"desc","expires_at":"2099-01-01T00:00:00Z"}'
assert_eq "far-future expires_at is accepted" "200" "$HTTP_STATUS"

summary
