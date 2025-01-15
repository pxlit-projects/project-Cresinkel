# Fullstack Java Project

## Wouter Cressato (3AONC)

## Folder structure

- Readme.md
- _architecture_: this folder contains documentation regarding the architecture of your system.
- `docker-compose.yml` : to start the backend (starts all microservices)
- _backend-java_: contains microservices written in java
- _demo-artifacts_: contains images, files, etc that are useful for demo purposes.
- _frontend-web_: contains the Angular webclient

Each folder contains its own specific `.gitignore` file.  
**:warning: complete these files asap, so you don't litter your repository with binary build artifacts!**

## How to setup and run this application

### Frontend
In frontend/project/ run commands:
* npm run build
* docker build -t NAME
* docker run -d -p PORT:80 NAME

### Backend
Run XAMPP to start database (or another way to start your mysql)
Run "docker compose up" in backend/project/ to start RabbitMQ
Run the 6 applications in this order:
* Configuration Service
* Discovery Service
* Gateway Service
* Review Service
* Comment Service
* Post Service
