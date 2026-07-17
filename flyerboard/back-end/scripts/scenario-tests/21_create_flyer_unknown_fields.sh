#!/usr/bin/env bash
# Scenario 21: Extra/unknown fields in the create-flyer JSON body. kotlinx.serialization's Json
# instance does not set ignoreUnknownKeys, so this is expected to be rejected -- the important
# thing (given the earlier 500-on-malformed-body bug) is that it's a clean 400, not a 500.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "21: Create flyer -- unknown JSON fields"

TOKEN=$(mint_token "scenario21-$$@dev.local" --create)
req POST "/api/v1/user" "$TOKEN" '{"first_name":"Extra","last_name":"TwentyOne"}'

req POST "/api/v1/flyers" "$TOKEN" \
    '{"title":"Has extra field","description":"desc","totally_unexpected_field":"surprise"}'
assert_in "unknown field in body is handled without a 500" "$HTTP_STATUS" "200" "400"

# Duplicate keys: last one should win per JSON semantics, or it's cleanly rejected -- either way,
# never a 500.
req POST "/api/v1/flyers" "$TOKEN" '{"title":"First","title":"Second","description":"desc"}'
assert_in "duplicate JSON keys are handled without a 500" "$HTTP_STATUS" "200" "400"

summary
