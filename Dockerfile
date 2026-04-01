# Stage 1: Build the React Frontend
FROM node:22-alpine as frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN CI=false npm run build

# Stage 2: Build the Java Backend
FROM maven:3.8.5-openjdk-17-slim as backend-build
WORKDIR /app/backend

# Copy the frontend build output into the backend's static folder
COPY --from=frontend-build /app/frontend/dist /app/backend/src/main/resources/static

# Copy backend files and build the JAR
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2.5: Find the JAR (to ensure robust naming)
RUN cp target/*.jar app.jar

# Stage 3: Final Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=backend-build /app/backend/app.jar .

# Render dynamically assigns a port, so we make sure Spring Boot listens to it
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
