## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

```
Java 11
Maven 3.6.0
Docker 2.0.0.3
```

### Running

#### Run MongoDB containers

```bash
$ docker-compose up
```

#### Run vertx-customers-crud microservice

```bash
$ mvn clean compile vertx:run
```

#### Import Postman collection
Open Postman, import "customers-crud-service.postman_collection" and you should be able to create, read, update and delete 