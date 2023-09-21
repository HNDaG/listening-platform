#!/bin/bash

echo "Cleaning Gradle build..."
./gradlew clean

echo "Building your application..."
./gradlew build

echo "Starting Docker Compose..."
docker-compose up --build --force-recreate -d
