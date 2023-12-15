#!/bin/bash

if [ -d zpi-student-project-management ]; then
    cd zpi-student-project-management
else
    git clone https://github.com/zpi-wojdan/zpi-student-project-management.git
    cd zpi-student-project-management
fi

git pull

docker build -t frontend:latest zpi-frontend
docker build -t backend:latest zpi-backend

docker compose up -d