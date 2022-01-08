## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

```
Java 11
Maven 3.6.0
Docker 2.0.0.3
```

### Running

#### Run kafka Server

```bash
$ cd kafka-server
$ docker-compose up
```
#### Run vertx-customers-crud microservice

```bash
$ cd vertx-customers-crud
$ mvn clean compile vertx:run
```

#### Run vertx-customers-reports microservice

```bash
$ cd vertx-customers-reports
$ mvn clean compile vertx:run
```