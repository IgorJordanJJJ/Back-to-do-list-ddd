# Build stage for the application
FROM openjdk:17-jdk-slim-buster AS builder
WORKDIR /application

# Copy Gradle build files
COPY gradlew .
COPY settings.gradle.kts .

# Copy modules
COPY bootstrap/ bootstrap/
COPY application/ application/
COPY ports/ ports/
COPY domain/ domain/
COPY infrastructure/ infrastructure/


# Copy all other project files
COPY . .

# Set execution permissions and start the build, excluding tests
RUN --mount=type=cache,target=/root/.gradle  chmod +x gradlew && ./gradlew clean build -x test

# Stage to extract layers from the built .jar file
FROM openjdk:17-jdk-slim-buster AS layers
WORKDIR /application

# Copy the built .jar file from the previous stage
COPY --from=builder /application/app/build/libs/*.jar app.jar

# Extract layers from the .jar file
RUN java -Djarmode=layertools -jar app.jar extract

# Final image for running the application
FROM openjdk:17-jdk-slim-buster
VOLUME /tmp

# Install necessary fonts
RUN apt-get update && apt-get install -y fontconfig ttf-dejavu fonts-dejavu-core && rm -rf /var/lib/apt/lists/*

RUN adduser --disabled-password --gecos "" spring-user
USER spring-user

# Copy necessary directories to run the application
COPY --from=layers /application/dependencies/ ./
COPY --from=layers /application/spring-boot-loader/ ./
COPY --from=layers /application/snapshot-dependencies/ ./
COPY --from=layers /application/application/ ./

# Set the entry point to run the application
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]