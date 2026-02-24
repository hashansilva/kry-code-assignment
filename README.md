# kry-code-assignment

Fullstack code assignment for a medical triage and booking application.

## Project Purpose
This project implements the backend part of a triage flow where a patient:
1. Completes a 5-question assessment.
2. Receives a recommendation (`Chat`, `Nurse`, `Doctor`) based on total score.
3. Receives available 15-minute booking slots.
4. Confirms a booking.

The frontend (React + TypeScript + Material UI + SCSS) is planned but not implemented yet in this repository.

## Tech Stack
- Backend: Spring Boot 3 (Java 17)
- Runtime distribution: Amazon Corretto 17
- Persistence: In-memory H2 database (via Spring Data JPA)
- Build tool: Maven
- Testing: JUnit 5 (Spring Boot test starter)
- Containerization: Docker + Docker Compose

## Repository Structure
- `backend/` Spring Boot API (`/assessment`, `/booking`)
- `docs/` assignment brief PDF
- `docker-compose.yml` local container orchestration

## API Endpoints

### `POST /assessment`
Request:
```json
{
  "score": 11
}
```

Response shape:
```json
{
  "recommendation": "Nurse",
  "availableSlots": ["2026-02-20T09:00:00"]
}
```

Recommendation logic:
- `5-7 => Chat`
- `8-11 => Nurse`
- `12-15 => Doctor`

### `POST /booking`
Request:
```json
{
  "slot": "2026-02-20T09:15:00",
  "recommendation": "Nurse"
}
```

Response shape:
```json
{
  "confirmationId": "<uuid>",
  "slot": "2026-02-20T09:15:00",
  "recommendation": "Nurse"
}
```

## Scheduling Rules Implemented
- 15-minute slot intervals
- Clinic operating hours: `08:00-18:00` local time
- 4 clinicians total
- Each clinician works max 8 hours/day
- Each clinician has a 1-hour break exactly 4 hours after shift start
- Only future slots are returned
- Slot window is `today + next 3 calendar days`

Current clinician start-time assumption (documented in code):
- `08:00`, `09:00`, `10:00`, `11:00`

## Run Backend Locally
Prerequisites:
- Java 17 (Amazon Corretto 17 recommended)
- Maven 3.9+

Commands:
```bash
cd backend
mvn spring-boot:run
```

Backend default URL:
- `http://localhost:8080`

## Run Tests
```bash
cd backend
mvn test
```

## Run with Docker Compose
Prerequisites:
- Docker
- Docker Compose plugin

Command:
```bash
docker compose up --build
```

Service:
- Backend: `http://localhost:8080`

Stop:
```bash
docker compose down
```

## Trade-offs and Assumptions
- No external database is used; H2 in-memory storage resets on restart.
- Timezone handling uses server local time.
- No auth layer is implemented.
- Booking conflicts are handled with runtime availability checks against current booking counts.

## What to Improve Next
- Implement frontend SPA (React + TS + Material UI + SCSS) per assignment flow.
- Add integration tests for controller endpoints and booking conflict scenarios.
- Add API documentation (OpenAPI/Swagger).
- Add health checks and basic observability.
