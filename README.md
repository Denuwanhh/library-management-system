# Library Management System

This project is designed to manage a library system with two main services: user management and book management. The system is built using Spring Boot and leverages Eureka for service discovery.
 
## Prerequisite 
- Java 17 or Higher Version
- Maven

## High-Level Architecture Diagram

```mermaid
graph TD
    subgraph Services
        EUREKA[lms-eureka-server:8761]
        USER[lms-user-management:8080]
        BOOK[lms-book-management:8081]
    end

    subgraph Databases
        USER_DB[(lms_user_db: H2 Database)]
        BOOK_DB[(lms_book_db: H2 Database)]
    end

    EUREKA --> USER
    EUREKA --> BOOK

    USER <--> USER_DB
    BOOK <--> BOOK_DB
```


## How to Run

You can spin up three servers by following the steps below.

1. Spin up Eureka Discovery Server
   ```
   cd lms-eureka-server
   mvn clean install spring-boot:run
   ```

2. Spin up LMS User Management
   ```
   cd lms-user-management
   mvn clean install spring-boot:run
   ```

3. Spin up LMS Book Management

   Make sure to spin up LMS Eureka & LMS User Management services before this step. Otherwise, the API integration test will fail.
   ```
   cd lms-book-management
   mvn clean install spring-boot:run
   ```

## API Documentation

Documentations are aviable in below mentioned links.

| Service               |Documentation                |                  
|----------------|-------------------------------|
|LMS User Management|http://localhost:8080/swagger-ui/index.html#/|
|LMS Book Management|http://localhost:8081/swagger-ui/index.html#/|


## Non Functional Requirements


| Component               |Comment                |                  
|----------------|-------------------------------|
|Cache|Used Cache Abstraction API to enable the cache in the spring boot application.           |           
|Database          |Used a relational database since Book & User data can be managed structured manner and that are related.          |           
|Unit Test          |Integration & Unit tests are available in the application. Used Junit & Mokito frameworks.
|Logs|Used slf4j for logs action.|
|Exception|Used `GlobalExceptionHandler` and customize exceptions to handle specific exceptions. All the exceptions are logged with HTTP status code, message & timestamp|
|Validation|Validate the domain object using `spring-boot-starter-validation`|
|Documentation|Used Swagger, open API documentation. Documentations are available in below mentioned links pattern; ```{Host}:{Port}/swagger-ui/index.html#/```|
|Resiliency|Used circuit breaker pattern to maintain the resiliency of the application|
