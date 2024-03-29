# Notifications Microservice

This project was developed in the context of the Capstone Project Curricular Unit of the Informatics
and Computer Engineering course at FEUP and in collaboration with Altice Labs.

For additional details, please refer to our [report](docs/pe25_report.pdf) and [poster](docs/pe25_poster.pdf). Should you require further information, please consult these resources.

The developer team is composed by 4 students from the Bachelor of Informatics and Computing Engineering at FEUP: 

| Member | Email |
|-------------------------------------|-----------------------------------------------------|
| José Luís Cunha Rodrigues           | [up202008462@fe.up.pt](mailto:up202008462@fe.up.pt) |
| Ricardo André Araújo de Matos       | [up202007962@fe.up.pt](mailto:up202007962@fe.up.pt) |
| Rúben Costa Viana                   | [up202005108@fe.up.pt](mailto:up202005108@fe.up.pt) |
| Tiago Filipe Magalhães Barbosa      | [up202004926@fe.up.pt](mailto:up202004926@fe.up.pt) |


This team worked under the monitoring of professor Jácome Cunha ([jacome@fe.up.pt](mailto:jacome@fe.up.pt)) and Altice Labs mentor José Nuno Lapa Bonifácio ([jose-n-bonifacio@alticelabs.com](mailto:jose-n-bonifacio@alticelabs.com)).
The proposal of the project was made by Carlos Guilherme Araújo ([carlos-guilherme-araujo@alticelabs.com](mailto:carlos-guilherme-araujo@alticelabs.com)). \mypar


With the fast-paced development of the world, technologies and services in the telecommunications sector have undergone a major evolution.
In order to manage the networks, services, and operational tasks, telecommunication service providers have resourced to use platforms known as Operations Support Systems (OSS).
These systems help manage a high volume of data produced, which has to be monitored to maintain service quality and guarantee operational effectiveness.
The objective of the OSS department is to offer companies technology that raise the standard of services delivered to society.

The capacity to alert users in real-time of any network and service problems is one of the essential needs for an OSS platform.
These alerts provide the teams the chance to take quick action and avoid service interruptions.
Yet, as collaboration technologies have grown in popularity, users want to receive these notifications over a variety of platforms like WhatsApp, phone calls, SMS, Microsoft Teams and email.

It is also crucial for companies to have a fast response time to failures that might happen.
With this project, machines will be able to contact the appropriate entities when a failures happens in the product they are in.
For instance, contacting a repairman when your internet fails. 

In order to satisfy these needs, Altice Labs has suggested creating a micro-service that can send notifications across these channels to inform users and products of network and/or service problems.

The social impact of this project is substantial since it immediately enhances people's lives by guaranteeing that telecommunications services are effective, dependable, and continuous.

# Quarkus

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
