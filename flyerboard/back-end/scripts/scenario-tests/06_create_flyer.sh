#!/usr/bin/env bash
# Scenario 6: A signed-up user creates a flyer and receives a signed upload URL.
# Deliberately omits the optional expires_at field, per spec, to guard against regressing the
# fixed "omitted optional field crashes the server" bug.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "06: Create a flyer"

EMAIL="scenario06-$$@dev.local"
TOKEN=$(mint_token "$EMAIL" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Uploader","last_name":"Six"}'
assert_eq "profile setup ok" "200" "$HTTP_STATUS"

req POST "/api/v1/flyers" "$TOKEN" '{"title":"Live Music Night","description":"Join us for live music."}'
assert_eq "create flyer returns 200" "200" "$HTTP_STATUS"
STATUS=$(echo "$RESP_BODY" | jq -r '.flyer.status // empty')
SIGNED_URL=$(echo "$RESP_BODY" | jq -r '.upload.signed_url // empty')
assert_eq "new flyer starts in PENDING status" "pending" "$STATUS"
[[ -n "$SIGNED_URL" && "$SIGNED_URL" != "null" ]] && ok "response includes a signed upload URL" || bad "expected a signed upload URL, got: $RESP_BODY"

summary
