# Shared helpers for component/feature generator scripts.
# Source this file after setting SCRIPT_DIR in the caller:
#   SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
#   source "$SCRIPT_DIR/_common.sh"

REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Converts a dash-separated identifier to PascalCase (e.g. my-app → MyApp).
to_pascal() {
    echo "$1" | sed 's/-/ /g' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)} 1' | tr -d ' '
}

# apply_subs <src> <dst> [extra -e args...]
# Copies src to dst and applies the 4 standard component substitutions:
#   TemplateReplaceMe → APP_PASCAL, templatereplaceme → APP, User → NAME, user → NAME_LOWER
# Any extra arguments are inserted as additional sed -e expressions between the app
# substitutions and the name substitutions (e.g. -e "s/Example/$PROVIDER/g").
# Requires APP_PASCAL, APP, NAME, NAME_LOWER to be set by the caller.
apply_subs() {
    local src="$1" dst="$2"
    shift 2
    mkdir -p "$(dirname "$dst")"
    cp "$src" "$dst"
    sed -i \
        -e "s/TemplateReplaceMe/$APP_PASCAL/g" \
        -e "s/templatereplaceme/$APP/g" \
        "$@" \
        -e "s/User/$NAME/g" \
        -e "s/user/$NAME_LOWER/g" \
        "$dst"
}

# apply_feature_subs <src> <dst>
# Copies src to dst and substitutes IntelliJ file-template variables.
# Requires PARENT_PACKAGE, FEATURE_PACKAGE, FEATURE_NAME to be set by the caller.
apply_feature_subs() {
    local src="$1" dst="$2"
    mkdir -p "$(dirname "$dst")"
    cp "$src" "$dst"
    sed -i \
        -e "s/\${PACKAGE_NAME}/$PARENT_PACKAGE/g" \
        -e "s/\${Package_Name}/$FEATURE_PACKAGE/g" \
        -e "s/\${Feature_Name}/$FEATURE_NAME/g" \
        "$dst"
}
