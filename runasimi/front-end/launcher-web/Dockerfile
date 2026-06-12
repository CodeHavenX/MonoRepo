FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :runasimi:front-end:launcher-web:wasmJsBrowserDistribution --no-daemon --quiet

FROM nginx:alpine
COPY --from=builder /app/runasimi/front-end/launcher-web/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]