# build stage
FROM gradle:latest AS BUILD
WORKDIR /app
COPY . .
RUN gradle clean build

# production stage
FROM openjdk:latest
ENV ARTIFACT_NAME='scout-1.0.jar'

WORKDIR /app
COPY --from=BUILD . .

ENTRYPOINT exec java -jar ./app/build/libs/$ARTIFACT_NAME
