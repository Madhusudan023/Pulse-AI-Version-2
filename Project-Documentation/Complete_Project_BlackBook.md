# Pulse AI Survey Platform - Complete Project Black Book (Part 1)

## Chapter 1: Project Overview

### Project Objective
The objective of Pulse AI Survey is to replace legacy, point-in-time annual employee surveys with a continuous, AI-driven feedback loop. It dynamically generates contextual questions, analyzes employee sentiment in real-time, and provides role-based actionable insights to Leadership and HR.

### Business Problem
Organizations suffer from "feedback lag." By the time annual survey results are analyzed, the data is stale, and employee turnover may have already occurred. Traditional surveys also lack qualitative depth—it is difficult for HR to manually analyze thousands of free-text comments to gauge organizational morale.

### Functional Requirements
- **Role-Based Access Control (RBAC):** Distinct dashboards for Employee, Regional HR, Global HR, and VP.
- **Dynamic Survey Generation:** Ability to create surveys using static question banks and AI-generated context-specific questions.
- **Sentiment Analysis:** Automated processing of text feedback to classify sentiment as Positive, Neutral, or Negative using LLMs.
- **Real-Time Reporting:** Dashboards reflecting live participation rates and sentiment aggregation per region and globally.
- **Asynchronous Notifications:** Automated alerts for pending surveys without blocking core application flows.

### Non-Functional Requirements
- **Scalability:** Microservices architecture to scale heavy workloads (like Sentiment Analysis) independently.
- **High Availability & Fault Tolerance:** Circuit breakers to prevent cascading failures if external AI APIs are down.
- **Security:** Stateless JWT-based authentication via a centralized API Gateway.

### Technology Stack & Justification
- **Backend Framework:** Spring Boot 3.2.4 (Java 17). *Why?* Industry standard for robust, scalable enterprise microservices. Offers out-of-the-box integration with Cloud native tools.
- **Microservices Orchestration:** Spring Cloud (Netflix Eureka, Gateway, Config). *Why?* Abstract complexities of service discovery and centralized routing.
- **Database:** MySQL 8.0. *Why?* ACID-compliant relational data management suitable for structured survey and employee mapping.
- **Event Streaming:** Apache Kafka (Confluent 7.4.4) & Zookeeper. *Why?* High throughput, durable message broker for decoupling survey submissions from reporting/notification engines.
- **AI Integration:** Google Gemini API. *Why?* State-of-the-art Natural Language Processing for advanced sentiment classification.
- **Frontend:** React + TypeScript + Vite + TailwindCSS. *Why?* Fast build times, type safety, and component reusability. TanStack Query for optimal caching.
- **Resilience:** Resilience4j. *Why?* Lightweight circuit breaker for protecting external API calls.

---

## Chapter 2: Complete Folder Structure

The project follows a standard Maven multi-module architecture (logically separated by directories) tailored for Domain-Driven Design (DDD).

*   `api-gateway/`: Acts as the reverse proxy and security checkpoint.
*   `auth-service/`: Manages `UserCredential` entities and JWT generation.
*   `config-server/`: Centralized external configuration (`application.yml` provider).
*   `employee-service/`: Core domain for `Employee` entities, Roles, and Regions.
*   `eureka-server/`: The service registry where all other services announce their presence.
*   `notification-service/`: Kafka consumer that sends alerts.
*   `pulse-ai-frontend/`: The Vite/React presentation layer.
*   `question-bank-service/`: Manages predefined and AI questions.
*   `reporting-service/`: Kafka consumer aggregating data for UI dashboards.
*   `sentiment-service/`: Integrates with Gemini API for text processing.
*   `survey-service/`: The orchestrator for dispatching surveys and collecting responses.

**Naming Convention:** Packages follow standard `com.pulseai.[servicename]`. Sub-packages strictly isolate concerns: `controller`, `service`, `repository`, `entity`, `dto`, `config`, `exception`, `security`. This ensures high cohesion and low coupling.

---

## Chapter 3: Microservice Architecture

### 1. API Gateway (`api-gateway`)
*   **Purpose:** Single entry point for the frontend. Prevents clients from needing to know the IPs of backend services.
*   **Important Classes:** `ApiGatewayApplication` (Entry), `SwaggerConfig` (Aggregates OpenAPI docs).
*   **Security:** Implements CORS globally and forwards headers. In standard enterprise apps, this is where a global `GatewayFilter` validates JWTs before routing.

### 2. Auth Service (`auth-service`)
*   **Purpose:** Identity provider.
*   **Important Classes:** `AuthController` (`/login`, `/create`), `AuthService`, `JwtUtil` (Generates JWTs using HMAC256), `CustomUserDetailsService` (Loads user by email).
*   **Database:** `auth_db` containing `UserCredential` table.

### 3. Employee Service (`employee-service`)
*   **Purpose:** Manages organizational hierarchy.
*   **Important Classes:** `EmployeeController`, `InternalEmployeeController` (For inter-service calls), `EmployeeService`, `AuthFeignClient` (Communicates synchronously with Auth Service to create credentials when a new employee is hired).

### 4. Survey Service (`survey-service`)
*   **Purpose:** Core business engine. Creates surveys and records answers.
*   **Kafka Producer:** Produces events to `survey-events-topic` when a survey is published or completed.

### 5. Sentiment Service (`sentiment-service`)
*   **Purpose:** Consumes text responses, hits Gemini API, and produces sentiment scores.
*   **Kafka Consumer:** Listens to new text responses.
*   **External API:** Uses `RestTemplate` or `WebClient` to call Google Gemini.

### 6. Reporting Service (`reporting-service`)
*   **Purpose:** Aggregates data for fast dashboard rendering (CQRS pattern).
*   **Kafka Consumer:** Listens to `sentiment-events` and `survey-events` to update materialized views of participation rates.

---

## Chapter 4: Deep Code Walkthrough

### Example: `AuthController.java`
**Logic:**
```java
@PostMapping("/login")
public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request)
```
1.  **Dependency Injection:** Uses `@RequiredArgsConstructor` (Lombok) to inject `AuthService`. This promotes constructor injection, which makes classes easier to mock in unit tests.
2.  **Validation:** Relies on Spring Validation (though omitted in raw snippet, typical T3 expects `@Valid`).
3.  **Service Delegation:** Calls `authService.login(request)`.
4.  **Response Construction:** Wraps the output in a generic `ApiResponse<T>` to maintain a consistent JSON contract (`success`, `message`, `data`, `traceId`) across all microservices.

### Example: `AuthFeignClient.java` (Inside Employee Service)
**Logic:**
```java
@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AuthFeignClient {
    @PostMapping("/api/v1/auth/create")
    ApiResponse<Void> createCredential(@RequestBody CreateCredentialRequest request);
}
```
*   **Why it's used:** Instead of writing boilerplate `RestTemplate` HTTP calls, OpenFeign dynamically generates an implementation at runtime.
*   **Annotations:** `@FeignClient(name = "auth-service")` tells Feign to ask Eureka for the IP of `auth-service` and load balance requests automatically.

---

## Chapter 5: Spring Boot Internal Working

When a microservice (e.g., `EmployeeServiceApplication`) starts via `SpringApplication.run()`:
1.  **`@SpringBootApplication`**: A meta-annotation combining `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
2.  **Component Scan**: Spring scans `com.pulseai.employeeservice.*` for classes annotated with `@Component`, `@Service`, `@Repository`, `@RestController`.
3.  **IoC & DI**: Spring's Inversion of Control container instantiates these classes as Singleton Beans and injects dependencies via constructors.
4.  **Auto-Configuration**: Spring detects `mysql-connector-java` and `spring-data-jpa` on the classpath and automatically creates a `DataSource`, `EntityManagerFactory`, and `TransactionManager`.
5.  **Tomcat Initialization**: Embedded Tomcat starts on port 8089. `DispatcherServlet` is registered to intercept all incoming HTTP requests and route them to the appropriate `@RequestMapping` in `EmployeeController`.

---

## Chapter 6: API Internal Flow

### Scenario: Employee Login
1.  **Client Request:** React sends `POST /api/v1/auth/login` to `API Gateway (8080)`.
2.  **Gateway Routing:** Gateway checks its route definitions, asks Eureka for `auth-service`, and forwards the request to port `8088`.
3.  **Controller:** `AuthController` receives the DTO `LoginRequest`.
4.  **Service/Security:** `AuthService` delegates to Spring Security's `AuthenticationManager` which calls `CustomUserDetailsService`.
5.  **Repository:** `UserCredentialRepository` executes `SELECT * FROM user_credentials WHERE email = ?`.
6.  **Token Generation:** If `BCryptPasswordEncoder` verifies the hash, `JwtUtil` generates a JWT containing the user's `employeeId`, `role`, and `region` as claims.
7.  **Response:** The JWT is wrapped in `ApiResponse` and serialized to JSON via Jackson, returning HTTP 200 OK back through the Gateway to React.

---

## Chapter 7: Database Architecture

The architecture uses a **Database-per-service** pattern to ensure loose coupling.

### Key Entities
1.  **`BaseEntity`**: Annotated with `@MappedSuperclass` and `@EntityListeners(AuditingEntityListener.class)`. Provides `id`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy` to all tables automatically via Spring Data JPA Auditing.
2.  **`Employee` (in `employee_db`)**:
    *   `employeeCode` (UK), `email` (UK), `managerId` (Self-referencing FK).
    *   Enums: `Department`, `Region`, `Role`.
3.  **`UserCredential` (in `auth_db`)**:
    *   `employeeId` (Logical FK to Employee db), `password` (BCrypt hash).

*Why this design?* Separating Auth and Employee databases ensures that if the Employee service is overwhelmed by dashboard queries, the Auth service can still process logins efficiently.

---

## Chapter 8: Kafka Event Streaming

Kafka is used to decouple heavy processing from fast HTTP requests.

*   **Topics:** Used for publish-subscribe messaging.
*   **Producer:** `SurveyService` publishes an event when a survey closes.
*   **Consumer:** `ReportingService` (Group ID: `reporting-service-group`) listens to this topic.
*   **Serialization:** Events are serialized to JSON (using Kafka's `StringSerializer` or `JsonSerializer`) before being written to the broker.
*   **Advantage:** If `ReportingService` goes down, `SurveyService` doesn't fail. Kafka holds the messages. When `ReportingService` restarts, it picks up exactly where it left off (using consumer offsets).

---

## Chapter 9: Security

Security is implemented at two levels:
1.  **Perimeter Security:** The API Gateway restricts cross-origin requests (CORS).
2.  **Service-Level Security:** 
    *   `SecurityConfig.java` defines the `SecurityFilterChain`.
    *   `JwtAuthenticationFilter.java` intercepts requests (extending `OncePerRequestFilter`).
    *   It extracts the `Bearer` token from the `Authorization` header, parses it using `Jwts.parser()`, and validates the signature.
    *   It creates a `UsernamePasswordAuthenticationToken` and sets it in the `SecurityContextHolder`.
    *   Method-level security (`@PreAuthorize("hasAnyRole('VP', 'GLOBAL_HR')")`) is used in Controllers to ensure RBAC.
# Pulse AI Survey Platform - Complete Project Black Book (Part 2)

## Chapter 10: Design Patterns

1.  **Microservices Architecture Pattern:** Splits the application into 7 domains. *Advantage:* Independent scaling and deployment.
2.  **API Gateway Pattern:** Centralized routing (`api-gateway`). *Advantage:* Simplifies frontend integration; hides internal microservice structure.
3.  **Service Registry Pattern (Eureka):** Dynamic IP resolution. *Advantage:* Prevents hardcoded IPs in a cloud environment where containers restart.
4.  **Database-per-Service Pattern:** Isolated MySQL DBs for Auth, Employee, etc. *Advantage:* Enforces bounded contexts.
5.  **Event-Driven Architecture (Publisher-Subscriber):** Kafka implementation. *Advantage:* Asynchronous decoupling of Notifications and Reporting.
6.  **Data Transfer Object (DTO) Pattern:** E.g., `LoginRequest`, `EmployeeResponse`. *Advantage:* Prevents exposing internal JPA Entities (`Employee.java`) directly to the client, preventing mass assignment vulnerabilities.
7.  **Repository Pattern / DAO:** Spring Data JPA (`EmployeeRepository`). *Advantage:* Abstracts complex JDBC and SQL into simple interface methods.
8.  **Singleton Pattern:** Spring manages `@Service` and `@Controller` classes as Singletons by default to conserve memory.

---

## Chapter 11: Calculations

Calculations primarily happen in the **Reporting Service** and frontend dashboards:
*   **Participation Percentage:** `(Total Surveys Completed / Total Surveys Dispatched) * 100`. Calculated dynamically via SQL aggregates (`COUNT()`) grouped by `region` or `businessUnit`.
*   **Sentiment Score Calculation:** The Gemini API returns a classification (Positive, Neutral, Negative). The Reporting service calculates the percentage distribution: `(Total Positive Answers / Total Answers) * 100`.
*   **Manager Roll-up:** Using the `managerId` in the `Employee` table, the system can recursively aggregate sentiment scores for a specific VP's entire downline org structure.

---

## Chapter 12: Important Java Files

Based on core business logic, the most critical files to study are:

1.  **`EmployeeController.java` (Employee Service):** Core REST API. High interview value for understanding RBAC (`@PreAuthorize`) and CRUD operations.
2.  **`JwtAuthenticationFilter.java` (Auth Service / Microservices):** Crucial for understanding how Spring Security intercepts HTTP requests and parses JWT claims.
3.  **`AuthFeignClient.java` (Employee Service):** Best file to explain synchronous inter-service communication and Spring Cloud OpenFeign.
4.  **`GlobalExceptionHandler.java` (Across all services):** Uses `@RestControllerAdvice`. Essential for explaining how Java exceptions (`ResourceNotFoundException`) are translated into clean HTTP 404 JSON responses.
5.  **`SecurityConfig.java` (Auth Service):** Defines the `SecurityFilterChain`, disables CSRF (since JWTs are immune), and configures stateless session management.

---

## Chapter 13: Configuration Files

1.  **`application.yml` (Local config):** Every service has one. Defines `server.port`, `spring.datasource.url`, `eureka.client.service-url`, and `kafka.bootstrap-servers`.
2.  **Config Server Repository:** In a production setup, `config-server` reads from a Git repository, allowing you to change properties (like database passwords) dynamically without rebuilding the `.jar`.
3.  **CORS Configuration:** Handled in the API Gateway `application.yml` (`spring.cloud.gateway.globalcors`). It allows `http://localhost:5173` (Vite) to make requests, avoiding browser CORS errors.
4.  **Swagger/OpenAPI:** Configured via `@OpenAPIDefinition` in `SwaggerConfig.java`. It automatically generates interactive API documentation accessible via `/swagger-ui.html`.

---

## Chapter 14: Interview Questions (T3 Level Selection)

*Note: For a full T3 interview, the panel will focus on these deep architectural concepts.*

**Q1. How did you resolve the N+1 query problem in Hibernate when fetching Employees and their Managers?**
*Answer:* By default, lazy loading collections or nested entities triggers 1 initial query and N subsequent queries. We solve this using JPQL `JOIN FETCH` or Spring Data JPA's `@EntityGraph` annotation to load everything in a single optimized SQL `LEFT JOIN`.

**Q2. What happens if the Gemini API goes down? How does the Sentiment Service handle it?**
*Answer:* We implemented the Circuit Breaker pattern using Resilience4j. If the failure rate exceeds a threshold, the circuit "opens" and immediately returns a fallback response (e.g., "Sentiment Pending") without waiting for network timeouts. This prevents thread pool exhaustion.

**Q3. Why did you choose Kafka over RabbitMQ for Notifications?**
*Answer:* While RabbitMQ is great for simple task queues, Kafka's append-only log architecture provides massive throughput and message replayability. If our Reporting Service goes down for an hour, it can restart and consume the historical events from Kafka because Kafka retains messages on disk, unlike RabbitMQ which deletes them once acknowledged.

**Q4. How do you handle distributed transactions across Employee Service and Auth Service when a new hire is created?**
*Answer:* We avoid distributed ACID transactions (Two-Phase Commit) because they cause severe locks and degrade performance. Instead, we use Eventual Consistency via the Saga Pattern. If Employee creation succeeds but Auth creation fails, we execute a compensating transaction to logically delete/deactivate the Employee record.

**Q5. Explain the internal working of your JWT Filter.**
*Answer:* `OncePerRequestFilter` intercepts the request. We extract the `Authorization` header, remove the "Bearer " prefix. We use `Jwts.parserBuilder()` with our secret key to verify the cryptographic signature. If valid, we extract the claims (roles), create a `UsernamePasswordAuthenticationToken`, and set it in `SecurityContextHolder.getContext()`.

---

## Chapter 15: System Design Discussion

**How would I improve this project for high scale?**
1.  **Caching:** Introduce **Redis**. Querying the Employee hierarchy or regional sentiment averages repeatedly hits MySQL. Caching these aggregates in Redis would slash database load by 90%.
2.  **Database Scaling:** Implement Read-Replicas for MySQL. The Reporting Service should only query the Read-Replica, leaving the Master database free for high-throughput write operations from the Survey Service.
3.  **Observability:** Integrate **ELK Stack (Elasticsearch, Logstash, Kibana)** for centralized logging. Currently, debugging requires checking 7 different service consoles. Implement **Sleuth/Zipkin** for distributed tracing to track a single request ID across all microservices.

---

## Chapter 16: T3 Interview Story

**The 5-Minute Pitch:**
"I architected Pulse AI, a microservices-based continuous feedback platform. I designed it using Spring Boot and Spring Cloud (Gateway, Eureka), breaking down monolithic survey tools into 7 distinct domains. I implemented Kafka for asynchronous event processing between the Survey and Notification domains. To provide cutting-edge value, I integrated the Google Gemini API to dynamically analyze unstructured text feedback. The entire system is secured via stateless JWTs and uses a React frontend."

**The Whiteboard Explanation:**
Draw the React client talking to the API Gateway. Draw arrows from Gateway to Eureka (for discovery) and then to the specific services. Emphasize the Database-per-service pattern by drawing cylinders under each service. Finally, draw Kafka acting as the asynchronous backbone between the Survey Service and the Reporting/Notification consumers.

---

## Chapter 17: References

1.  **Spring Cloud Architecture:** https://spring.io/projects/spring-cloud
2.  **Apache Kafka Documentation:** https://kafka.apache.org/documentation/
3.  **Google Gemini API:** https://ai.google.dev/docs
4.  **JWT Specification (RFC 7519):** https://datatracker.ietf.org/doc/html/rfc7519
5.  **Hibernate ORM User Guide:** https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html

---

## Chapter 18: Learning Guide (Request Lifecycle)

**Trace a Request: Submitting a Survey**
1.  **React (Frontend):** User clicks "Submit". Axios sends a POST request with JWT in headers.
2.  **API Gateway:** Intercepts request. `JwtFilter` validates signature. Checks Eureka for `survey-service` IP. Routes to `http://192.168.x.x:8092/api/v1/surveys`.
3.  **SurveyController:** Receives DTO. Calls `SurveyService`.
4.  **SurveyService:** Saves answers via `SurveyRepository` (Hibernate creates `INSERT INTO...`).
5.  **Kafka Producer:** `SurveyService` publishes `SurveyCompletedEvent` to Kafka.
6.  **HTTP Response:** Controller returns 200 OK. Gateway forwards back to React.
7.  **Asynchronous Background:** `ReportingService` consumes Kafka event, updates dashboard aggregates. `SentimentService` consumes text answers, calls Gemini API, updates database.
