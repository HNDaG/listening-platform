#!/bin/bash

echo "Building your application..."
./gradlew assemle

echo "Starting Docker Compose..."
docker-compose up --build --force-recreate -d
