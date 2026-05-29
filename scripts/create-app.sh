#!/usr/bin/env bash
set -euo pipefail

APP_NAME=""
DISPLAY_NAME=""
NO_WASM=false
NO_ANDROID=false
NO_JVM=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --name)    APP_NAME="$2";    shift 2 ;;
        --display) DISPLAY_NAME="$2"; shift 2 ;;
        --no-wasm)    NO_WASM=true;    shift ;;
        --no-android) NO_ANDROID=true; shift ;;
        --no-jvm)     NO_JVM=true;     shift ;;
        *) echo "Unknown argument: $1" >&2; exit 1 ;;
    esac
done

if [[ -z "$APP_NAME" || -z "$DISPLAY_NAME" ]]; then
    echo "Usage: $0 --name <appname> --display <DisplayName> [--no-wasm] [--no-android] [--no-jvm]" >&2
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Derive name variants
APP_UPPER=$(echo "$APP_NAME" | tr '[:lower:]' '[:upper:]' | tr '-' '_')
APP_DASH=$(echo "$APP_NAME" | tr '_' '-')
APP_UNDERSCORE=$(echo "$APP_NAME" | tr '-' '_')

DEST="$REPO_ROOT/$APP_NAME"
TEMPLATE="$REPO_ROOT/templatereplaceme"

if [[ -e "$DEST" ]]; then
    echo "Error: $DEST already exists." >&2
    exit 1
fi

# Step 1: Copy template
echo "Copying template..."
cp -r "$TEMPLATE" "$DEST"

# Remove build artifacts from the copy
find "$DEST" -type d -name "build" -exec rm -rf {} + 2>/dev/null || true

# Step 2: Replace strings in file contents
echo "Replacing content strings..."
find "$DEST" \( -name "*.kt" -o -name "*.kts" -o -name "*.xml" -o -name "*.conf" \
    -o -name "*.json" -o -name "*.yml" -o -name "*.html" -o -name "*.css" \) \
    -not -path "*/build/*" \
    | while read -r f; do
        sed -i \
            -e "s/TEMPLATEREPLACEME/$APP_UPPER/g" \
            -e "s/template-replace-me/$APP_DASH/g" \
            -e "s/template_replace_me/$APP_UNDERSCORE/g" \
            -e "s/TemplateReplaceMe/$DISPLAY_NAME/g" \
            -e "s/templatereplaceme/$APP_NAME/g" \
            "$f"
    done

# Step 3: Rename directories and files bottom-up
echo "Renaming paths..."
# Rename files first (deepest first via sort -r on depth)
while IFS= read -r path; do
    base=$(basename "$path")
    dir=$(dirname "$path")
    new_base="${base//templatereplaceme/$APP_NAME}"
    new_base="${new_base//TemplateReplaceMe/$DISPLAY_NAME}"
    if [[ "$base" != "$new_base" ]]; then
        mv "$path" "$dir/$new_base"
    fi
done < <(find "$DEST" -not -path "*/build/*" \( -name "*templatereplaceme*" -o -name "*TemplateReplaceMe*" \) | awk '{ print length, $0 }' | sort -rn | cut -d' ' -f2-)

# Step 4: Append includes to settings.gradle.kts
echo "Updating settings.gradle.kts..."
SETTINGS="$REPO_ROOT/settings.gradle.kts"
{
    echo ""
    echo "include(\"$APP_NAME:api\")"
    echo "include(\"$APP_NAME:shared\")"
    echo "include(\"$APP_NAME:back-end\")"
    echo "include(\"$APP_NAME:front-end:shared-app\")"
    echo "include(\"$APP_NAME:front-end:shared-ui\")"
    if [[ "$NO_ANDROID" == false ]]; then
        echo "include(\"$APP_NAME:front-end:app-android\")"
    fi
    if [[ "$NO_JVM" == false ]]; then
        echo "include(\"$APP_NAME:front-end:app-jvm\")"
    fi
    if [[ "$NO_WASM" == false ]]; then
        echo "include(\"$APP_NAME:front-end:app-wasm\")"
    fi
} >> "$SETTINGS"

# Step 5: Append dependsOn entries to releaseAll in build.gradle.kts
echo "Updating build.gradle.kts..."
BUILD="$REPO_ROOT/build.gradle.kts"
DEPENDS_BLOCK="    dependsOn(\"$APP_NAME:api:release\")\n"
DEPENDS_BLOCK+="    dependsOn(\"$APP_NAME:shared:release\")\n"
DEPENDS_BLOCK+="    dependsOn(\"$APP_NAME:back-end:release\")\n"
DEPENDS_BLOCK+="    dependsOn(\"$APP_NAME:front-end:shared-app:release\")\n"
DEPENDS_BLOCK+="    dependsOn(\"$APP_NAME:front-end:shared-ui:release\")"
if [[ "$NO_ANDROID" == false ]]; then
    DEPENDS_BLOCK+="\n    dependsOn(\"$APP_NAME:front-end:app-android:release\")"
fi
if [[ "$NO_JVM" == false ]]; then
    DEPENDS_BLOCK+="\n    dependsOn(\"$APP_NAME:front-end:app-jvm:release\")"
fi
if [[ "$NO_WASM" == false ]]; then
    DEPENDS_BLOCK+="\n    dependsOn(\"$APP_NAME:front-end:app-wasm:release\")"
fi

# Insert before the closing } of the releaseAll block
sed -i "s|    dependsOn(\"generateBuildArtifacts\")|$DEPENDS_BLOCK\n    dependsOn(\"generateBuildArtifacts\")|" "$BUILD"

# Step 6: Remove excluded platforms
if [[ "$NO_WASM" == true ]]; then
    echo "Removing WASM platform..."
    rm -rf "$DEST/front-end/app-wasm"
fi
if [[ "$NO_ANDROID" == true ]]; then
    echo "Removing Android platform..."
    rm -rf "$DEST/front-end/app-android"
fi
if [[ "$NO_JVM" == true ]]; then
    echo "Removing JVM platform..."
    rm -rf "$DEST/front-end/app-jvm"
fi

echo ""
echo "============================================"
echo " App '$APP_NAME' created successfully!"
echo "============================================"
echo ""
echo "Post-generation checklist:"
echo "  [ ] Update Dockerfile image name in $APP_NAME/back-end/"
echo "  [ ] Configure docker-compose credentials for $APP_NAME"
echo "  [ ] Set Supabase/backend credentials in config files"
echo "  [ ] Add CI pipeline for $APP_NAME (e.g., .github/workflows/$APP_NAME.yml)"
echo "  [ ] Review and update DI modules in $APP_NAME/back-end/.../dependencyinjection/"
echo "  [ ] Review and update DI modules in $APP_NAME/front-end/shared-app/.../di/"
