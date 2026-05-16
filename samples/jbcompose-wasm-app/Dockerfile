FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :samples:jbcompose-wasm-app:wasmJsBrowserDistribution --no-daemon --quiet

FROM nginx:alpine
COPY --from=builder /app/samples/jbcompose-wasm-app/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]