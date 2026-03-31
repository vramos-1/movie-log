# Movie Log

A full-stack web application that lets users search for movies, log personal ratings and written reviews, and browse their activity in a personal movie log dashboard.

## Features

- **Account management** — register and log in with a secure email/password account
- **Movie search** — search for movies by title and view details including synopsis and release year
- **Personal ratings** — rate any movie on a 1–5 star scale; update your rating at any time
- **Written reviews** — write, edit, and delete reviews with an optional spoiler flag
- **My Movie Log** — a personal dashboard showing all movies you have rated or reviewed, sortable by recent activity or title, with pagination
- **Protected routes** — unauthenticated users are redirected to login before accessing personal data

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, TypeScript, Vite, React Router v7 |
| Backend | Spring Boot 3, Java 17, Spring Security, Spring Data JPA |
| Database | H2 (embedded) |
| Auth | Stateless JWT with BCrypt password hashing |

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Node.js | 20+ |
| Maven | Bundled via `./mvnw` — no install needed |

---

## Running Locally

### 1. Start the backend

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

> **Note:** By default the app uses an in-memory H2 database that resets on each restart. To persist data between restarts, use the `dev` profile:
> ```bash
> ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
> ```
> With the `dev` profile, the H2 web console is available at `http://localhost:8080/h2-console`
> (JDBC URL: `jdbc:h2:file:./data/movielog`, username: `sa`, password: empty).

### 2. Install frontend dependencies (first time only)

```bash
cd frontend
npm install
```

### 3. Start the frontend

```bash
cd frontend
npm run dev
```

The app is available at `http://localhost:5173`.

---

## Running Both Together

Open two terminal windows:

**Terminal 1**
```bash
./mvnw spring-boot:run
```

**Terminal 2**
```bash
cd frontend && npm run dev
```

Then open `http://localhost:5173` in your browser.

---

## Running Tests

**Backend**
```bash
./mvnw test
```

**Frontend**
```bash
cd frontend
npm test
```

---

## Environment Variables

The following environment variables can be set to override defaults:

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | Secret key used to sign JWT tokens (min 32 chars) | Placeholder safe for local dev only |
| `JWT_EXPIRATION_MS` | Token lifetime in milliseconds | `3600000` (1 hour) |

For any non-local environment, always set `JWT_SECRET` to a strong random value:

```bash
export JWT_SECRET=your-strong-secret-of-at-least-32-characters
./mvnw spring-boot:run
```

---

## Project Structure

```
demo/
├── src/main/java/com/example/demo/
│   ├── auth/          # Registration, login, JWT filter
│   ├── movie/         # Movie search and details
│   ├── rating/        # Per-user movie ratings
│   ├── review/        # Movie reviews with ownership checks
│   ├── user/          # User profile and My Movie Log
│   └── common/        # API response envelope, exception handling
├── src/test/          # Backend integration tests
├── frontend/          # React + TypeScript frontend
│   └── src/
│       ├── api/       # API client modules
│       ├── features/  # Auth, movies, ratings, reviews, dashboard
│       └── components/# Shared layout components
└── docs/              # Architecture notes, API contract, runbook
```

---

## API Overview

All endpoints are prefixed with `/api`.

| Method | Path | Auth required | Description |
|--------|------|:---:|-------------|
| `POST` | `/auth/register` | | Create a new account |
| `POST` | `/auth/login` | | Log in and receive a JWT |
| `GET` | `/auth/me` | ✓ | Get the current user's profile |
| `GET` | `/movies/search?query=` | | Search for movies |
| `GET` | `/movies/{id}` | | Get movie details |
| `PUT` | `/movies/{id}/rating` | ✓ | Save or update a rating |
| `GET` | `/movies/{id}/rating/me` | ✓ | Get your rating for a movie |
| `POST` | `/movies/{id}/reviews` | ✓ | Post a review |
| `GET` | `/movies/{id}/reviews` | | List all reviews for a movie |
| `PATCH` | `/reviews/{reviewId}` | ✓ | Edit your review |
| `DELETE` | `/reviews/{reviewId}` | ✓ | Delete your review |
| `GET` | `/me/log` | ✓ | Get your personal movie log |

All responses use a consistent envelope:

```json
{
  "success": true,
  "data": { },
  "meta": { "page": 1, "limit": 10, "total": 42 },
  "error": null
}
```

---

## Documentation

- [docs/runbook.md](docs/runbook.md) — full local deployment guide
- [docs/api-contract.md](docs/api-contract.md) — API endpoint reference
- [docs/architecture.md](docs/architecture.md) — architecture notes
- [docs/milestones.md](docs/milestones.md) — development milestone checklist
