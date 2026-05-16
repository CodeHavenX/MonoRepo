FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :edifikana:back-end:buildFatJar --no-daemon --quiet

FROM eclipse-temurin:21-jre AS runtime
EXPOSE 8080
RUN mkdir /app
COPY --from=builder /app/edifikana/back-end/build/libs/edifikana-back-end-all.jar /app/service.jar
ENTRYPOINT ["java","-jar","/app/service.jar"]
