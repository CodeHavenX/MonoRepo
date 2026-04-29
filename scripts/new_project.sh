#!/usr/bin/env bash
# Creates a new project by cloning templatereplaceme and substituting the placeholder name.
#
# Usage:
#   scripts/new_project.sh <project_name>
#
# <project_name> must be lowercase letters and numbers only (e.g. "myapp", "runasimi2").
# The script derives all casing variants automatically:
#   lower  : myapp
#   Pascal : Myapp          (used in class names — TemplateReplaceMe → Myapp)
#   UPPER  : MYAPP          (used in env var names)
#   kebab  : myapp          (used in docker image names / jar names)
#   snake  : myapp          (used in gradle variable names like templatereplacemeBuildVariable)
#
# After running this script:
#   1. Review the generated project — especially config.properties, docker-compose.yml,
#      and the application.conf module path.
#   2. Commit the two modified root files (settings.gradle.kts, build.gradle.kts) and
#      the new project directory.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TEMPLATE_DIR="$REPO_ROOT/templatereplaceme"

# ── Argument validation ────────────────────────────────────────────────────────

if [[ $# -ne 1 ]]; then
    echo "Usage: $0 <project_name>"
    echo "  <project_name> — lowercase letters/numbers only (e.g. myapp)"
    exit 1
fi

NEW_NAME="$1"

if [[ ! "$NEW_NAME" =~ ^[a-z][a-z0-9]*$ ]]; then
    echo "Error: project name must be lowercase letters/numbers only, starting with a letter."
    exit 1
fi

if [[ "$NEW_NAME" == "templatereplaceme" ]]; then
    echo "Error: project name cannot be 'templatereplaceme'."
    exit 1
fi

DEST_DIR="$REPO_ROOT/$NEW_NAME"

if [[ -d "$DEST_DIR" ]]; then
    echo "Error: directory '$DEST_DIR' already exists."
    exit 1
fi

# ── Derive casing variants ─────────────────────────────────────────────────────

OLD_LOWER="templatereplaceme"
OLD_PASCAL="TemplateReplaceMe"
OLD_UPPER="TEMPLATE_REPLACE_ME"
OLD_KEBAB="template-replace-me"
OLD_CAMEL="templatereplaceme"        # gradle variable prefix (same as lower here)

NEW_LOWER="$NEW_NAME"
# Capitalise first letter for PascalCase
NEW_PASCAL="$(echo "${NEW_NAME:0:1}" | tr '[:lower:]' '[:upper:]')${NEW_NAME:1}"
NEW_UPPER="$(echo "$NEW_NAME" | tr '[:lower:]' '[:upper:]')"
NEW_KEBAB="$NEW_NAME"    # already lowercase, no hyphens needed for single-word names
NEW_CAMEL="$NEW_NAME"

echo "Creating project '$NEW_NAME' from template…"
echo "  lower  : $NEW_LOWER"
echo "  Pascal : $NEW_PASCAL"
echo "  UPPER  : $NEW_UPPER"
echo ""

# ── Copy template (excluding build artefacts) ──────────────────────────────────

rsync -a --exclude='build/' --exclude='.DS_Store' "$TEMPLATE_DIR/" "$DEST_DIR/"

# ── Replace file contents ──────────────────────────────────────────────────────
# Process every non-binary file under the new project directory.

echo "Substituting placeholder strings in file contents…"

find "$DEST_DIR" -type f \
    \( -name "*.kt" -o -name "*.kts" -o -name "*.conf" \
    -o -name "*.properties" -o -name "*.yml" -o -name "*.yaml" \
    -o -name "*.xml" -o -name "Dockerfile" -o -name ".gitignore" \) | \
while read -r file; do
    # Apply all four substitutions in order (most specific first)
    sed -i '' \
        -e "s/${OLD_PASCAL}/${NEW_PASCAL}/g" \
        -e "s/${OLD_UPPER}/${NEW_UPPER}/g" \
        -e "s/${OLD_KEBAB}/${NEW_KEBAB}/g" \
        -e "s/${OLD_LOWER}/${NEW_LOWER}/g" \
        "$file"
done

# ── Rename files whose names contain the placeholder ──────────────────────────

echo "Renaming files…"

# Collect files whose names contain the Pascal variant (covers most cases)
# Process deepest paths first to avoid renaming parents before children.
find "$DEST_DIR" -depth -type f \( \
    -name "*${OLD_PASCAL}*" -o \
    -name "*${OLD_LOWER}*" \
\) | while read -r old_path; do
    dir="$(dirname "$old_path")"
    base="$(basename "$old_path")"
    new_base="${base//${OLD_PASCAL}/${NEW_PASCAL}}"
    new_base="${new_base//${OLD_LOWER}/${NEW_LOWER}}"
    if [[ "$base" != "$new_base" ]]; then
        mv "$old_path" "$dir/$new_base"
    fi
done

# ── Rename directories whose names contain the placeholder ────────────────────

echo "Renaming directories…"

# Must process depth-first so children are renamed before parents.
find "$DEST_DIR" -depth -type d \( \
    -name "*${OLD_PASCAL}*" -o \
    -name "*${OLD_LOWER}*" \
\) | while read -r old_dir; do
    parent="$(dirname "$old_dir")"
    base="$(basename "$old_dir")"
    new_base="${base//${OLD_PASCAL}/${NEW_PASCAL}}"
    new_base="${new_base//${OLD_LOWER}/${NEW_LOWER}}"
    if [[ "$base" != "$new_base" ]]; then
        mv "$old_dir" "$parent/$new_base"
    fi
done

# ── Append includes to settings.gradle.kts ────────────────────────────────────

SETTINGS_FILE="$REPO_ROOT/settings.gradle.kts"

echo "Updating settings.gradle.kts…"

# Find the last templatereplaceme include line and insert the new block after it.
# Using printf to build the block and Python to do the insertion safely.
python3 - "$SETTINGS_FILE" "$OLD_LOWER" "$NEW_LOWER" <<'PYEOF'
import sys, re

settings_path = sys.argv[1]
old_name      = sys.argv[2]
new_name      = sys.argv[3]

with open(settings_path) as f:
    content = f.read()

# Build the new include block mirroring the template block
new_block = f'''
include("{new_name}:back-end")
include("{new_name}:shared")
include("{new_name}:api")
include("{new_name}:front-end:shared-ui")
include("{new_name}:front-end:shared-app")
include("{new_name}:front-end:app-android")
include("{new_name}:front-end:app-jvm")
include("{new_name}:front-end:app-wasm")
'''

# Find the last line that includes old_name and insert the new block after the
# blank line that follows it.
pattern = rf'(include\("{re.escape(old_name)}[^"]*"\)\n(?:include\("{re.escape(old_name)}[^"]*"\)\n)*)'
match = re.search(pattern, content)
if match:
    insert_at = match.end()
    content = content[:insert_at] + new_block + content[insert_at:]
else:
    content += new_block

with open(settings_path, 'w') as f:
    f.write(content)

print(f"  Added include block for '{new_name}'")
PYEOF

# ── Append dependsOn entries to root build.gradle.kts ─────────────────────────

BUILD_FILE="$REPO_ROOT/build.gradle.kts"

echo "Updating build.gradle.kts…"

python3 - "$BUILD_FILE" "$OLD_LOWER" "$NEW_LOWER" <<'PYEOF'
import sys, re

build_path = sys.argv[1]
old_name   = sys.argv[2]
new_name   = sys.argv[3]

with open(build_path) as f:
    content = f.read()

new_block = f'''
    dependsOn("{new_name}:back-end:release")
    dependsOn("{new_name}:shared:release")
    dependsOn("{new_name}:api:release")
    dependsOn("{new_name}:front-end:shared-ui:release")
    dependsOn("{new_name}:front-end:shared-app:release")
    dependsOn("{new_name}:front-end:app-wasm:release")
    dependsOn("{new_name}:front-end:app-android:release")
    dependsOn("{new_name}:front-end:app-jvm:release")
'''

pattern = rf'(    dependsOn\("{re.escape(old_name)}[^"]*:release"\)\n(?:    dependsOn\("{re.escape(old_name)}[^"]*:release"\)\n)*)'
match = re.search(pattern, content)
if match:
    insert_at = match.end()
    content = content[:insert_at] + new_block + content[insert_at:]
else:
    content += new_block

with open(build_path, 'w') as f:
    f.write(content)

print(f"  Added dependsOn block for '{new_name}'")
PYEOF

# ── Done ───────────────────────────────────────────────────────────────────────

echo ""
echo "Done. Project '$NEW_NAME' created at: $DEST_DIR"
echo ""
echo "Next steps:"
echo "  1. Review $NEW_NAME/back-end/config.properties and docker-compose.yml"
echo "  2. Run: ./gradlew :${NEW_NAME}:back-end:release to verify the back-end builds"
echo "  3. Run: ./gradlew :${NEW_NAME}:front-end:shared-app:release to verify the front-end builds"
echo "  4. Commit the new directory plus the updated settings.gradle.kts and build.gradle.kts"
