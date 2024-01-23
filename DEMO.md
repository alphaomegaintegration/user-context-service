Certainly! Below is a template for documenting and demoing microservices and their APIs:

# Microservices Demo Documentation

## Overview

This documentation provides a comprehensive guide for setting up, running, and demonstrating a set of microservices and their associated APIs. The demo includes multiple microservices working together to showcase key functionalities.

### Microservices Included

1. **User Context Service**
    - Manages user, context, usercontext information and authentication.
    - Utilizes Keycloak for identity and access management.
    - Has batch load facility for loading users



## Prerequisites

Before starting the demo, ensure the following prerequisites are met:

- Docker and Docker Compose installed.
- Java Development Kit (JDK) version 17 or later.
- Maven installed.

## Setup Instructions

Follow these steps to set up and run the demo:

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/your-username/microservices-demo.git
   cd microservices-demo
   ```

2. **Start Services:**
    - Navigate to the directory with the `docker-compose.yaml` file.
    - Run the startup script to launch required services:
      ```bash
      ./startServices.sh
      ```

3. **Set Environment Variables:**
    - Navigate to the project root directory.
    - Create or use an existing `.env` file and set the required environment variables.

4. **Run Microservices:**
    - Start the microservices by running the following commands:
      ```bash
      cd microservices/user-context-service
      ./mvnw clean package -Dmaven.test.skip=true && java -jar target/user-context-service-0.0.1-SNAPSHOT.jar
      ```

      ```bash
      cd ../batch-service
      ./mvnw clean package -Dmaven.test.skip=true && java -jar target/batch-service-0.0.1-SNAPSHOT.jar
      ```

## API Documentation

### User Context Service API

#### Authentication Endpoint

- **GET /public/login
    - Request:
       ```shell
        http   -a sa.admin@ucs.com:password GET  http://localhost:8090/public/login accept:application/json -v
        ```
      - Response:
         ```json
          {
            "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGdUYtQlM4azNuRzNuSWhWclZSc09qNXloQlhDeVIySkxSZmdMV0JRQ3BJIn0.eyJleHAiOjE3MDUzMzczNjUsImlhdCI6MTcwNTMzNzA2NSwianRpIjoiZTdmOTQ1ZDQtN2M4OC00MjI3LTgzY2MtZjMzYzI1YTVjYWQzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy91c2VyLWNvbnRleHQtc2VydmljZSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIyY2MzNzJjNC1kY2RiLTQ5MzYtOGYwZC0zZWQ5YmUzYzViYjQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ1c2VyLWNvbnRleHQtc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiIxM2MyMWY2NC03NzNkLTRmMDAtYjQ3MS1lZjFkZDZiNzE1MmYiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy11c2VyLWNvbnRleHQtc2VydmljZSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiIxM2MyMWY2NC03NzNkLTRmMDAtYjQ3MS1lZjFkZDZiNzE1MmYiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6InNhIGFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2EuYWRtaW5AdWNzLmNvbSIsImdpdmVuX25hbWUiOiJzYSIsImZhbWlseV9uYW1lIjoiYWRtaW4iLCJlbWFpbCI6InNhLmFkbWluQHVjcy5jb20ifQ.PEttbdwalLBZexc19gY5avyY5oGBRxznAtABE9053p2pDFYrYZYhPP9Hn-5S3l6Hlj84PIsiAQ40qmU1h_mt3rRhuy4ObKSL6DCVSkSVfqwhlj7gN-DF4kQPF0WhuEAks0oq4VFopg2g8c3lzEqQE1p2jYrGOzHv4J6rGQQ7QCbxikF06aTXETDG1i1uEWU1K9oe5hQmXya8uzQ5mJnnNlX62b5_Y8zzIHz5RKa6WdCWyXtl8snzRZx7VJIcU38pOXKOup-yJp2KDH7DNLqa-i9skkIAJhf_F26mKviK_i6VBCnVsAXydNNzp_uYiWexgqe-QJMG3ovG4tOGrq5UNA",
            "expires_in": 300,
            "id_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGdUYtQlM4azNuRzNuSWhWclZSc09qNXloQlhDeVIySkxSZmdMV0JRQ3BJIn0.eyJleHAiOjE3MDUzMzczNjUsImlhdCI6MTcwNTMzNzA2NSwiYXV0aF90aW1lIjowLCJqdGkiOiI0OWZmMTcyOS00NDJjLTRmM2YtYjU0Yy00MDliOGM0NDY1N2IiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL3VzZXItY29udGV4dC1zZXJ2aWNlIiwiYXVkIjoidXNlci1jb250ZXh0LXNlcnZpY2UiLCJzdWIiOiIyY2MzNzJjNC1kY2RiLTQ5MzYtOGYwZC0zZWQ5YmUzYzViYjQiLCJ0eXAiOiJJRCIsImF6cCI6InVzZXItY29udGV4dC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjEzYzIxZjY0LTc3M2QtNGYwMC1iNDcxLWVmMWRkNmI3MTUyZiIsImF0X2hhc2giOiJJMEN6emVUQ2hCakNtSUpWME9Jb0VRIiwiYWNyIjoiMSIsInNpZCI6IjEzYzIxZjY0LTc3M2QtNGYwMC1iNDcxLWVmMWRkNmI3MTUyZiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoic2EgYWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzYS5hZG1pbkB1Y3MuY29tIiwiZ2l2ZW5fbmFtZSI6InNhIiwiZmFtaWx5X25hbWUiOiJhZG1pbiIsImVtYWlsIjoic2EuYWRtaW5AdWNzLmNvbSJ9.XZActc-z_kG_nsqbW8n7GOZGQ0ePPozHzDirmVPnY9oMFtUnNuqAWPddKeyGep_a2UeXS1mkPZuvpx3O2wQraqghpbwIXDo-I9A2frPTxdswixtU0Rkv2WX0TBp0AoeMzNKer-dvlujEN4iswWIB0vV7MvUXKjMI6DxRGoiGbb71sHO9sNjfghA_d1rLBJD9BVN6z6BbJPvhR09j3Sq70hdjfoqBYgLvRTuV0P_P5naZXOX8YHQgMFYCNwrYILqpnRNXrunaGI6Em7Yq-4U4jtRWVtztPx1gXHnvx9YZw2JMtKHyNC4kZWjZZkIuQ8wasQApet4TobjG3PrdQyRYRA",
            "not-before-policy": 0,
            "refresh_expires_in": 1800,
            "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1NTc3ZTgzYS0xNTE3LTRhNTUtODRiMS0xN2MzZDlkNjRiOTIifQ.eyJleHAiOjE3MDUzMzg4NjUsImlhdCI6MTcwNTMzNzA2NSwianRpIjoiMzEzZmU4MzQtYTNhNi00NGNkLTlkZmItNWFiMmIzMDNjYmI1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy91c2VyLWNvbnRleHQtc2VydmljZSIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC9yZWFsbXMvdXNlci1jb250ZXh0LXNlcnZpY2UiLCJzdWIiOiIyY2MzNzJjNC1kY2RiLTQ5MzYtOGYwZC0zZWQ5YmUzYzViYjQiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoidXNlci1jb250ZXh0LXNlcnZpY2UiLCJzZXNzaW9uX3N0YXRlIjoiMTNjMjFmNjQtNzczZC00ZjAwLWI0NzEtZWYxZGQ2YjcxNTJmIiwic2NvcGUiOiJvcGVuaWQgZW1haWwgcHJvZmlsZSIsInNpZCI6IjEzYzIxZjY0LTc3M2QtNGYwMC1iNDcxLWVmMWRkNmI3MTUyZiJ9.KY21DtPMlel7n2aXcNkBcoxusf_e2v9D4Ha2XxUHhUc",
            "scope": "openid email profile",
            "session_state": "13c21f64-773d-4f00-b471-ef1dd6b7152f",
            "token_type": "Bearer"
        }
        ```
- **GET /public/validate
    - Request:
       ```shell
        http --auth-type=jwt GET http://localhost:8090/public/validate -v
        GET /public/validate HTTP/1.1
        Accept: */*
        Accept-Encoding: gzip, deflate
        Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGdUYtQlM4azNuRzNuSWhWclZSc09qNXloQlhDeVIySkxSZmdMV0JRQ3BJIn0.eyJleHAiOjE3MDUzMzc3NDcsImlhdCI6MTcwNTMzNzQ0NywianRpIjoiNTAyZWZmMDgtMWQ1OC00Nzc4LThkMWItOWFhOTMzYjI0YjNjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy91c2VyLWNvbnRleHQtc2VydmljZSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIyY2MzNzJjNC1kY2RiLTQ5MzYtOGYwZC0zZWQ5YmUzYzViYjQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ1c2VyLWNvbnRleHQtc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiIyNzgxMDhkYS0yZTI3LTQ0NDctYjM4ZS0wYjYyNDM2Y2YxZjciLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy11c2VyLWNvbnRleHQtc2VydmljZSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiIyNzgxMDhkYS0yZTI3LTQ0NDctYjM4ZS0wYjYyNDM2Y2YxZjciLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6InNhIGFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2EuYWRtaW5AdWNzLmNvbSIsImdpdmVuX25hbWUiOiJzYSIsImZhbWlseV9uYW1lIjoiYWRtaW4iLCJlbWFpbCI6InNhLmFkbWluQHVjcy5jb20ifQ.C27h6yThjhHf1y-9wiKNWw2-dEfClg3YkEUFDUCeb3vdHU8ukoU9VNCpwNuUV6fq_oBifLZ5-1JxP95BG0mjLfEXmHXckGQQOuUlLVzyOnoFXrLBKCvDLUGR5XG5HB7IYAE1Gm7uenE81bs8RAhwrUf5Ba0xRkhbV-cL69AUhpdmyGYa9thMw0_afrMhcjBoG8M_cPQhT3BDl4ilFIpvLtlqcwKfmXnB7sUPhqL6qceQSCCCV21ul2SbKllPUDzi8nFMry1yXF5Hyk4jILM01u4OlWldIaftyVZjUvMjNH5z2IrMmXGW2fDnb9pz-Xw5CoNQ5L4uWnGWSAc0rKxpAw
        Connection: keep-alive
        Host: localhost:8090
        User-Agent: HTTPie/3.2.2       
      ```
        - Response:
           ```json
            {
              "acr": "1",
              "allowed-origins": [
                "/*"
              ],
              "aud": [
                "account"
              ],
              "azp": "user-context-service",
              "email": "sa.admin@ucs.com",
              "email_verified": true,
              "exp": "2024-01-15T16:55:47Z",
              "family_name": "admin",
              "given_name": "sa",
              "iat": "2024-01-15T16:50:47Z",
              "iss": "http://localhost:8080/realms/user-context-service",
              "jti": "502eff08-1d58-4778-8d1b-9aa933b24b3c",
              "name": "sa admin",
              "preferred_username": "sa.admin@ucs.com",
              "realm_access": {
                "roles": [
                   "offline_access",
                   "uma_authorization",
                   "default-roles-user-context-service"
                ]
              },
              "resource_access": {
                "account": {
                  "roles": [
                    "manage-account",
                    "manage-account-links",
                    "view-profile"
                  ]
               }
              },
              "scope": "openid email profile",
              "session_state": "278108da-2e27-4447-b38e-0b62436cf1f7",
              "sid": "278108da-2e27-4447-b38e-0b62436cf1f7",
              "sub": "2cc372c4-dcdb-4936-8f0d-3ed9be3c5bb4",
              "typ": "Bearer"
            }
          ```

- **GET /contexts**
    - Request:
      ```shell
       http --auth-type=jwt GET http://localhost:8090/contexts -v
       GET /contexts HTTP/1.1
       Accept: */*
       Accept-Encoding: gzip, deflate
       Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGdUYtQlM4azNuRzNuSWhWclZSc09qNXloQlhDeVIySkxSZmdMV0JRQ3BJIn0.eyJleHAiOjE3MDUzMzc3NDcsImlhdCI6MTcwNTMzNzQ0NywianRpIjoiNTAyZWZmMDgtMWQ1OC00Nzc4LThkMWItOWFhOTMzYjI0YjNjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy91c2VyLWNvbnRleHQtc2VydmljZSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIyY2MzNzJjNC1kY2RiLTQ5MzYtOGYwZC0zZWQ5YmUzYzViYjQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ1c2VyLWNvbnRleHQtc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiIyNzgxMDhkYS0yZTI3LTQ0NDctYjM4ZS0wYjYyNDM2Y2YxZjciLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy11c2VyLWNvbnRleHQtc2VydmljZSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiIyNzgxMDhkYS0yZTI3LTQ0NDctYjM4ZS0wYjYyNDM2Y2YxZjciLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6InNhIGFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2EuYWRtaW5AdWNzLmNvbSIsImdpdmVuX25hbWUiOiJzYSIsImZhbWlseV9uYW1lIjoiYWRtaW4iLCJlbWFpbCI6InNhLmFkbWluQHVjcy5jb20ifQ.C27h6yThjhHf1y-9wiKNWw2-dEfClg3YkEUFDUCeb3vdHU8ukoU9VNCpwNuUV6fq_oBifLZ5-1JxP95BG0mjLfEXmHXckGQQOuUlLVzyOnoFXrLBKCvDLUGR5XG5HB7IYAE1Gm7uenE81bs8RAhwrUf5Ba0xRkhbV-cL69AUhpdmyGYa9thMw0_afrMhcjBoG8M_cPQhT3BDl4ilFIpvLtlqcwKfmXnB7sUPhqL6qceQSCCCV21ul2SbKllPUDzi8nFMry1yXF5Hyk4jILM01u4OlWldIaftyVZjUvMjNH5z2IrMmXGW2fDnb9pz-Xw5CoNQ5L4uWnGWSAc0rKxpAw
       Connection: keep-alive
       Host: localhost:8090
       User-Agent: HTTPie/3.2.2
      ```
    - Response:
      ```json
        {"content":[{"id":"user-context-service","contextId":"user-context-service","contextName":"user-context-service","description":"The context for user-context-service","permissions":["DELETE_USERS","LIST_CONTEXTS","LIST_USER_CONTEXTS","UPDATE_USERS","LIST_USERS","CREATE_USERS","VIEW_CONFIGURATIONS","DELETE_USER_CONTEXTS","UPDATE_USER_CONTEXTS","CREATE_CONTEXTS","UPDATE_CONTEXTS","CREATE_USER_CONTEXTS","NONE","DELETE_CONTEXTS"],"roles":[{"roleId":"admin-user-context-service","roleName":"admin-user-context-service","permissions":["DELETE_USERS","LIST_CONTEXTS","LIST_USER_CONTEXTS","UPDATE_USERS","LIST_USERS","CREATE_USERS","VIEW_CONFIGURATIONS","DELETE_USER_CONTEXTS","UPDATE_USER_CONTEXTS","CREATE_CONTEXTS","UPDATE_CONTEXTS","CREATE_USER_CONTEXTS","DELETE_CONTEXTS"]},{"roleId":"noop-user-context-service","roleName":"noop-user-context-service","permissions":["NONE"]},{"roleId":"reader-user-context-service","roleName":"reader-user-context-service","permissions":["LIST_CONTEXTS","LIST_USER_CONTEXTS","LIST_USERS"]}],"enabled":true,"transactionId":null,"createdBy":null,"modifiedBy":null,"createdTime":"2024-01-15T16:19:42.485Z[UTC]","modifiedTime":"2024-01-15T16:19:42.485Z[UTC]"},{"id":"unhrc-bims","contextId":"unhrc-bims","contextName":"unhrc-bims","description":"The context for unhrc-bims","permissions":["VIEW_MY_SCHEDULE","LIST_COUNTRIES","LIST_APPLICANTS_SCHEDULE","ASSIGN_ADMIN_ROLE","ASSIGN_ADJUDICATOR_ROLE","VIEW_APPLICANT_DATA","VIEW_OFFICE_DATA","SCHEDULE_APPLICANTS","LIST_APPLICANTS","LIST_OFFICES","VIEW_APPLICANT_SCHEDULE","VIEW_COUNTRY_DATA","BATCH_SCHEDULE_APPLICANTS","NONE"],"roles":[{"roleId":"noop-unhrc-bims","roleName":"noop-unhrc-bims","permissions":["NONE"]},{"roleId":"adjudicator-unhrc-bims","roleName":"adjudicator-unhrc-bims","permissions":["VIEW_MY_SCHEDULE"]},{"roleId":"employee-unhrc-bims","roleName":"employee-unhrc-bims","permissions":["LIST_APPLICANTS","VIEW_APPLICANT_DATA","VIEW_APPLICANT_SCHEDULE","SCHEDULE_APPLICANTS"]},{"roleId":"admin-unhrc-bims","roleName":"admin-unhrc-bims","permissions":["VIEW_MY_SCHEDULE","LIST_COUNTRIES","LIST_APPLICANTS_SCHEDULE","ASSIGN_ADMIN_ROLE","ASSIGN_ADJUDICATOR_ROLE","VIEW_APPLICANT_DATA","VIEW_OFFICE_DATA","SCHEDULE_APPLICANTS","LIST_APPLICANTS","LIST_OFFICES","VIEW_APPLICANT_SCHEDULE","VIEW_COUNTRY_DATA","BATCH_SCHEDULE_APPLICANTS"]}],"enabled":true,"transactionId":null,"createdBy":null,"modifiedBy":null,"createdTime":"2024-01-15T16:19:42.484Z[UTC]","modifiedTime":"2024-01-15T16:19:42.484Z[UTC]"}],"elapsed":"7","correlationId":null,"page":1,"pageSize":2,"total":2}
      ```

#### User Information Endpoint

- **GET /users/{userId}**
    - Response:
      ```json
      {
        "userId": "123",
        "username": "jason.miller@example.com",
        "firstName": "Jason",
        "lastName": "Miller",
        "email": "jason.miller@example.com"
      }
      ```

### Batch Service API

#### Batch Processing Endpoint

- **POST /process-batch**
    - Request:
      ```json
      {
        "batchId": "1",
        "fileUrl": "s3://your-bucket/users.csv"
      }
      ```
    - Response:
      ```json
      {
        "status": "success",
        "message": "Batch processing completed successfully."
      }
      ```

## Demo Scenarios

1. **User Authentication:**
    - Use the User Context Service to authenticate a user and obtain an access token.

2. **Retrieve User Information:**
    - Fetch user information using the obtained access token from the User Context Service.

3. **Batch Processing:**
    - Submit a batch processing request to the Batch Service and monitor the processing status.

## Conclusion

This documentation serves as a guide to set up and demonstrate the microservices and their APIs. Explore the various scenarios to showcase the capabilities of the microservices architecture. Feel free to adapt and extend the demo based on specific use cases and requirements.