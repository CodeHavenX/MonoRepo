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

TMPL_IFACE="$REPO_ROOT/templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/service/UserService.kt"
TMPL_IMPL="$REPO_ROOT/templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/service/impl/UserServiceImpl.kt"
DEST_IFACE="$REPO_ROOT/$APP/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$APP/client/lib/service/${NAME}Service.kt"
DEST_IMPL="$REPO_ROOT/$APP/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$APP/client/lib/service/impl/${NAME}ServiceImpl.kt"

apply_subs "$TMPL_IFACE" "$DEST_IFACE"
apply_subs "$TMPL_IMPL"  "$DEST_IMPL"

echo "Created:"
echo "  $DEST_IFACE"
echo "  $DEST_IMPL"
echo ""
echo "# Add to $APP/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$APP/client/lib/di/ServiceModule.kt"
echo "singleOf(::${NAME}ServiceImpl) { bind<${NAME}Service>() }"
echo ""
echo "# Note: No jvmTest template exists for frontend services. Write tests manually in:"
echo "#   $APP/front-end/shared-app/src/jvmTest/kotlin/com/cramsan/$APP/client/lib/service/impl/${NAME}ServiceImplTest.kt"
