#!/usr/bin/env bash
# Scenario 31: The moderation `reason` field, unlike flyer title/description, is not run through
# InputSanitizer -- ModerationService persists it verbatim. This test documents that (currently
# real) difference so it can't silently get worse (e.g. crash) or get silently "fixed" without
# anyone noticing the behavior change.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
section "31: Moderation reason field -- HTML content and length"

ADMIN_TOKEN=$(mint_token "scenario31admin-$$@dev.local" --create)
req POST "/api/v1/user" "$ADMIN_TOKEN" '{"first_name":"Admin","last_name":"ThirtyOne"}'
ADMIN_UUID=$(user_id_from_token "$ADMIN_TOKEN")
promote_admin "$ADMIN_UUID"

OWNER_TOKEN=$(mint_token "scenario31owner-$$@dev.local" --create)
req POST "/api/v1/user" "$OWNER_TOKEN" '{"first_name":"Owner","last_name":"ThirtyOne"}'
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario31 Flyer","description":"desc"}'
FLYER_ID=$(echo "$RESP_BODY" | jq -r '.flyer.id')

req POST "/api/v1/moderation/${FLYER_ID}" "$ADMIN_TOKEN" \
    '{"action":"reject","reason":"<script>alert(1)</script>Not safe for the venue"}'
assert_eq "reject with HTML-laced reason returns 200, not a 500" "200" "$HTTP_STATUS"
REASON_OUT=$(echo "$RESP_BODY" | jq -r '.rejection_reason')
assert_eq "reason is persisted verbatim, unlike title/description (not sanitized)" \
    "<script>alert(1)</script>Not safe for the venue" "$REASON_OUT"

LONG_REASON=$(printf 'r%.0s' {1..5000})
req POST "/api/v1/flyers" "$OWNER_TOKEN" '{"title":"Scenario31 Flyer 2","description":"desc"}'
FLYER_ID_2=$(echo "$RESP_BODY" | jq -r '.flyer.id')
req POST "/api/v1/moderation/${FLYER_ID_2}" "$ADMIN_TOKEN" "{\"action\":\"reject\",\"reason\":\"${LONG_REASON}\"}"
assert_eq "reject with a very long reason (5000 chars) returns 200, not a 500" "200" "$HTTP_STATUS"

summary
