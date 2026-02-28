# kry-code-assignment

Fullstack medical triage and booking application.

## Project Purpose
Patients complete a 5-question assessment, receive a care recommendation (`Chat`, `Nurse`, `Doctor`), see available 15-minute appointment slots, and confirm a booking.

## Tech Stack
- Frontend: React 18 + TypeScript + Material UI + SCSS + Vite
- Backend: Spring Boot 3 (Java 17, Amazon Corretto 17)
- Persistence: In-memory H2 database (Spring Data JPA)
- API docs: OpenAPI 3 + Swagger UI
- Health monitoring: Spring Boot Actuator
- Containerization: Docker + Docker Compose

## Repository Structure
- `frontend/` SPA UI and flow
- `backend/` Spring Boot API (`/assessment`, `/booking`)
- `docs/` assignment brief PDF
- `docker-compose.yml` runs frontend and backend together

## Frontend Flow
- Start page shows: `See a doctor in X mins` and `Book Meeting`
- Questionnaire: 5 questions, one at a time, with progress
- Back navigation preserves previous answers
- Always-visible cancel action resets to start
- On submit: sends score to backend `/assessment`
- Recommendation + available slots screen
- Booking confirmation via `/booking`
- Return home and show upcoming booking time

## API Endpoints

### `GET /availability`
Response shape:
```json
{
  "nextAvailableSlot": "2026-02-20T09:00:00"
}
```

This endpoint is used by the landing page to calculate the real-time `See a doctor in X mins/hrs` message from the earliest currently available slot.

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
- Clinician shifts: max 8 hours/day each
- Mandatory 1-hour break exactly 4 hours after shift start
- Only future slots returned
- Slot window: now through next 3 calendar days
- Availability also checks existing bookings for slot capacity

Current clinician start-time assumption:
- `08:00`, `09:00`, `10:00`, `11:00`

## OpenAPI 3 / Swagger
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Health Check (Actuator)
- Health endpoint: `http://localhost:8080/actuator/health`
- Exposed actuator endpoints: `health`, `info`

## Run Frontend Locally
Prerequisites:
- Node.js 20+

Commands:
```bash
cd frontend
npm install
npm run dev
```

Frontend URL:
- `http://localhost:5173`

Optional env override:
- `VITE_API_BASE_URL` (default is `/api`)

## Run Backend Locally
Prerequisites:
- Java 17 (Amazon Corretto 17 recommended)
- Maven 3.9+

Commands:
```bash
cd backend
mvn spring-boot:run
```

Backend URL:
- `http://localhost:8080`

## Run Backend Tests + Coverage
```bash
cd backend
mvn clean test
```

Coverage report:
- HTML: `backend/target/site/jacoco/index.html`
- XML: `backend/target/site/jacoco/jacoco.xml`

## Run Full Stack with Docker Compose
Prerequisites:
- Docker
- Docker Compose plugin

Command:
```bash
docker compose up --build
```

Services:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`

Stop:
```bash
docker compose down
```

## Trade-offs and Assumptions
- No external DB; H2 in-memory state resets on backend restart.
- Server local time is used for slot calculations and for defining the `today + 3 days` window.
- Clinician shifts are hardcoded to start at `08:00`, `09:00`, `10:00`, `11:00`.
- Each clinician break is fixed to exactly `+4h` after shift start for 1 hour.
- Availability rule implemented is: at least 1 clinician free for the slot after existing bookings are considered.
- The statement “4 clinicians available at any given time” is interpreted as a total pool size of 4 clinicians, not strict constant availability of 4 in every slot.
- Booking recommendation value is trusted from frontend input but validated against allowed enum values (`Chat`, `Nurse`, `Doctor`).
- No authentication/authorization layer is implemented.
- No rate limiting or anti-abuse controls are implemented.
- Frontend uses `/api` base path and proxies to backend in both local dev (Vite proxy) and Docker (Nginx proxy).

## Next Improvements
- Add frontend unit/component tests (React Testing Library).
- Add backend integration tests for REST endpoints.
- Improve scheduling model if strict constant 4-clinician concurrency is required.
