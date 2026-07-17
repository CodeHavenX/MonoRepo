#!/usr/bin/env bash
# Scenario 17: Search-query (`q`) edge cases on the public flyer listing -- empty string,
# whitespace-only, SQL-metacharacter probes, and case-insensitivity.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "17: Search query edge cases"

OWNER_TOKEN=$(mint_token "scenario17owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"Seventeen"}'
TITLE="Scenario17-CaseTest-$$"
req POST "/api/v1/flyers" "$OWNER_TOKEN" "{\"title\":\"${TITLE}\",\"description\":\"desc\"}"
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')
ADMIN_TOKEN=$(mint_token "scenario17admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"Seventeen"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"
req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" '{"action":"approve"}'
assert_eq "setup: flyer approved" "200" "$HTTP_STATUS"

req GET "/api/v1/flyers?q=" ""
assert_eq "empty q= string is handled without a 500" "200" "$HTTP_STATUS"

req GET "/api/v1/flyers?q=%20%20%20" ""
assert_eq "whitespace-only query is handled without a 500" "200" "$HTTP_STATUS"

LOWER_TITLE=$(echo "$TITLE" | tr '[:upper:]' '[:lower:]')
req GET "/api/v1/flyers?q=${LOWER_TITLE}" ""
assert_eq "lowercased search query returns 200" "200" "$HTTP_STATUS"
FOUND_CI=$(echo "$RESP_BODY" | jq --arg id "$FLYER_ID" '[.flyers[] | select(.id == $id)] | length')
assert_eq "search is case-insensitive" "1" "$FOUND_CI"

req GET "/api/v1/flyers?q=NoSuchFlyerTitleAnywhere-$$" ""
assert_eq "query matching nothing returns 200" "200" "$HTTP_STATUS"
NONE=$(echo "$RESP_BODY" | jq '.flyers | length')
assert_eq "query matching nothing returns an empty list" "0" "$NONE"

# SQL-metacharacter probe against the underlying `ilike` filter. This must never 500 and must
# never return unrelated rows (i.e. the wildcard characters are treated as literal search text,
# not passed through unescaped).
req GET "/api/v1/flyers?q=%25" ""
assert_eq "a bare '%' wildcard in the query is handled without a 500" "200" "$HTTP_STATUS"

req GET "/api/v1/flyers?q='%20OR%20'1'='1" ""
assert_eq "a SQL-injection-shaped query is handled without a 500" "200" "$HTTP_STATUS"
INJECTION_RESULTS=$(echo "$RESP_BODY" | jq '.flyers | length')
assert_eq "SQL-injection-shaped query returns no rows (treated as literal text)" "0" "$INJECTION_RESULTS"

summary
