#!/usr/bin/env bash
set -euo pipefail

# --name    PascalCase feature name, e.g. AddProperty
# --parent  Path relative to repo root where the feature package will be created, e.g.
#           edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features

FEATURE_NAME=""
PARENT_REL=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --name)   FEATURE_NAME="$2"; shift 2 ;;
        --parent) PARENT_REL="$2";   shift 2 ;;
        *) echo "Unknown argument: $1" >&2; exit 1 ;;
    esac
done

if [[ -z "$FEATURE_NAME" || -z "$PARENT_REL" ]]; then
    echo "Usage: $0 --name <FeatureName> --parent <parent-dir-path>" >&2
    echo "Example: $0 --name AddProperty --parent edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features" >&2
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/_common.sh"

# Derive package info — extract everything after "kotlin/" and convert / to .
PACKAGE_PATH="${PARENT_REL#*/kotlin/}"
PARENT_PACKAGE="${PACKAGE_PATH//\//.}"
FEATURE_PACKAGE=$(echo "$FEATURE_NAME" | tr '[:upper:]' '[:lower:]')

COMMON_DIR="$REPO_ROOT/$PARENT_REL/$FEATURE_PACKAGE"
TEST_DIR="$REPO_ROOT/${PARENT_REL/\/commonMain\//\/jvmTest\/}/$FEATURE_PACKAGE"

TMPL_DIR="$REPO_ROOT/.idea/fileTemplates"

apply_feature_subs "$TMPL_DIR/Compose Feature.kt"            "$COMMON_DIR/${FEATURE_NAME}Screen.kt"
apply_feature_subs "$TMPL_DIR/Compose Feature.kt.child.1.kt" "$COMMON_DIR/${FEATURE_NAME}Event.kt"
apply_feature_subs "$TMPL_DIR/Compose Feature.kt.child.2.kt" "$COMMON_DIR/${FEATURE_NAME}UIState.kt"
apply_feature_subs "$TMPL_DIR/Compose Feature.kt.child.3.kt" "$COMMON_DIR/${FEATURE_NAME}ViewModel.kt"
apply_feature_subs "$TMPL_DIR/Compose Feature.kt.child.4.kt" "$COMMON_DIR/${FEATURE_NAME}Screen.preview.kt"
apply_feature_subs "$TMPL_DIR/Compose Feature.kt.child.0.kt" "$TEST_DIR/${FEATURE_NAME}ViewModelTest.kt"

echo "Created:"
echo "  $COMMON_DIR/${FEATURE_NAME}Screen.kt"
echo "  $COMMON_DIR/${FEATURE_NAME}Event.kt"
echo "  $COMMON_DIR/${FEATURE_NAME}UIState.kt"
echo "  $COMMON_DIR/${FEATURE_NAME}ViewModel.kt"
echo "  $COMMON_DIR/${FEATURE_NAME}Screen.preview.kt"
echo "  $TEST_DIR/${FEATURE_NAME}ViewModelTest.kt"
echo ""
echo "# Register the ViewModel in the appropriate DI module (ViewModelModule.kt or ViewModelPlatformModule):"
echo "viewModelOf(::${FEATURE_NAME}ViewModel)"
echo ""
echo "# Register the destination as a route in the appropriate router."
