# ── Stage 1: Build the WAR with Maven ─────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy POM first (layer-cached dependency download)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Run on Tomcat 10 (Jakarta EE) ────────────────────────────────
FROM tomcat:10.1-jdk17-temurin

# Remove the default Tomcat welcome app
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Deploy our WAR as the ROOT app (served at /)
COPY --from=build /app/target/calculator.war /usr/local/tomcat/webapps/ROOT.war

# Tomcat listens on 8080 by default
EXPOSE 8080

# Start Tomcat in the foreground (required for Docker)
CMD ["catalina.sh", "run"]
