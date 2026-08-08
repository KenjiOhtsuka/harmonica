# Testing Strategy

## Goals

1. Real-DB integration tests currently live in `KenjiOhtsuka/harmonica_test`
   → bring them **into this repository**.
2. Make DB-backed tests runnable locally and in CI.
3. Never break `./gradlew build` when no database is available.

## Current state

- Unit tests exist in `core` and `gradle-plugin` (JUnit 6.1.2, `useJUnitPlatform`).
  They use **stubs, not real JDBC drivers**: core's DB tests go through
  `StubConnection`/`StubDbAdapter`/`StubMigration` (package-private), adapter
  tests inspect `buildColumnDeclarationForCreateTableSql` by reflection, and
  `ConnectionTest` only asserts the connection-URI string — **no DBMS is
  actually exercised by unit tests**.
- Real-DB tests (`harmonica_test`) require a live PostgreSQL/MySQL with a
  `developer/developer` user and a `harmonica_test` database; they are a
  separate Gradle project and only run manually.
- This machine: no Docker, no local DBMS yet.

## Plan

### 1. Merge `harmonica_test` into this repo

- Create an `integration-test` Gradle subproject (or a dedicated source set in
  `core`) that contains the real-DB migrations and assertions.
- Reuse the `core` + `gradle-plugin` code under test directly (same build),
  which removes the awkwardness of a separate repo publishing snapshots.
- Port the README instructions from `harmonica_test` into this repo's docs.

### 2. Local DB environments

- Install Docker, then provide `docker-compose.yml` at repo root:

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_USER: developer
      POSTGRES_PASSWORD: developer
      POSTGRES_DB: harmonica_test
    ports: ["5432:5432"]
  mysql:
    image: mysql:8
    environment:
      MYSQL_USER: developer
      MYSQL_PASSWORD: developer
      MYSQL_DATABASE: harmonica_test
      MYSQL_ROOT_PASSWORD: root
    ports: ["3306:3306"]
```

- SQLite needs no server. It is **not** covered by unit tests today (only a URI
  string is asserted); add a **Docker-free SQLite integration path** that runs
  in plain CI: `org.xerial:sqlite-jdbc` + a temp-file/`:memory:` DB, gated by
  the same env-var helper. This gives an always-green real-DB smoke test
  without Docker.
- **H2 as the Docker-free alternative**: `Connection.buildConnectionUriFromDbConfig`
  returns `""` for `Dbms.H2` and `SqlServerAdapter` is 100% TODO. Add H2
  support as an embedded (no-server) DBMS alongside SQLite; defer SQL Server /
  Oracle to a later phase (they still require services).
- Document start/stop and how to run the integration suite against them.

### 3. Gating (DB may be absent)

- Integration tests read connection info from env vars
  (`HARMONICA_TEST_DB_URL` etc.) or known local defaults.
- JUnit (6.x): use `@EnabledIfEnvironmentVariable` / an assumption helper so tests
  **skip** (not fail) when the DB is unreachable.
- The default `./gradlew build` includes unit tests only; DB tests run via a
  separate task (e.g. `integrationTest`) or a `-PwithDb` flag.

### 4. CI

- GitHub Actions: run the full suite (including DB tests) using service
  containers (Postgres/MySQL) so every merge is verified against real DBs.
- Keep a plain unit-test job as the fast path and as the JDK 8-bytecode check
  (javap major-version assertion — see [`ci.md`](ci.md)).
- SQLite/H2 tests run in the fast path (no services needed).
- Remove/replace the legacy CircleCI config once Actions is authoritative. —
  **done** (Phase 0).

### 5. Tooling

- Prefer JUnit (6.x, already in use) + Testcontainers (start DB containers from
  within tests) once Docker is available; env-var-based config remains the fallback.
- Migrate existing `harmonica_test` assertions (they are currently shell-style:
  `./gradlew jarmonicaUp` then inspect the DB) into proper assertions.

## Definition of done

- `integration-test` module exists in this repo, ported from `harmonica_test`.
- `docker-compose.yml` + docs committed; local run verified on this machine.
- `./gradlew build` passes without Docker; `integrationTest` passes with Docker.
- SQLite + H2 real-DB smoke tests are part of the always-on fast path.
- CI job runs DB-backed tests on every push.
