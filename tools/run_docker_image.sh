#!/bin/bash

docker compose up
PORT=$(docker compose port web 8080 | cut -d':' -f2)
echo "App is running on host's port: $PORT"