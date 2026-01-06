FROM amazoncorretto:25-alpine AS build

RUN apk add --no-cache bash curl unzip git

WORKDIR /app

# Copy only Gradle wrapper + build scripts first
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

# Download dependencies (cached unless build scripts change)
RUN ./gradlew dependencies --no-daemon || true

# Now copy the actual source code
COPY src src

# Build the application
RUN ./gradlew clean bootJar -x test --no-daemon

# ---- Runtime image ----
FROM amazoncorretto:25-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]