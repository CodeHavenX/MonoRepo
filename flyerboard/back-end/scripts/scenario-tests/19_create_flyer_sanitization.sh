#!/usr/bin/env bash
# Scenario 19: Creating a flyer with HTML/script content in title/description (XSS probe), and
# with unicode/emoji content.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "19: Create flyer -- HTML sanitization and unicode"

TOKEN=$(mint_token "scenario19-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Xss","last_name":"Nineteen"}'

req POST "/api/v1/flyers" "$TOKEN" \
    '{"title":"<script>alert(1)</script>Hello","description":"<img src=x onerror=alert(1)>World"}'
assert_eq "HTML-laced create returns 200" "200" "$HTTP_STATUS"
TITLE_OUT=$(echo "$RESP_BODY" | jq -r '.flyer.title')
DESC_OUT=$(echo "$RESP_BODY" | jq -r '.flyer.description')
# InputSanitizer strips HTML *tags* (the `<...>` markup) but deliberately leaves any enclosed
# text behind -- "<script>alert(1)</script>" becomes the plain string "alert(1)", not "".
assert_eq "script tags stripped, enclosed text left as inert plain text" "alert(1)Hello" "$TITLE_OUT"
assert_eq "img tag stripped from description" "World" "$DESC_OUT"
[[ "$TITLE_OUT" != *"<"* ]] && ok "no raw angle brackets survive in title" || bad "title still contains a tag: $TITLE_OUT"

req POST "/api/v1/flyers" "$TOKEN" '{"title":"🎉 Fiesta Ünïcödé 日本語","description":"emoji desc 🚀"}'
assert_eq "unicode/emoji title+description returns 200" "200" "$HTTP_STATUS"
UNICODE_TITLE=$(echo "$RESP_BODY" | jq -r '.flyer.title')
assert_eq "unicode content preserved" "🎉 Fiesta Ünïcödé 日本語" "$UNICODE_TITLE"

summary
