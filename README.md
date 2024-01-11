# Microservice README

This README provides instructions for setting up and running the User Context Service microservice. Follow the steps below to ensure a smooth deployment.

## Prerequisites

- Docker and Docker Compose
- Java Development Kit (JDK) version 17 or later
- Maven

## Step 1: Start Services

1. Navigate to the directory containing the `docker-compose.yaml` file.
2. Run the startup script to launch the required services:

    ```bash
    cd /path/to/project
    ./startServices.sh
    ```
## Step 2: Set up Keycloak and extract client ids and secrets

Use this article of a reference on how to setup keycloak https://www.mastertheboss.com/keycloak/how-to-use-keycloak-admin-rest-api/#google_vignette
## Step 3: Set Environment Variables

1. Navigate to the project root directory.
2. Create or use an existing `.env` file and set the following environment variables:

    ```bash

    export POSTGRES_URL=jdbc:postgresql://localhost:5432/batch
    export POSTGRES_USER=batchuser
    export POSTGRES_PASSWORD=password

    export REDIS_HOST=localhost
    export REDIS_PORT=6379
    export REDIS_SERVER_MODE=standalone

    export USER_BATCH_CSV_FILES=classpath:users.csv
    export USER_BATCH_FLUSH_DB=false

    export KEYCLOAK_CLIENT_SECRET=client_secret
    export KEYCLOAK_CLIENT_ID=user-context-service
    export KEYCLOAK_REALM=user-context-service
    export KEYCLOAK_BASE_URL=http://localhost:8080
    export KEYCLOAK_TOKEN_URI=/realms/user-context-service/protocol/openid-connect/token
    export KEYCLOAK_JWKSET_URI=http://localhost:8080/realms/user-context-service/protocol/openid-connect/certs
    export KEYCLOAK_ISSUER_URL=http://localhost:8080/realms/user-context-service
    export KEYCLOAK_ADMIN_USER=admin
    export KEYCLOAK_ADMIN_PASSWORD=password
    export KEYCLOAK_ADMIN_CLIENT_SECRET=client_secret
    export KEYCLOAK_ADMIN_CLIENT_ID=admin-cli
    ```

## Step 4: Run the Application

Execute the following command to start the application:

```bash
./mvnw clean package -Dmaven.test.skip=true && java -jar target/user-context-service-0.0.1-SNAPSHOT.jar
```

The microservice will be deployed and accessible at the specified configurations.

Note: Ensure that all dependencies and prerequisites are met before running the above commands. Adjust any file paths or configurations as needed for your environment.