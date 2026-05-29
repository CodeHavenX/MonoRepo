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

TMPL_API="$REPO_ROOT/templatereplaceme/api/src/commonMain/kotlin/com/cramsan/templatereplaceme/api/UserApi.kt"
TMPL_REQ="$REPO_ROOT/templatereplaceme/shared/src/commonMain/kotlin/com/cramsan/templatereplaceme/lib/model/network/CreateUserNetworkRequest.kt"
TMPL_RESP="$REPO_ROOT/templatereplaceme/shared/src/commonMain/kotlin/com/cramsan/templatereplaceme/lib/model/network/UserNetworkResponse.kt"
DEST_API="$REPO_ROOT/$APP/api/src/commonMain/kotlin/com/cramsan/$APP/api/${NAME}Api.kt"
DEST_REQ="$REPO_ROOT/$APP/shared/src/commonMain/kotlin/com/cramsan/$APP/lib/model/network/Create${NAME}NetworkRequest.kt"
DEST_RESP="$REPO_ROOT/$APP/shared/src/commonMain/kotlin/com/cramsan/$APP/lib/model/network/${NAME}NetworkResponse.kt"

apply_subs "$TMPL_API"  "$DEST_API"
apply_subs "$TMPL_REQ"  "$DEST_REQ"
apply_subs "$TMPL_RESP" "$DEST_RESP"

echo "Created:"
echo "  $DEST_API"
echo "  $DEST_REQ"
echo "  $DEST_RESP"
