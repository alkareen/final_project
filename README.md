Task Manager — Task Management System
==========================================

**Final project in the discipline "Development of backend applications in Java using Spring Boot"**

Project Description
----------------

Task Manager is a backend service (REST API) for managing tasks with support for a role-based access model.

Users can create projects, tasks, assign performers, add categories (tags), and leave comments.The system provides strict verification of access rights depending on the user's role.

### Main features

* **Authentication and authorization** by JWT token

*   **Three user roles**:

    * **USER** — works only with their own projects and tasks

    * **MANAGER** — can create tasks in any projects, assign performers, change status and priority

    * **ADMIN** — full access + user management (role assignment, blocking)

* **CRUD operations** for the following entities:

    * Projects (Project)

    * Tasks — with fields: name, description, status, priority, deadline, performer

    * Categories (Category) — tags for tasks (ManyToMany link)

    * Comments (Comment) — on a specific task

* **Admin Panel** - view all users, change roles and activity status


Technology stack
--------------------

*   **Spring Boot 4.0.1**

*   Spring Security + JWT (JJWT)

*   Spring Data JPA (Hibernate)

* **Liquibase** — database migration management

* **PostgreSQL** — the main database management system

* Lombok — reduction of boilerplate

* MapStruct — mapping between Entity and DTO

* JUnit 5 + Mockito — unit-tests

* **Docker + docker-compose** — containerization


Architecture
-----------

The project is built according to the classical multilayer architecture:


`   Controller → DTO → Service → Mapper → Repository → Entity   `

* **Controller** — processing HTTP requests

* **Service** — business logic and access rights verification

* **Mapper** (MapStruct) — conversion between Entity and DTO

* **Repository** (Spring Data JPA) — working with a database

* **Entity** — database models

* **Security** — JWT filter and role configuration


Database structure
---------------------

* users — users (email, password, roles, enabled)

* project — projects (name, description, created\_by)

* task — tasks (title, description, status, priority, deadline, project\_id, assignee\_id, created\_by)

* category — categories/tags

* task\_category — connecting table (ManyToMany between task and category)

* comment — comments (text, task\_id, author\_id)


All connections are implemented with foreign keys and CASCADE deletion.

Project Launch
--------------

### Option 1: With Docker (recommended)

` # Launching containers from the main and test database docker-compose up -d # Launching the mvn spring-boot:run application `

The application will be available at: [**http://localhost:8080 **](http://localhost:8080/?referrer=grok.com )

### Option 2: Without Docker

Requires running PostgreSQL on port 5432 with the taskmanager database.

`   mvn run   `

Testing
------------

`   ./mvnw test   `

* A separate test database is used (port 5433)

* All major services are covered by unit tests:

    *   AuthService

    *   ProjectService

    *   TaskService

    *   CategoryService

    *   CommentService

    *   AdminService

* Tests check both successful scenarios and prohibited actions (403)


API Demonstration
----------------

It is recommended to use **Postman** for testing and demonstration.

Collection: TaskManager - Full Collection.json (in the /postman folder)

In the collection:

* Folders by entity: Auth, Admin, Project, Task, Category, Comment

* Examples of requests with tokens of different roles

* Examples of access errors (403 Forbidden)


Author
-----

Boranbay Zangar, Software Engineering 3227-8, 3'd year student, December 2025