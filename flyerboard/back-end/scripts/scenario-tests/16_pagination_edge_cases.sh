#!/usr/bin/env bash
# Scenario 16: Pagination edge cases on the public flyer listing -- offset past the end, zero
# limit, negative values, and an oversized limit.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "16: Pagination edge cases"

req GET "/api/v1/flyers?offset=999999&limit=20" ""
assert_eq "offset far beyond total returns 200" "200" "$HTTP_STATUS"
COUNT=$(echo "$RESP_BODY" | jq '.flyers | length')
assert_eq "offset far beyond total returns an empty page, not an error" "0" "$COUNT"

req GET "/api/v1/flyers?limit=0" ""
assert_in "limit=0 is handled without a 500" "$HTTP_STATUS" "200" "400"
if [[ "$HTTP_STATUS" == "200" ]]; then
    COUNT0=$(echo "$RESP_BODY" | jq '.flyers | length')
    assert_eq "limit=0 returns zero items when accepted" "0" "$COUNT0"
fi

req GET "/api/v1/flyers?offset=-1" ""
assert_in "negative offset is handled without a 500" "$HTTP_STATUS" "200" "400"

req GET "/api/v1/flyers?limit=-5" ""
assert_in "negative limit is handled without a 500" "$HTTP_STATUS" "200" "400"

req GET "/api/v1/flyers?limit=100000" ""
assert_in "very large limit is handled without a 500" "$HTTP_STATUS" "200" "400"

req GET "/api/v1/flyers" ""
assert_eq "omitted pagination params default cleanly" "200" "$HTTP_STATUS"
DEFAULT_LIMIT=$(echo "$RESP_BODY" | jq -r '.limit')
[[ -n "$DEFAULT_LIMIT" && "$DEFAULT_LIMIT" != "null" ]] && ok "default limit is reported ($DEFAULT_LIMIT)" || bad "no default limit reported"

summary
