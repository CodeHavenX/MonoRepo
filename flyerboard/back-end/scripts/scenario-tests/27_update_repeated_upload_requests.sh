#!/usr/bin/env bash
# Scenario 27: Requesting a fresh upload URL twice in a row. Each call should succeed and return
# a signed URL (signed URLs are typically time-limited/tokenized, so we just check both calls
# succeed rather than asserting the URLs differ byte-for-byte).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "27: Repeated request_upload=true calls"

TOKEN=$(mint_token "scenario27-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Upload","last_name":"TwentySeven"}'
req POST "/api/v1/flyers" "$TOKEN" '{"title":"Scenario27 Flyer","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

req PUT "/api/v1/flyers/${FLYER_ID}" "$TOKEN" '{"request_upload":true}'
assert_eq "first request_upload=true returns 200" "200" "$HTTP_STATUS"
URL_1=$(echo "$RESP_BODY" | jq -r '.upload.signed_url // empty')
[[ -n "$URL_1" ]] && ok "first call returns a signed URL" || bad "first call returned no signed URL"

req PUT "/api/v1/flyers/${FLYER_ID}" "$TOKEN" '{"request_upload":true}'
assert_eq "second request_upload=true returns 200" "200" "$HTTP_STATUS"
URL_2=$(echo "$RESP_BODY" | jq -r '.upload.signed_url // empty')
[[ -n "$URL_2" ]] && ok "second call returns a signed URL" || bad "second call returned no signed URL"

req PUT "/api/v1/flyers/${FLYER_ID}" "$TOKEN" '{"request_upload":false}'
assert_eq "a subsequent update with request_upload=false returns 200" "200" "$HTTP_STATUS"
UPLOAD_FIELD=$(echo "$RESP_BODY" | jq -r '.upload')
assert_eq "no upload object is returned when request_upload=false" "null" "$UPLOAD_FIELD"

summary
