# syntax=docker/dockerfile:1

# 1. Compila o frontend React
FROM node:22-alpine AS frontend-build

WORKDIR /frontend

COPY marketplace-frontend/package.json marketplace-frontend/package-lock.json ./
RUN npm ci

COPY marketplace-frontend/ ./
RUN npm run build


# 2. Compila o backend Spring Boot
FROM maven:3.9-eclipse-temurin-21 AS backend-build

WORKDIR /app

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src

# Coloca o React compilado nos arquivos estáticos do Spring
COPY --from=frontend-build /frontend/dist/ ./src/main/resources/static/

RUN mvn -B -DskipTests clean package


# 3. Imagem final, somente com Java
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S marketplace \
    && adduser -S marketplace -G marketplace

COPY --from=backend-build \
    /app/target/marketplace-0.0.1-SNAPSHOT.jar \
    /app/marketplace.jar

USER marketplace

ENV PORT=10000

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT} -jar /app/marketplace.jar"]