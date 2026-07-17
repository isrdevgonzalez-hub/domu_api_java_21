FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

RUN echo "=== CONTENIDO DE TARGET ===" \
    && find /app/target -maxdepth 2 -type f \
    && WAR_FILE=$(find /app/target -maxdepth 1 -type f -name "*.war" | head -n 1) \
    && echo "WAR encontrado: $WAR_FILE" \
    && test -n "$WAR_FILE" \
    && cp "$WAR_FILE" /app/app.war \
    && test -f /app/app.war

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/app.war /app/app.war

RUN ls -lah /app \
    && test -f /app/app.war

EXPOSE 8080
EXPOSE 5005

ENTRYPOINT ["java", "-jar", "/app/app.war"]
