#!/bin/bash
set -e

REGISTRY="ghcr.io/codehavenx"
PLATFORMS="linux/amd64,linux/arm64"
CONF="$(dirname "$0")/deploy_images.conf"
VERSION="${GITHUB_REF_NAME:-local}"

while IFS=' ' read -r image_name dockerfile || [[ -n "$image_name" ]]; do
  # Skip blank lines and comments
  [[ -z "$image_name" || "$image_name" == \#* ]] && continue

  echo "Building and pushing image: ${REGISTRY}/${image_name}:${VERSION} from dockerfile: $dockerfile"

  docker buildx build \
    --progress=plain \
    --platform "$PLATFORMS" \
    -t "${REGISTRY}/${image_name}:latest" \
    -t "${REGISTRY}/${image_name}:${VERSION}" \
    -f "$dockerfile" \
    . \
    --push
done < "$CONF"
