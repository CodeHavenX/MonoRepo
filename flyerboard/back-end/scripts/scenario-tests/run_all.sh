#!/usr/bin/env bash
# Runs every scenario script in this directory sequentially and prints an overall summary.
# See README.md for prerequisites.
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
chmod +x "$DIR"/*.sh 2>/dev/null

if ! curl -s -o /dev/null -w "" --max-time 3 "${BASE_URL:-http://127.0.0.1:9292}/health"; then
    echo "ERROR: Flyerboard back-end is not reachable at ${BASE_URL:-http://127.0.0.1:9292}." >&2
    echo "See README.md for how to boot it against local Supabase." >&2
    exit 1
fi

TOTAL_PASS=0
TOTAL_FAIL=0
declare -a SCENARIO_RESULTS

for script in "$DIR"/[0-9][0-9]_*.sh; do
    name=$(basename "$script")
    out=$(bash "$script" 2>&1)
    echo "$out"
    p=$(echo "$out" | grep -oP '(?<=Summary: )\d+(?= passed)')
    f=$(echo "$out" | grep -oP '\d+(?= failed)')
    p=${p:-0}
    f=${f:-0}
    TOTAL_PASS=$((TOTAL_PASS + p))
    TOTAL_FAIL=$((TOTAL_FAIL + f))
    if [[ "$f" -eq 0 ]]; then
        SCENARIO_RESULTS+=("PASS  $name  ($p checks)")
    else
        SCENARIO_RESULTS+=("FAIL  $name  ($p passed / $f failed)")
    fi
done

echo ""
echo "################################################"
echo "# Overall scenario results"
echo "################################################"
printf '%s\n' "${SCENARIO_RESULTS[@]}"
echo ""
echo "TOTAL: $TOTAL_PASS assertions passed, $TOTAL_FAIL assertions failed across ${#SCENARIO_RESULTS[@]} scenarios"

[[ "$TOTAL_FAIL" -eq 0 ]]
