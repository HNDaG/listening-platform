#!/bin/bash

echo "Building your application..."
./gradlew assemble

echo "Starting Docker Compose..."
docker-compose up --build --force-recreate -d
