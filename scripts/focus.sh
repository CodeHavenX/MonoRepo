#!/usr/bin/env bash
set -euo pipefail

PROPS_FILE="active-modules.properties"
META_MODULES_DIR=".meta-modules"
SCRIPT_NAME=$(basename "$0")

usage() {
  echo "Usage:"
  echo "  ./$SCRIPT_NAME :module-a :module-b    # focus on specific modules"
  echo "  ./$SCRIPT_NAME meta-module            # focus on a meta-module (see .meta-modules/)"
  echo "  ./$SCRIPT_NAME --all                  # load all modules (removes focus)"
  echo "  ./$SCRIPT_NAME --status               # show current focus"
  echo "  ./$SCRIPT_NAME --list                 # list all available modules and meta-modules"
}

case "${1:-}" in
  --all)
    rm -f "$PROPS_FILE"
    echo "✅ Full project sync restored (removed $PROPS_FILE)"
    ;;

  --status)
    if [ ! -f "$PROPS_FILE" ]; then
      echo "📦 Full sync mode (no $PROPS_FILE found)"
    else
      echo "🔧 Focused on:"
      grep "^modules" "$PROPS_FILE" | sed 's/modules = //' | tr ',' '\n' | sed 's/^ */  - /'
    fi
    ;;

  --list)
    echo "📋 Available modules:"
    find . -name "build.gradle.kts" -o -name "build.gradle" \
      | grep -v "^\./build.gradle" \
      | grep -v "^\./build-logic" \
      | grep -v "^\./devtools/templates" \
      | grep -v "^\./intellij-plugin" \
      | grep -v "/build/" \
      | sed 's|/build.gradle.kts||;s|/build.gradle||;s|^\./|:|;s|/|:|g' \
      | sort

    if [ -d "$META_MODULES_DIR" ]; then
      echo ""
      echo "📋 Available meta-modules:"
      find "$META_MODULES_DIR" -maxdepth 1 -type f -printf "%f\n" | sort
    fi
    ;;

  --help|-h)
    usage
    ;;

  "")
    echo "❌ No modules or meta-modules specified"
    usage
    exit 1
    ;;

  *)
    modules=$(IFS=", "; echo "$*")
    echo "modules = $modules" > "$PROPS_FILE"
    echo "✅ Focus set. Transitive deps and meta-modules will be auto-resolved by Gradle."
    echo "   Modules: $modules"
    echo ""
    echo "   Re-sync your IDE or run: ./gradlew tasks"
    ;;
esac
