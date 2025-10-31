FROM eclipse-temurin:21-jre AS runtime
EXPOSE 8080
RUN mkdir /app
COPY /build/libs/service-ktor-all.jar /app/service.jar
ENTRYPOINT ["java","-jar","/app/service.jar"]