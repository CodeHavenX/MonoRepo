#!/bin/bash
set -e

REGISTRY="ghcr.io/codehavenx"
PLATFORMS="linux/amd64,linux/arm64"
CONF="$(dirname "$0")/deploy_images.conf"

while IFS=' ' read -r image_name build_context || [[ -n "$image_name" ]]; do
  # Skip blank lines and comments
  [[ -z "$image_name" || "$image_name" == \#* ]] && continue

  echo "Building and pushing image: ${REGISTRY}/${image_name}:latest from context: $build_context"

  docker buildx build \
    --progress=plain \
    --platform "$PLATFORMS" \
    -t "${REGISTRY}/${image_name}:latest" \
    "$build_context" \
    --push
done < "$CONF"
