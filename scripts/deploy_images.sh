#!/bin/bash

docker build -t ghcr.io/codehavenx/edifikana-backend:latest edifikana/back-end
docker push ghcr.io/codehavenx/edifikana-backend:latest

docker build -t ghcr.io/codehavenx/edifikana-frontend:latest edifikana/front-end/app-wasm
docker push ghcr.io/codehavenx/edifikana-frontend:latest

docker build -t ghcr.io/codehavenx/runasimi-frontend:latest runasimi/front-end/app-wasm
docker push ghcr.io/codehavenx/runasimi-frontend:latest

docker build -t ghcr.io/codehavenx/samples-ktor-service:latest samples/service-ktor
docker push ghcr.io/codehavenx/samples-ktor-service:latest

docker build -t ghcr.io/codehavenx/samples-wasm-frontend:latest samples/jbcompose-wasm-app
docker push ghcr.io/codehavenx/samples-wasm-frontend:latest