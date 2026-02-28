# AGENTS.md

## Project Purpose
This repository contains a fullstack medical triage and booking application built for a code assignment. The user journey is:

1. Start on a landing page showing current wait time.
2. Complete a 5-question symptom assessment.
3. Submit the total score to the backend.
4. Receive a care recommendation: `Chat`, `Nurse`, or `Doctor`.
5. View available 15-minute appointment slots.
6. Confirm a booking and return to the home screen with the upcoming booking shown.

The frontend experience is a major evaluation area, but the backend scheduling logic and test coverage also matter.

## Repo Layout
- `frontend/`: React 18 + TypeScript + Vite SPA using Material UI and SCSS.
- `backend/`: Spring Boot 3 API with Java 17, H2, JPA, Actuator, and OpenAPI.
- `docs/code-assignment-2026.pdf`: Original assignment brief.
- `README.md`: Primary source of run instructions, assumptions, and implementation details.
- `docker-compose.yml`: Runs frontend and backend together.

## Product Requirements
- Questionnaire must render one question at a time with visible progress.
- Users must be able to navigate backwards without losing answers.
- A cancel action must always reset the flow back to the start.
- The frontend sends the total score to `POST /assessment`.
- The backend returns:
  - a recommendation using score bands `5-7 => Chat`, `8-11 => Nurse`, `12-15 => Doctor`
  - available 15-minute slots
- Users can select a slot and confirm via `POST /booking`.
- The UI must be responsive and handle loading and error states cleanly.

## Scheduling Rules
These are the core backend constraints and should not be changed casually:

- Clinic hours are `08:00-18:00` local time.
- Slots are 15 minutes long.
- Availability window is from now through the next 3 calendar days.
- Past slots must not be returned.
- There are 4 clinicians in the scheduling pool.
- Each clinician works at most 8 hours per day.
- Each clinician takes a 1-hour break exactly 4 hours after shift start.
- A slot is available when at least one clinician is free and not already fully booked for that slot.

Current documented assumption from `README.md`:
- Clinician start times are hardcoded as `08:00`, `09:00`, `10:00`, and `11:00`.

If scheduling behavior changes, update both implementation and `README.md`.

## API Surface
- `POST /assessment`: accepts `{ "score": number }` and returns recommendation plus available slots.
- `POST /booking`: accepts `{ "slot": string, "recommendation": "Chat" | "Nurse" | "Doctor" }` and returns a confirmation payload.
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`

## Development Commands
Frontend:
```bash
cd frontend
npm install
npm run dev
```

Frontend build:
```bash
cd frontend
npm run build
```

Backend:
```bash
cd backend
mvn spring-boot:run
```

Backend tests:
```bash
cd backend
mvn clean test
```

Full stack with Docker:
```bash
docker compose up --build
```

## Implementation Notes
- Frontend API base URL defaults to `/api`.
- Local development and Docker rely on proxying `/api` to the backend.
- Backend state is in-memory through H2, so bookings reset on restart.
- Main backend scheduling logic lives in `backend/src/main/java/com/kry/triage/service/SlotService.java`.
- Backend tests already exist for recommendation, assessment, booking, and slot logic. Preserve or extend them when behavior changes.

## Change Guidelines
- Preserve the end-to-end triage flow described in the assignment brief.
- Prefer small, focused changes over refactors that rename or reshape the whole project.
- Keep business rules aligned with the assignment and the assumptions already documented in `README.md`.
- If you change an assumption, document it in `README.md`.
- For backend logic changes, update or add tests in `backend/src/test/java/com/kry/triage/service/`.
- For frontend changes, maintain responsive behavior and clear loading/error states.
- Keep frontend and backend as separate applications.

## Source of Truth
When in doubt, consult these in order:

1. `docs/code-assignment-2026.pdf`
2. `README.md`
3. Existing implementation and tests

If those sources conflict, prefer the assignment brief and then reconcile the README accordingly.
