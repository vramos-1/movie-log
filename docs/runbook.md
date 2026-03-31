# Local Deployment Runbook

## Prerequisites

| Tool | Minimum version |
|------|----------------|
| Java | 17 |
| Maven | Bundled via `mvnw` wrapper — no separate install needed |
| Node.js | 20 |

---

## Backend

### Standard run (in-memory H2, resets on restart)

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

### Dev run (file-based H2, data persists between restarts)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

When running with the `dev` profile the H2 web console is available at
`http://localhost:8080/h2-console`. Use the JDBC URL
`jdbc:h2:file:./data/movielog` with username `sa` and an empty password.

### JWT secret

The default secret in `application.properties` is a placeholder that is safe
for local development. In any deployed environment override it with an
environment variable:

```bash
export JWT_SECRET=<your-strong-secret-of-at-least-32-characters>
./mvnw spring-boot:run
```

### Run backend tests

```bash
./mvnw test
```

---

## Frontend

Install dependencies (first time only):

```bash
cd frontend
npm install
```

Start the dev server:

```bash
npm run dev
```

The app is available at `http://localhost:5173`. The Vite dev server proxies API
calls to `http://localhost:8080`.

### Run frontend tests

```bash
npm test
```

### Production build

```bash
npm run build
```

The compiled output is written to `frontend/dist/`.

---

## Running both together

Open two terminal windows:

**Terminal 1 — backend**
```bash
./mvnw spring-boot:run
```

**Terminal 2 — frontend**
```bash
cd frontend && npm run dev
```

Then open `http://localhost:5173` in your browser.
