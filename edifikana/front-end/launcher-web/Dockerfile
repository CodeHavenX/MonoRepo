FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :edifikana:front-end:launcher-web:wasmJsBrowserDistribution --no-daemon --quiet

FROM nginx:alpine
COPY --from=builder /app/edifikana/front-end/launcher-web/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
COPY edifikana/front-end/launcher-web/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]