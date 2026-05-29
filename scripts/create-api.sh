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

TMPL_API="$REPO_ROOT/templatereplaceme/api/src/commonMain/kotlin/com/cramsan/templatereplaceme/api/UserApi.kt"
TMPL_REQ="$REPO_ROOT/templatereplaceme/shared/src/commonMain/kotlin/com/cramsan/templatereplaceme/lib/model/network/CreateUserNetworkRequest.kt"
TMPL_RESP="$REPO_ROOT/templatereplaceme/shared/src/commonMain/kotlin/com/cramsan/templatereplaceme/lib/model/network/UserNetworkResponse.kt"

DEST_API_DIR="$REPO_ROOT/$APP/api/src/commonMain/kotlin/com/cramsan/$APP/api"
DEST_SHARED_DIR="$REPO_ROOT/$APP/shared/src/commonMain/kotlin/com/cramsan/$APP/lib/model/network"

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

apply_subs "$TMPL_API"  "$DEST_API_DIR/${NAME}Api.kt"
apply_subs "$TMPL_REQ"  "$DEST_SHARED_DIR/Create${NAME}NetworkRequest.kt"
apply_subs "$TMPL_RESP" "$DEST_SHARED_DIR/${NAME}NetworkResponse.kt"

echo "Created:"
echo "  $DEST_API_DIR/${NAME}Api.kt"
echo "  $DEST_SHARED_DIR/Create${NAME}NetworkRequest.kt"
echo "  $DEST_SHARED_DIR/${NAME}NetworkResponse.kt"
