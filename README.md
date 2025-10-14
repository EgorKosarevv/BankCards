# BankCards

Detailed guide for setting up and running a Spring Boot application with Docker Compose.

---

## Table of Contents

- [Requirements](#requirements)  
- [Cloning the Repository](#cloning-the-repository)  
- [Configuration](#configuration)  
- [Building the Application](#building-the-application)  
- [Running with Docker Compose](#running-with-docker-compose)  
- [Usage](#usage)  
- [Stopping and Cleanup](#stopping-and-cleanup)  
- [Contacts](#contacts)  

---

## Requirements
- Java 17  
- Apache Maven 3.9.9  
- Spring Boot 3.3.5  
- Docker 28.0.4  
- Git 2.49
- PostgreSQL
- MapStruct
- Liquibase
- Swagger
- JWT


---

## Cloning the Repository

```bash
git clone https://github.com/EgorKosarevv/BankCards.git
cd BankCards
```

---

## Configuration

1. In the root directory, there is a `docker-compose.yml` file. It defines the following services:  
   - `app` —  Spring Boot application  
   - `postgres` — PostgreSQL database  

2. If needed, update the environment variables in `docker-compose.yml`, e.g., database settings.

---

## Building the Application

Before running the application, you need to build the project and create the executable JAR file.
1. Make sure that Java 17+ and Maven are installed on your system..

2. Open the project directory in the terminal

3. Run the following command to build the project:

```bash
mvn clean install
```

---

## Running with Docker Compose

To start all services:

```bash
docker compose up -d
```

This will start:  
- PostgreSQL (with persistent volume for data)  
- Spring Boot application (available on the port specified in `docker-compose.yml`)  

Check running containers:

```bash
docker ps
```

View application logs:

```bash
docker compose logs -f app
```

---

## Usage

- Open your browser and go to: `http://localhost:8080` (port may vary based on settings)  
- Use the API or the web interface of your application  

---

## Stopping and Cleanup

To stop the containers:

```bash
docker compose down
```

To also remove all volumes (data):

```bash
docker compose down -v
```

---

## Contacts

- Telegram: [@kosarevvegor](https://t.me/kosarevvegor)
