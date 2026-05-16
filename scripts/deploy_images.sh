#!/bin/bash
set -e

SCRIPTS="$(dirname "$0")"

"$SCRIPTS/build_images.sh"
"$SCRIPTS/promote_images.sh"
