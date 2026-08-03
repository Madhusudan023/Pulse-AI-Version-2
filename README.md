# Pulse AI Survey Platform

**Pulse AI Survey** is a comprehensive, microservices-based application built for the **T3 Hackathon**. It is designed to create a continuous employee feedback loop by leveraging AI to generate questions, conduct surveys, analyze sentiment, and provide role-based actionable insights across an organization.

## 🌟 Value Proposition
Traditional annual surveys are outdated. Pulse AI provides real-time, continuous pulse checks on employee sentiment, using AI to dynamically generate relevant questions and instantly analyze the emotional tone of employee feedback.

### 3. Comprehensive Question Bank
- Pre-loaded with industry-standard engagement questions.
- Questions categorized by themes (e.g., Leadership, Work-Life Balance, Growth).
- **Theme-Based Filtering**: Easily find and select questions based on their category when building a survey.
- **Dynamic Flexible Rating Scales**: Customize the interpretation of Likert scale questions (e.g., set Positive as 8-10, Neutral as 5-7, Negative as 1-4) on a per-question basis.

### 4. Advanced Survey Management
- Support for various survey types (Onboarding, Monthly Pulse, Exit).
- Experience-based targeting (e.g., employees with < 6 months vs > 6 months tenure).
- **Draft Mode Editing**: Edit question details and remove previously added questions before publishing.

## 🏗️ Architecture & Microservices
The project is built on a scalable **Spring Cloud Microservices** architecture with a modern **React/Vite** frontend.

### Infrastructure Services
- **Eureka Server** (`eureka-server`): Service Discovery and Registration.
- **Config Server** (`config-server`): Centralized configuration management.
- **API Gateway** (`api-gateway`): Single entry point for the frontend, handling routing and CORS.

### Core Microservices
- **Auth Service** (`auth-service`): Manages authentication, JWT token generation, and user credentials.
- **Employee Service** (`employee-service`): Manages employee profiles, organizational hierarchy, and role-based access (VP, GLOBAL_HR, REGIONAL_HR, EMPLOYEE).
- **Question Bank Service** (`question-bank-service`): Stores and manages survey questions, including AI-generated questions.
- **Survey Service** (`survey-service`): Core engine for dispatching surveys, tracking participation, and collecting responses.
- **Sentiment Service** (`sentiment-service`): Integrates with AI (Gemini APIs) to analyze text responses and determine underlying employee sentiment.
- **Reporting Service** (`reporting-service`): Aggregates data from surveys and sentiment analysis to populate real-time dashboards.
- **Notification Service** (`notification-service`): Handles alerts and notifications for pending surveys and important HR updates.
- **Google Form Service** (`google-form-service`): Automates the creation of Google Forms dynamically, maps them to internal surveys, applies **employee experience filtering** (e.g., >6 or <6 months), and dispatches personalized emails to targeted employees via **Gmail SMTP**.

## 💻 Tech Stack
- **Backend:** Java 17, Spring Boot 3.2.4, Spring Cloud (Eureka, Gateway, Config), Spring Security, Hibernate, MySQL.
- **Frontend:** React, TypeScript, Vite, Tailwind CSS, Framer Motion, Lucide Icons, Axios, TanStack React Query.
- **Resilience:** Resilience4j (Circuit Breakers, Retries) applied to Gemini AI and Google Forms external APIs.
- **AI Integration:** Google Gemini API for NLP and Sentiment Analysis.

---

## 🚀 Development Phases (T3 Hackathon Journey)

### Phase 1: Foundation & Infrastructure Setup
- Initialized the Spring Boot parent POM with necessary dependencies.
- Created the **Eureka Server** for service discovery.
- Set up the **Config Server** to manage application properties centrally.
- Built the **API Gateway** to route traffic and handle cross-origin requests from the frontend.
- Created the **Auth Service** with JWT generation and Spring Security.

### Phase 2: Core Domain Services
- Developed the **Employee Service** to handle user roles and organizational mapping.
- Built the **Question Bank Service** for managing predefined survey questions.
- Built the **Survey Service** to manage survey campaigns, assign them to employees, and collect structured responses.

### Phase 3: AI Integration & Advanced Analytics
- Integrated Google's Gemini API into the **Sentiment Service** to automatically parse open-ended feedback and categorize the sentiment (Positive, Neutral, Negative).
- Developed the **Reporting Service** to calculate participation rates, aggregate sentiment scores, and generate region-specific or global HR reports.
- Created the **Notification Service** to alert employees of new surveys.

### Phase 4: Frontend Development & Role-Based UI
- Initialized the **Vite + React + TypeScript** frontend.
- Designed a stunning, glassmorphism-inspired UI with smooth Framer Motion animations.
- Implemented **Role-Based Dashboards**:
  - **Employee Dashboard:** View pending surveys and complete them.
  - **Regional HR Dashboard:** Manage regional surveys, approve AI questions, and view regional sentiment.
  - **Global HR Dashboard:** Oversee all regions, add Regional HRs, and view company-wide analytics.
  - **VP Dashboard:** High-level strategic overview and region-by-region comparisons.

### Phase 5: Integration, Refinement & Bug Fixing
- **Microservice Communication:** Implemented OpenFeign clients for inter-service communication (e.g., Employee Service talking to Auth Service to create credentials).
- **Frontend Integration:** Hooked up the React frontend to the API Gateway.
- **Accessibility & UX Improvements:** Addressed Lighthouse accessibility warnings, improved form labels, and fixed React Query caching issues to ensure instantaneous UI updates when new users are created.
- **Final Polish:** Added dedicated pages for Regional HR management and ensured smooth routing.

---

## 🛠️ How to Run

1. **Start the Infrastructure:** 
   Run `ConfigServerApplication` followed by `EurekaServerApplication`.
2. **Start the Core Services:** 
   Run `ApiGatewayApplication`, `AuthServiceApplication`, and `EmployeeServiceApplication`.
3. **Start the Domain Services:** 
   Run `QuestionBankService`, `SurveyService`, `SentimentService`, etc.
4. **Start the Frontend:**
   ```bash
   cd pulse-ai-frontend
   npm install
   npm run dev
   ```

*Built with ❤️ for the T3 Hackathon.*
