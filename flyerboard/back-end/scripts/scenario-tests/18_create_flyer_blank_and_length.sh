#!/usr/bin/env bash
# Scenario 18: Creating a flyer with blank/whitespace-only fields, and fields far exceeding the
# server's sanitizer max length (InputSanitizer truncates rather than rejects).
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "18: Create flyer -- blank fields and oversized fields"

TOKEN=$(mint_token "scenario18-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Blank","last_name":"Eighteen"}'

req POST "/api/v1/flyers" "$TOKEN" '{"title":"   ","description":"   "}'
assert_in "blank/whitespace-only title+description is handled without a 500" "$HTTP_STATUS" "200" "400"
if [[ "$HTTP_STATUS" == "200" ]]; then
    BLANK_TITLE=$(echo "$RESP_BODY" | jq -r '.flyer.title')
    assert_eq "whitespace-only title is sanitized to an empty string" "" "$BLANK_TITLE"
fi

LONG_TITLE=$(printf 'a%.0s' {1..500})
LONG_DESC=$(python3 -c "print('b' * 5000)" 2>/dev/null || printf 'b%.0s' {1..5000})
req POST "/api/v1/flyers" "$TOKEN" "{\"title\":\"${LONG_TITLE}\",\"description\":\"${LONG_DESC}\"}"
assert_eq "oversized title/description does not 500" "200" "$HTTP_STATUS"
RETURNED_TITLE_LEN=$(echo "$RESP_BODY" | jq -r '.flyer.title | length')
RETURNED_DESC_LEN=$(echo "$RESP_BODY" | jq -r '.flyer.description | length')
[[ "$RETURNED_TITLE_LEN" -le 200 ]] && ok "title truncated to <= 200 chars (got $RETURNED_TITLE_LEN)" || bad "title not truncated: $RETURNED_TITLE_LEN chars"
[[ "$RETURNED_DESC_LEN" -le 2000 ]] && ok "description truncated to <= 2000 chars (got $RETURNED_DESC_LEN)" || bad "description not truncated: $RETURNED_DESC_LEN chars"

summary
