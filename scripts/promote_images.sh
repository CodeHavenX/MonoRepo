#!/bin/bash
set -e

REGISTRY="ghcr.io/codehavenx"
CONF="$(dirname "$0")/deploy_images.conf"
STAGING_TAG="sha-${GITHUB_SHA:-local}"
VERSION="${GITHUB_REF_NAME:-local}"

while IFS=' ' read -r image_name _dockerfile || [[ -n "$image_name" ]]; do
  # Skip blank lines and comments
  [[ -z "$image_name" || "$image_name" == \#* ]] && continue

  echo "Promoting ${REGISTRY}/${image_name}:${STAGING_TAG} → :latest / :${VERSION}"

  docker buildx imagetools create \
    -t "${REGISTRY}/${image_name}:latest" \
    -t "${REGISTRY}/${image_name}:${VERSION}" \
    "${REGISTRY}/${image_name}:${STAGING_TAG}"
done < "$CONF"
