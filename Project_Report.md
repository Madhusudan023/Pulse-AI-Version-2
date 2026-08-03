# Project Report: Pulse AI Survey Platform (Black Book)

## 1. Abstract / Introduction
**Pulse AI Survey** is an advanced, microservices-based application engineered to modernize continuous employee feedback. Traditional annual surveys often fail to capture real-time organizational health. Pulse AI solves this by deploying continuous, dynamic "pulse checks" that leverage Artificial Intelligence (Google Gemini API) to generate context-aware questions and analyze the emotional tone (sentiment) of employee feedback in real-time. This project is built on a highly scalable Spring Cloud Microservices architecture and features a modern, role-based React/Vite frontend.

---

## 2. Services Used & System Architecture
The application adopts a robust microservices architecture designed for a T3-level enterprise grade environment.

### Infrastructure Services
*   **Eureka Server (`eureka-server`)**: Implements Service Discovery. Microservices register themselves here, allowing them to find and communicate with each other dynamically without hardcoded IPs.
*   **Config Server (`config-server`)**: Centralized configuration management. It stores application properties in one place, allowing configurations to be updated without rebuilding individual services.
*   **API Gateway (`api-gateway`)**: The single entry point for all frontend requests. It handles routing, load balancing, and CORS configurations.

### Core Microservices
*   **Auth Service (`auth-service`)**: Handles user authentication, credential verification, and issues JSON Web Tokens (JWT) for secure, stateless authorization.
*   **Employee Service (`employee-service`)**: Manages the organizational structure, employee profiles, and Role-Based Access Control (RBAC) levels (VP, GLOBAL_HR, REGIONAL_HR, EMPLOYEE).
*   **Question Bank Service (`question-bank-service`)**: Manages the repository of survey questions, including both static and AI-generated dynamic questions.
*   **Survey Service (`survey-service`)**: The core engine. It manages survey campaigns, dispatches them to targeted employees, and securely collects structured responses.
*   **Sentiment Service (`sentiment-service`)**: Integrates with Google's Gemini API to analyze open-ended text feedback. It categorizes employee sentiment (Positive, Neutral, Negative) using advanced NLP.
*   **Reporting Service (`reporting-service`)**: Aggregates response data and sentiment scores to generate real-time metrics and dashboards for HR and Leadership.
*   **Notification Service (`notification-service`)**: An asynchronous service that listens to events (e.g., via Kafka) to alert employees of new surveys or HR of critical feedback.
*   **Google Form Service (`google-form-service`)**: Syncs surveys to real Google Forms. Implements target audience filters (e.g., checking employee `joiningDate` to target New Joiners < 6 months or Tenured > 6 months) and handles external email dispatching using real **Gmail SMTP**. Protected heavily by Resilience4j.

#### 2. Advanced Survey Management & Question Bank
- **Theme-Based Question Filtering:** Users can easily filter the question bank by specific themes (e.g., Leadership, Work-Life Balance) using interactive checkboxes when building a survey.
- **Dynamic Flexible Rating Scales:** Likert scale questions now support customizable scoring ranges (e.g., Positive: 8-10, Neutral: 5-7, Negative: 1-4). These scales are defined on a per-question basis and can be edited before adding a question to a survey.
- **Draft Mode Editing:** Survey administrators can fully manage draft surveys, including editing question details directly from the add question modal, and removing previously added questions from the survey using the 'View Questions' interface.
- **Audience Filtering by Experience:** HR can now filter survey recipients based on their tenure. 
    - The `google-form-service` calculates the employee's tenure using `java.time.temporal.ChronoUnit.MONTHS.between` comparing `joiningDate` to `LocalDate.now()`.
    - Surveys can be targeted at employees with `< 6 months` experience or `>= 6 months` experience.

---

## 3. Internal Working & Data Flow
1.  **Authentication**: A user (Employee/HR) logs into the React frontend. The request goes through the **API Gateway** to the **Auth Service**. Upon verification, a JWT is returned.
2.  **Survey Generation**: Regional/Global HR creates a survey campaign via the **Survey Service**. They can pull questions from the **Question Bank Service**, which may have used AI to generate context-specific questions.
3.  **Distribution**: When a survey is published, the Survey Service publishes an event to Kafka. The **Notification Service** consumes this event and alerts targeted employees.
4.  **Response Collection**: Employees submit surveys. Structured answers are saved in the Survey Service database.
5.  **Sentiment Processing**: Open-ended responses are sent to the **Sentiment Service**, which calls the Gemini API to determine the sentiment score.
6.  **Reporting**: The **Reporting Service** continuously pulls data from the Survey and Sentiment services to populate real-time Dashboards (e.g., VP Dashboard showing global morale, HR Dashboard showing regional issues).

---

## 4. Important Java Files & What to Remember

When working on or reviewing this project, pay special attention to the following standard files (applicable across most of the microservices):

*   **`Application.java` (e.g., `SurveyServiceApplication.java`)**: The entry point. Remember to look for annotations like `@EnableDiscoveryClient` and `@SpringBootApplication`.
*   **`JwtAuthenticationFilter.java` (in API Gateway & Auth Service)**: Intercepts requests to validate JWTs. **Remember:** This is where security context is set. If authentication fails, check this file first.
*   **`*Controller.java` (e.g., `SurveyController.java`)**: Exposes REST APIs. **Remember:** These should be "thin" clients; they should only handle HTTP routing and delegate business logic to the Service layer.
*   **`*ServiceImpl.java` (e.g., `SentimentServiceImpl.java`)**: Contains core business logic. **Remember:** This is where the Gemini API integration code resides (making HTTP calls to Google's endpoints) and where Resilience4j `@CircuitBreaker` annotations should be applied to prevent cascading failures.
*   **`*FeignClient.java` (e.g., `EmployeeFeignClient.java`)**: Interfaces used for synchronous inter-service communication. **Remember:** Feign clients abstract away HTTP boilerplate, making API calls to other microservices look like local method calls.

---

## 5. Design Patterns Used & Why

1.  **Microservices Architecture Pattern**: 
    *   *Why:* To allow independent scaling, deployment, and development of different domains (e.g., Auth vs. Sentiment).
2.  **API Gateway Pattern**:
    *   *Why:* To provide a unified entry point, preventing the frontend from needing to know the location of every microservice. It centralizes security and routing.
3.  **Service Discovery Pattern (Eureka)**:
    *   *Why:* In a cloud environment, IP addresses change. Eureka acts as a phonebook, allowing services to find each other by name.
4.  **Circuit Breaker Pattern (Resilience4j)**:
    *   *Why:* To prevent cascading failures. If the AI/Gemini API is down, the Circuit Breaker stops the Sentiment Service from repeatedly trying and timing out, allowing the rest of the application to function normally.
5.  **Data Transfer Object (DTO) Pattern**:
    *   *Why:* To decouple the internal database entities (Hibernate/JPA) from the data exposed to the frontend. It prevents accidental exposure of sensitive fields (like passwords).
6.  **Repository Pattern (Spring Data JPA)**:
    *   *Why:* Abstracts the database layer, allowing for clean, interface-driven database interactions without writing raw SQL.
7.  **Event-Driven Architecture (Pub/Sub with Kafka)**:
    *   *Why:* Used for the Notification Service. It decouples the Survey Service from Notifications, ensuring that if the email server is slow, survey submission is not delayed.

---

## 6. What We Have Created & Internal Flow
We have engineered a complete, end-to-end feedback loop. 
*   **What we built:** A robust backend of 7 microservices, configured with Spring Cloud, secured with JWT, and backed by MySQL databases. The frontend is a highly responsive Vite/React app styled with TailwindCSS and Framer Motion.
*   **Internal Flow:** `User UI -> API Gateway -> Specific Microservice -> Database / External AI API`. Asynchronous tasks (like emails or heavy reporting analytics) are offloaded to message brokers (Kafka/RabbitMQ) to keep the UI fast.

---

## 7. What I Did Extra (Highlight for T3 Level)
To elevate this project beyond a standard CRUD application to a T3 level standard, the following advanced implementations were added:
*   **AI Integration (GenAI)**: Integrated Google Gemini LLMs for real-time unstructured data parsing and sentiment analysis, moving beyond traditional survey metrics.
*   **Resilience & Fault Tolerance**: Implemented Resilience4j Circuit Breakers and Retries on external API calls to ensure high availability.
*   **Distributed Tracing (Zipkin)**: Configured Zipkin (as seen in `docker-compose.yml`) to trace requests as they travel across multiple microservices, drastically reducing debugging time in production.
*   **Event Streaming**: Utilized Apache Kafka / Zookeeper to handle high-throughput, asynchronous event processing for notifications and data aggregation.
*   **Modern Frontend State Management**: Utilized TanStack React Query for aggressive caching and optimistic UI updates, ensuring the dashboard feels instantaneous.

---

## 8. References
1.  [Spring Cloud Microservices Documentation](https://spring.io/projects/spring-cloud)
2.  [Google Gemini API Documentation](https://ai.google.dev/docs)
3.  [Resilience4j - Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker)
4.  [Microservices Patterns by Chris Richardson](https://microservices.io/)
5.  [React Query / TanStack Documentation](https://tanstack.com/query/v5)

---

## 9. Most Asked Interview Questions on T3 Level (Deep & Detail)

### Q1: In your Microservices Architecture, how do you handle distributed transactions? (The Saga Pattern)
**Detailed Answer:** In a monolithic app, transactions are ACID-compliant and handled by a single database. In our architecture, different services have different databases. We handle this using the **Saga Pattern**. Specifically, we use Choreography or Orchestration. If a Survey creation process requires steps in the Employee Service and Notification Service, and a later step fails, we cannot simply `rollback`. Instead, we publish "Compensating Events" to Kafka, which tell the previous services to undo their specific actions, maintaining eventual consistency.

### Q2: Why did you choose API Gateway over direct client-to-microservice communication?
**Detailed Answer:** Direct communication creates massive tight coupling. The frontend would need to know the IPs of 7 different services, handle 7 different CORS policies, and manage authentication in 7 places. The API Gateway acts as a Reverse Proxy. It centralizes our JWT validation, rate limiting, and routing. It allows us to refactor our backend (e.g., splitting a service into two) without breaking the frontend, as the Gateway simply updates its routing rules.

### Q3: Explain how the Circuit Breaker pattern (Resilience4j) works in your Sentiment Service.
**Detailed Answer:** The Sentiment Service relies on a third-party API (Google Gemini). If Gemini is down or experiencing high latency, requests from our Survey Service to the Sentiment Service would pile up, eventually crashing our threads. The Circuit Breaker wraps the API call. 
*   **Closed State:** Normal operation.
*   **Open State:** If the failure rate exceeds a threshold (e.g., 50% failures in the last 10 calls), the circuit "opens." Subsequent calls fail immediately without hitting the network, saving resources. We return a fallback response (e.g., "Sentiment Pending").
*   **Half-Open State:** After a timeout, it allows a few test requests through. If they succeed, the circuit closes. If they fail, it opens again.

### Q4: How does Eureka Service Discovery actually work under the hood?
**Detailed Answer:** Eureka is a Client-Server model. We run the `eureka-server`. Every other microservice includes the Eureka Client dependency. On startup, services register their IP, Port, and Service ID (e.g., `survey-service`) with the Eureka server. They also send periodic "heartbeats" (typically every 30s). If the Eureka Server doesn't receive a heartbeat from an instance for a set time (e.g., 90s), it evicts that instance from its registry. When our API Gateway needs to route to `survey-service`, it asks Eureka for available instances and load balances among them.

### Q5: What is the N+1 query problem in Hibernate, and how did you solve it in your Repositories?
**Detailed Answer:** The N+1 problem occurs when you query a list of entities (1 query) and then access a lazily loaded relation for each entity, triggering an additional query per entity (N queries). In the Survey Service, if fetching a Survey with its associated Questions, we avoid this by using `JOIN FETCH` in our JPQL queries or by utilizing Spring Data JPA's `@EntityGraph` annotation. This ensures all required data is loaded in a single, optimized SQL `JOIN` statement rather than a blizzard of separate selects.
