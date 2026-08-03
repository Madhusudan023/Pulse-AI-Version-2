# Pulse AI Survey Platform - Learning Roadmap

This roadmap is designed for onboarding a mid-to-senior level Java engineer to the Pulse AI Survey microservices architecture. It traces the lifecycle of a request from end-to-end and points out the necessary concepts you must master at each stage.

## Stage 1: The Perimeter (API Gateway & Security)
**Goal:** Understand how requests enter the system and how users are authenticated.
*   **Trace Path:** Client -> `API Gateway (8080)` -> `Auth Service (8088)`
*   **Key Concepts to Learn:**
    *   **Spring Cloud Gateway:** How routes are dynamically discovered using `locator.enabled: true`.
    *   **Stateless Security (JWT):** Study `JwtAuthenticationFilter.java` and `JwtUtil.java`. Understand how HMCA256 signatures prevent tampering.
    *   **Spring Security:** Understand the `SecurityFilterChain` and how `@PreAuthorize("hasRole(...)")` enforces RBAC before a controller method executes.

## Stage 2: Service Discovery & Configuration
**Goal:** Understand how 7 independent microservices find each other without hardcoded IP addresses.
*   **Trace Path:** `eureka-server (8761)` and `config-server (8888)`
*   **Key Concepts to Learn:**
    *   **Netflix Eureka:** Client-side load balancing. How the API Gateway asks Eureka for the location of `employee-service`.
    *   **Spring Cloud Config:** Centralized configuration management.

## Stage 3: Core Business Logic (Spring Boot Internals)
**Goal:** Understand the standard MVC flow inside a single microservice.
*   **Trace Path:** `EmployeeController` -> `EmployeeService` -> `EmployeeRepository`
*   **Key Concepts to Learn:**
    *   **Inversion of Control (IoC) / Dependency Injection:** Why we use constructor injection (`@RequiredArgsConstructor`) instead of `@Autowired` fields.
    *   **DTO Pattern:** How `LoginRequest` maps to `UserCredential` and why we separate them.
    *   **Exception Handling:** Study `@RestControllerAdvice` in `GlobalExceptionHandler.java`.

## Stage 4: Data Persistence (Hibernate & JPA)
**Goal:** Understand how Java objects are saved to MySQL safely and efficiently.
*   **Key Concepts to Learn:**
    *   **Spring Data JPA:** How `JpaRepository` generates SQL statements at runtime.
    *   **Auditing:** Study `BaseEntity.java` and `@CreatedDate` / `@LastModifiedDate`.
    *   **N+1 Problem:** How fetching related entities can crush database performance if not managed with `JOIN FETCH`.

## Stage 5: Inter-Service Communication
**Goal:** Understand how Microservice A talks to Microservice B.
*   **Trace Path:** `EmployeeService` -> `AuthFeignClient` -> `AuthService`
*   **Key Concepts to Learn:**
    *   **Spring Cloud OpenFeign:** Creating declarative HTTP clients using interfaces.
    *   **Resilience4j (Circuit Breakers):** Protecting your services from cascading failures when an external API (like Gemini) goes down.

## Stage 6: Event-Driven Architecture (Kafka)
**Goal:** Understand how heavy background tasks are offloaded for asynchronous processing.
*   **Trace Path:** `SurveyService` (Producer) -> `Kafka Broker (9092)` -> `ReportingService` (Consumer)
*   **Key Concepts to Learn:**
    *   **Apache Kafka:** Topics, Partitions, Offsets, and Consumer Groups.
    *   **Saga Pattern (Conceptual):** Managing eventual consistency when you don't have a single database to rollback.

## Checkpoint: Whiteboard the System
If you have mastered the 6 stages above, you should be able to stand at a whiteboard and draw the entire `System_Architecture.drawio` diagram from memory, explaining exactly what happens when a user clicks "Submit Survey".
