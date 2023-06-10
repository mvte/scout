# build stage
FROM gradle:latest AS BUILD
WORKDIR /app
COPY src .
RUN gradle clean build

# production stage
FROM openjdk:latest
ENV ARTIFACT_NAME=scout.jar

WORKDIR /app
COPY --from=BUILD src .

ENTRYPOINT exec java -jar ./app/lib/build/libs/$ARTIFACT_NAME
