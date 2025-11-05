#!/bin/bash
set -e

docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/codehavenx/edifikana-backend:latest edifikana/back-end/ --push

docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/codehavenx/edifikana-frontend:latest edifikana/front-end/app-wasm --push

docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/codehavenx/runasimi-frontend:latest runasimi/front-end/app-wasm --push

docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/codehavenx/samples-ktor-service:latest samples/service-ktor --push

docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/codehavenx/samples-wasm-frontend:latest samples/jbcompose-wasm-app --push
