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

to_pascal() {
    echo "$1" | sed 's/-/ /g' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)} 1' | tr -d ' '
}
APP_PASCAL=$(to_pascal "$APP")
NAME_LOWER=$(echo "$NAME" | awk '{print tolower(substr($0,1,1)) substr($0,2)}')

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

TMPL_IFACE="$REPO_ROOT/templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/datastore/UserDatastore.kt"
TMPL_IMPL="$REPO_ROOT/templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/datastore/impl/ExampleUserDatastore.kt"
TMPL_TEST="$REPO_ROOT/templatereplaceme/back-end/src/test/kotlin/com/cramsan/templatereplaceme/server/datastore/impl/UserDatastoreImplTest.kt"

DEST_DIR="$REPO_ROOT/$APP/back-end/src/main/kotlin/com/cramsan/$APP/server/datastore"
DEST_IMPL_DIR="$DEST_DIR/impl"
DEST_TEST_DIR="$REPO_ROOT/$APP/back-end/src/test/kotlin/com/cramsan/$APP/server/datastore/impl"

apply_subs() {
    local src="$1" dst="$2"
    mkdir -p "$(dirname "$dst")"
    cp "$src" "$dst"
    sed -i \
        -e "s/TemplateReplaceMe/$APP_PASCAL/g" \
        -e "s/templatereplaceme/$APP/g" \
        -e "s/Example/$PROVIDER/g" \
        -e "s/User/$NAME/g" \
        -e "s/user/$NAME_LOWER/g" \
        "$dst"
}

apply_subs "$TMPL_IFACE" "$DEST_DIR/${NAME}Datastore.kt"
apply_subs "$TMPL_IMPL"  "$DEST_IMPL_DIR/${PROVIDER}${NAME}Datastore.kt"
apply_subs "$TMPL_TEST"  "$DEST_TEST_DIR/${PROVIDER}${NAME}DatastoreTest.kt"

echo "Created:"
echo "  $DEST_DIR/${NAME}Datastore.kt"
echo "  $DEST_IMPL_DIR/${PROVIDER}${NAME}Datastore.kt"
echo "  $DEST_TEST_DIR/${PROVIDER}${NAME}DatastoreTest.kt"
echo ""
echo "# Add to $APP/back-end/src/main/kotlin/com/cramsan/$APP/server/dependencyinjection/DatastoreModule.kt"
echo "singleOf(::${PROVIDER}${NAME}Datastore) { bind<${NAME}Datastore>() }"
