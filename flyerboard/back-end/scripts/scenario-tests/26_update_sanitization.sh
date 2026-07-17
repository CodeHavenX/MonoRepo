#!/usr/bin/env bash
# Scenario 26: Update flyer applies the same HTML-tag-stripping sanitization as create.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "26: Update flyer -- sanitization applies to edits too"

TOKEN=$(mint_token "scenario26-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Xss","last_name":"TwentySix"}'
req POST "/api/v1/flyers" "$TOKEN" '{"title":"Original","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

req PUT "/api/v1/flyers/${FLYER_ID}" "$TOKEN" \
    '{"title":"<b>Bold</b> Title","description":"<script>evil()</script>Safe","request_upload":false}'
assert_eq "update with HTML content returns 200" "200" "$HTTP_STATUS"
TITLE_OUT=$(echo "$RESP_BODY" | jq -r '.flyer.title')
DESC_OUT=$(echo "$RESP_BODY" | jq -r '.flyer.description')
assert_eq "bold tag stripped from updated title" "Bold Title" "$TITLE_OUT"
assert_eq "script tag stripped from updated description" "evil()Safe" "$DESC_OUT"

summary
