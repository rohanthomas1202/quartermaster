<p align="center">
  <h1 align="center">Quartermaster</h1>
  <p align="center">
    Weekly commitments that connect individual work to strategic goals.
    <br />
    <a href="docs/superpowers/specs/2026-03-22-weekly-commits-design.md"><strong>Read the design spec &rarr;</strong></a>
  </p>
</p>

<br />

## The Problem

Teams plan weekly work in a vacuum. Managers can't see whether individual commitments align with organizational strategy until it's too late. There's no structural link between what someone commits to on Monday and the Rally Cries driving the business.

## How It Works

Every weekly commitment links to an **Outcome**, which chains up to a **Defining Objective** and a **Rally Cry** — enforcing strategic alignment by design.

```
Rally Cry
  └── Defining Objective
        └── Outcome
              └── Weekly Commit Item  ← what you actually do
```

### The Weekly Lifecycle

```
 Mon 8 AM                     Fri 5 PM                    Submit
────┬──────────────────────────┬──────────────────────────┬────
    │                          │                          │
  DRAFT ──────► LOCKED ──────► RECONCILING ──────► RECONCILED
  Plan your      Heads down.    How'd it go?       Done. Incomplete
  week.          No changes.    Mark each item.    items carry forward.
```

| State | What Happens |
|-------|-------------|
| **Draft** | Add, edit, prioritize commits. Eisenhower matrix helps you focus. |
| **Locked** | Week begins. Commits are frozen — execute. |
| **Reconciling** | Friday evening: mark items Completed, Partial, or Not Done. |
| **Reconciled** | Week closed. Partial/Not Done items auto-carry to next week. |

### Eisenhower Matrix

Every commit item is tagged by **urgency** and **importance**, placing it in one of four quadrants:

```
         Urgent          Not Urgent
       ┌───────────────┬───────────────┐
  High │   Do First    │   Schedule    │
  Imp. │               │               │
       ├───────────────┼───────────────┤
  Low  │   Delegate    │   Eliminate   │
  Imp. │               │               │
       └───────────────┴───────────────┘
```

### Manager Dashboard

Read-only team visibility — who's reconciled, completion rates, and how commits distribute across Rally Cries. No micromanagement, just alignment.

## Architecture

Three independently deployable units:

```
Browser ──► Ship Host (Vite) ──► Weekly Commits Remote (Module Federation)
                                        │
                                  Ship Express API (/api/wc/*)
                                        │ proxy + auth headers
                                  Java 21 Service (Spring Boot 3)
                                        │
                                  PostgreSQL
```

| Component | Stack |
|-----------|-------|
| **Backend** | Java 21, Spring Boot 3, Spring Data JPA, Flyway, PostgreSQL |
| **Frontend** | React 18, TypeScript (strict), Vite, Zustand, Module Federation |
| **Testing** | JUnit 5, Testcontainers, Vitest, React Testing Library, MSW, Playwright |

## Project Structure

```
quartermaster/
├── service/                 # Java backend
│   ├── src/main/java/       # Controllers, services, models, repos, schedulers
│   └── src/main/resources/  # application.yml, Flyway migrations
├── web/                     # React micro-frontend
│   └── src/
│       ├── components/      # Views: CommitEntry, Locked, Reconciliation, Dashboard
│       ├── api/             # Typed API client + types
│       └── state/           # Zustand stores
└── e2e/                     # Playwright smoke tests
```

## Getting Started

### Prerequisites

- Java 21 (auto-provisioned via Gradle toolchain)
- Node.js 18+
- PostgreSQL 16
- Docker (for integration tests)

### Backend

```bash
cd service

# Create the database
createdb weekly_commits

# Run
./gradlew bootRun

# Test (unit tests only)
./gradlew test

# Test (with integration — requires Docker)
docker start   # start Docker Desktop first
./gradlew test
```

The service runs on `http://localhost:8080`.

### Frontend

```bash
cd web
npm install
npm run dev      # http://localhost:3001
npm test         # 13 tests
```

### E2E

```bash
cd e2e
npm install
npx playwright install chromium
npm test         # requires both service + web running
```

## API

All endpoints require `X-User-Id` and `X-Org-Id` headers (injected by Ship's proxy in production).

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/weekly-commits/current` | Current week for authed user |
| `POST` | `/api/v1/weekly-commits/{week}/items` | Add commit item (DRAFT only) |
| `PUT` | `/api/v1/weekly-commits/{week}/items/{id}/reconcile` | Set completion status |
| `POST` | `/api/v1/weekly-commits/{week}/submit-reconciliation` | Submit + trigger carry-forward |
| `GET` | `/api/v1/manager/team-summary?weekStart={date}` | Manager team roll-up |
| `GET` | `/api/v1/rally-cries` | List active rally cries |

See the [design spec](docs/superpowers/specs/2026-03-22-weekly-commits-design.md) for the complete API reference.

## License

Private — internal use only.
