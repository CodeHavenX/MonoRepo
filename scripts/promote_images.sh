#!/bin/bash
set -e

REGISTRY="ghcr.io/codehavenx"
CONF="$(dirname "$0")/deploy_images.conf"
SOURCE_TAG="${SOURCE_TAG:-sha-${GITHUB_SHA:-local}}"
TARGET_TAG="${TARGET_TAG:-latest}"

while IFS=' ' read -r image_name _dockerfile || [[ -n "$image_name" ]]; do
  # Skip blank lines and comments
  [[ -z "$image_name" || "$image_name" == \#* ]] && continue

  echo "Promoting ${REGISTRY}/${image_name}:${SOURCE_TAG} → :${TARGET_TAG}"

  docker buildx imagetools create \
    -t "${REGISTRY}/${image_name}:${TARGET_TAG}" \
    "${REGISTRY}/${image_name}:${SOURCE_TAG}"
done < "$CONF"
