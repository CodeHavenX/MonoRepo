#!/usr/bin/env bash
set -euo pipefail

NAME=""
APP=""
PROVIDER=""
while [[ $# -gt 0 ]]; do
    case $1 in
        --name)     NAME="$2";     shift 2 ;;
        --app)      APP="$2";      shift 2 ;;
        --provider) PROVIDER="$2"; shift 2 ;;
        *) echo "Unknown argument: $1" >&2; exit 1 ;;
    esac
done

if [[ -z "$NAME" || -z "$APP" || -z "$PROVIDER" ]]; then
    echo "Usage: $0 --name <PascalCaseName> --app <appname> --provider <ProviderName>" >&2
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/_common.sh"
APP_PASCAL=$(to_pascal "$APP")
NAME_LOWER=$(echo "$NAME" | awk '{print tolower(substr($0,1,1)) substr($0,2)}')

TMPL_IFACE="$REPO_ROOT/templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/datastore/UserDatastore.kt"
TMPL_IMPL="$REPO_ROOT/templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/datastore/impl/ExampleUserDatastore.kt"
TMPL_TEST="$REPO_ROOT/templatereplaceme/back-end/src/test/kotlin/com/cramsan/templatereplaceme/server/datastore/impl/UserDatastoreImplTest.kt"
DEST_IFACE="$REPO_ROOT/$APP/back-end/src/main/kotlin/com/cramsan/$APP/server/datastore/${NAME}Datastore.kt"
DEST_IMPL="$REPO_ROOT/$APP/back-end/src/main/kotlin/com/cramsan/$APP/server/datastore/impl/${PROVIDER}${NAME}Datastore.kt"
DEST_TEST="$REPO_ROOT/$APP/back-end/src/test/kotlin/com/cramsan/$APP/server/datastore/impl/${PROVIDER}${NAME}DatastoreTest.kt"

apply_subs "$TMPL_IFACE" "$DEST_IFACE"
apply_subs "$TMPL_IMPL"  "$DEST_IMPL"  -e "s/Example/$PROVIDER/g"
apply_subs "$TMPL_TEST"  "$DEST_TEST"  -e "s/Example/$PROVIDER/g"

echo "Created:"
echo "  $DEST_IFACE"
echo "  $DEST_IMPL"
echo "  $DEST_TEST"
echo ""
echo "# Add to $APP/back-end/src/main/kotlin/com/cramsan/$APP/server/dependencyinjection/DatastoreModule.kt"
echo "singleOf(::${PROVIDER}${NAME}Datastore) { bind<${NAME}Datastore>() }"
