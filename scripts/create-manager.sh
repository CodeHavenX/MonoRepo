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

TMPL="$REPO_ROOT/templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/managers/UserManager.kt"
DEST="$REPO_ROOT/$APP/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$APP/client/lib/managers/${NAME}Manager.kt"

apply_subs "$TMPL" "$DEST"

echo "Created:"
echo "  $DEST"
echo ""
echo "# Add to $APP/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$APP/client/lib/di/ManagerModule.kt"
echo "singleOf(::${NAME}Manager)"
echo ""
echo "# Note: No jvmTest template exists for managers. Write tests manually in:"
echo "#   $APP/front-end/shared-app/src/jvmTest/kotlin/com/cramsan/$APP/client/lib/managers/${NAME}ManagerTest.kt"
