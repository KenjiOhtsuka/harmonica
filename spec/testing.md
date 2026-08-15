# Testing Strategy

## Goals

1. Real-DB integration tests currently live in `KenjiOhtsuka/harmonica_test`
   → bring them **into this repository**.
2. Make DB-backed tests runnable locally and in CI.
3. Never break `./gradlew build` when no database is available.

## Current state

- Unit tests exist in `core`, `exposed`, and `gradle-plugin` (JUnit 6.1.2,
  `useJUnitPlatform`). `core`/`gradle-plugin` use **stubs, not real JDBC
  drivers**: core's DB tests go through
  `StubConnection`/`StubDbAdapter`/`StubMigration` (package-private), adapter
  tests inspect `buildColumnDeclarationForCreateTableSql` by reflection, and
  `ConnectionTest` only asserts the connection-URI string — no server DBMS is
  exercised there.
- The `exposed` module runs **real embedded-SQLite transaction tests** (Phase 3,
  PR B): `ExposedMigrationTest` drives the Exposed DSL inside harmonica's
  `Connection.transaction` against a temp-file SQLite DB, covering both the
  commit path (DDL + insert persist) and the rollback path (exception inside
  `exposedTransaction` rolls back and reconnects). This is the first real-DB
  test in the repo, and it is green without Docker.
- Real-DB tests read env vars with local defaults and run in a gated suite
  (Phase 4, §3) — SQLite embedded tests run fine; PostgreSQL/MySQL skip when
  their services are absent.

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
    image: postgres:16.14
    environment:
      POSTGRES_USER: developer
      POSTGRES_PASSWORD: developer
      POSTGRES_DB: harmonica_test
    ports: ["5432:5432"]
  mysql:
    image: mysql:8.4.11
    environment:
      MYSQL_USER: developer
      MYSQL_PASSWORD: developer
      MYSQL_DATABASE: harmonica_test
      MYSQL_ROOT_PASSWORD: root
    ports: ["3306:3306"]
```

- SQLite needs no server. It **is** covered by the embedded tests in the
  `exposed` module today (Phase 3, PR B); keep an always-green SQLite path in
  CI (`org.xerial:sqlite-jdbc` + a temp-file/`:memory:` DB) and extend the
  pattern to an always-green **Docker-free SQLite integration path** for `core`
  running in `build`'s `test` task, reusing the same env-var/config helper.
- **H2 as the Docker-free alternative** (**DONE 2026-08-15, PR #206**):
  `Dbms.H2` builds `jdbc:h2:<dbName>` (`mem:` prefix gives an in-memory DB);
  `H2Adapter` is complete; the H2 driver (2.2.224) is test/integration
  runtime-only, never published from `core`. In-memory state across rollback/
  reconnect is preserved by keeping the connection open after a failed
  `transaction` for H2 (no `DB_CLOSE_DELAY` needed — the original
  `DB_CLOSE_DELAY=-1` contract was superseded); identifier case is normalized
  from `DatabaseMetaData` (`DATABASE_TO_LOWER` supported). H2 tests live in
  `core` (`H2MigrationTest`, `H2AdapterTest`). SQL Server / Oracle are deferred
  to a later phase (they still require services).
- Document start/stop and how to run the integration suite against them.

### 3. Gating (DB may be absent)

- Integration tests read connection info from env vars with known local
  defaults; precedence is env-var > default:
  - PostgreSQL — `HARMONICA_TEST_POSTGRES_HOST`/`_PORT`/`_DB`/`_USER`/`_PASSWORD`
    → `127.0.0.1`/`5432`/`harmonica_test`/`developer`/`developer`.
  - MySQL — `HARMONICA_TEST_MYSQL_HOST`/`_PORT`/`_DB`/`_USER`/`_PASSWORD`
    → `127.0.0.1`/`3306`/`harmonica_test`/`developer`/`developer`.
- Gating (default/local — optional profile): a short-timeout **connectivity
  probe** (e.g. open the JDBC connection with a 1–2 s connect timeout in a
  `@BeforeAll`; on failure, `Assumptions.abort`) so a down/absent DB **skips**
  the suite instead of failing it.
- The CI `db-integration` job (Phase 4, item 5) runs in **required** mode:
  `HARMONICA_TEST_DB_REQUIRED` set only in CI turns the probe into a
  bounded-retry check that **fails** on invalid configuration, missing
  credentials, unavailable services, or skipped DB tests — never skips.
- The always-green embedded tests run in `./gradlew build`: SQLite via the
  `integration-test` module's `test` task, H2 via `core`'s `test` task; the
  gated PostgreSQL/MySQL suite runs via the `integrationTest` task — never in
  `build`.

### 4. CI

- GitHub Actions: run the full suite (including DB tests) using service
  containers (Postgres/MySQL) so every merge is verified against real DBs.
- Keep a plain unit-test job as the fast path and as the JDK 8-bytecode check
  (javap major-version assertion — see [`ci.md`](ci.md)).
- SQLite and H2 tests run in the fast path (no services needed).
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
- `./gradlew build` runs the SQLite (integration-test `test` task) and H2
  (`core` `test` task) embedded tests and passes without Docker; `integrationTest`
  passes with Docker.
- SQLite and H2 real-DB smoke tests are part of the always-on fast path.
- CI `db-integration` job runs DB-backed tests on every push (required mode).
