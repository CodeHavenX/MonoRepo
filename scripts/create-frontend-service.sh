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

to_pascal() {
    echo "$1" | sed 's/-/ /g' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)} 1' | tr -d ' '
}
APP_PASCAL=$(to_pascal "$APP")
NAME_LOWER=$(echo "$NAME" | awk '{print tolower(substr($0,1,1)) substr($0,2)}')

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

TMPL_IFACE="$REPO_ROOT/templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/service/UserService.kt"
TMPL_IMPL="$REPO_ROOT/templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/service/impl/UserServiceImpl.kt"

DEST_DIR="$REPO_ROOT/$APP/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$APP/client/lib/service"
DEST_IMPL_DIR="$DEST_DIR/impl"

apply_subs() {
    local src="$1" dst="$2"
    mkdir -p "$(dirname "$dst")"
    cp "$src" "$dst"
    sed -i \
        -e "s/TemplateReplaceMe/$APP_PASCAL/g" \
        -e "s/templatereplaceme/$APP/g" \
        -e "s/User/$NAME/g" \
        -e "s/user/$NAME_LOWER/g" \
        "$dst"
}

apply_subs "$TMPL_IFACE" "$DEST_DIR/${NAME}Service.kt"
apply_subs "$TMPL_IMPL"  "$DEST_IMPL_DIR/${NAME}ServiceImpl.kt"

echo "Created:"
echo "  $DEST_DIR/${NAME}Service.kt"
echo "  $DEST_IMPL_DIR/${NAME}ServiceImpl.kt"
echo ""
echo "# Add to $APP/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$APP/client/lib/di/ServiceModule.kt"
echo "singleOf(::${NAME}ServiceImpl) { bind<${NAME}Service>() }"
echo ""
echo "# Note: No jvmTest template exists for frontend services. Write tests manually in:"
echo "#   $APP/front-end/shared-app/src/jvmTest/kotlin/com/cramsan/$APP/client/lib/service/impl/${NAME}ServiceImplTest.kt"
