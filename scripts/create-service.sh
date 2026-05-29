#!/usr/bin/env bash
set -euo pipefail

NAME=""
APP=""
while [[ $# -gt 0 ]]; do
    case $1 in
        --name) NAME="$2"; shift 2 ;;
        --app)  APP="$2";  shift 2 ;;
        *) echo "Unknown argument: $1" >&2; exit 1 ;;
    esac
done

if [[ -z "$NAME" || -z "$APP" ]]; then
    echo "Usage: $0 --name <PascalCaseName> --app <appname>" >&2
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/_common.sh"
APP_PASCAL=$(to_pascal "$APP")
NAME_LOWER=$(echo "$NAME" | awk '{print tolower(substr($0,1,1)) substr($0,2)}')

TMPL="$REPO_ROOT/templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/service/UserService.kt"
TMPL_TEST="$REPO_ROOT/templatereplaceme/back-end/src/test/kotlin/com/cramsan/templatereplaceme/server/service/UserServiceTest.kt"
DEST="$REPO_ROOT/$APP/back-end/src/main/kotlin/com/cramsan/$APP/server/service/${NAME}Service.kt"
DEST_TEST="$REPO_ROOT/$APP/back-end/src/test/kotlin/com/cramsan/$APP/server/service/${NAME}ServiceTest.kt"

apply_subs "$TMPL"      "$DEST"
apply_subs "$TMPL_TEST" "$DEST_TEST"

echo "Created:"
echo "  $DEST"
echo "  $DEST_TEST"
echo ""
echo "# Add to $APP/back-end/src/main/kotlin/com/cramsan/$APP/server/dependencyinjection/ServicesModule.kt"
echo "singleOf(::${NAME}Service)"
