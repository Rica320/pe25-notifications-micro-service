# pe25-notifications-micro-service

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Implemented Plugins and Their Features

This section outlines the list of plugins that we have implemented along with their features. The following is a list of plugins and their respective features and corresponding endpoint:

### Authentication
- [x] JPA identity provider with Basic authentication

### Notifier

- [x] Send messages through multiple plugins.
  - Endpoint: /notifier

### WhatsApp

- [x] Send text messages
  - Endpoint: /whatsapp/message/text
- [x] Send messages with media files such as images and videos
  - Endpoint: /whatsapp/message/media
- [x] Send messages with location
  - Endpoint: /whatsapp/message/location
- [x] Send messages with link
  - Endpoint: /whatsapp/message/link
- [x] Create a group
  - Endpoint: /whatsapp/group/create
- [x] Add a person to the group
  - Endpoint: /whatsapp/group/add
- [x] Remove a person from the group
  - Endpoint: /whatsapp/group/remove

### Microsoft Teams

- [x] Add a webhook to the database
  - Endpoint: /msteams/add
- [x] Send a message to a team or teams
  - Endpoint: /msteams/message

### SMTP

- [x] Qute Template 
- [x] Send Smpt emails
  - Endpoint: /notifier for now

### SMPP

- [x] Send SMS messages
  - Endpoint: /sms/message

### Other features
 - [x] Limit to the endpoint use
 - [x] Schedule notification

## Prerequisites

You must have the following docker container running:
>docker run --name my_db_pe25 -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=my_db_pe25 -p 5432:5432 postgres:10.5

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/pe25-notifications-micro-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related Guides

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and JPA
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, JPA)
- RESTEasy Classic JSON-B ([guide](https://quarkus.io/guides/rest-json)): JSON-B serialization support for RESTEasy Classic
- GitHub App Command Airline ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-github-app/dev/index.html)): Add comment-based commands to your GitHub App
- GitHub App ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-github-app/dev/index.html)): Automate GitHub tasks with a GitHub App

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)



### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
