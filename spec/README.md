# Harmonica Development Restart — Spec

Harmonica development was paused several years ago. This directory holds the
plan to restart and modernize the project.

## Documents

| File | Purpose |
| --- | --- |
| [`plan.md`](plan.md) | Master roadmap: goals, phases, milestones, and definition of done |
| [`tech-notes.md`](tech-notes.md) | Toolchain & dependency research (JDK / Gradle / Kotlin compatibility findings) |
| [`exposed-integration.md`](exposed-integration.md) | Design for making the Exposed ORM **optional** for users |
| [`testing.md`](testing.md) | Test strategy: merging `harmonica_test`, local DB environments, CI |
| [`issues-triage.md`](issues-triage.md) | Triage of all open GitHub issues by urgency and size |
| [`ci.md`](ci.md) | GitHub Actions, CI workflows, and Dependabot design |

## Current state (baseline, branch `develop`)

> Snapshot taken 2026-08-16, after Phase 3 (Exposed bridge, PRs #197-#199;
> script-classpath wiring, PRs #201/#202; `bin/gw` tooling, PR #203) and Phase 4
> so far (H2 embedded DBMS, PR #206; integration-test module, PR #207; Docker +
> CI db-integration, PR #209). Update this
> list when the baseline advances.

- Kotlin **2.3.20**, Gradle wrapper **9.7.0**, `jvmTarget = 1.8` (class-file
  major 52 asserted in CI)
- Gradle plugin-publish **2.1.1**, Dokka **2.2.0** (Dokka bundles Jackson
  2.15.3 at build time only — not shipped; see the Dependabot alerts)
- Publish target: plugin-publish + OSSRH staging (jcenter/bintray removed);
  JitPack vs Maven Central decision still open (plan.md §6) — OSSRH is Phase 6
  prep
- CI: GitHub Actions only — `ci.yml` (PR/push, Temurin JDK 25,
  `actions/checkout@v7` + `gradle/actions/setup-gradle@v6` +
  `actions/setup-java@v5.7.0`), `jvm8-bytecode.yml` (major-52 assertion),
  `dependency-submit.yml` (dependency-graph submission). CircleCI removed.
- Four active modules: `core`, `exposed`, `gradle-plugin`, `integration-test` —
  `./gradlew build` runs **86 tests, 0 skipped** (73 core + 8 plugin + 4 exposed
  + 1 SQLite integration-test); the **2 gated** PostgreSQL + MySQL
  integration-test cases run via `:integration-test:integrationTest` (pass with
  Docker, skip without; required CI mode fails instead); `document` (nested
  standalone build) is excluded from the build
- H2 embedded DBMS support in `core` (PR #206): `Dbms.H2` → `jdbc:h2:<dbName>`,
  `H2Adapter` complete, H2 2.4.240 test-only; in-memory DB state survives a
  failed `transaction` (connection kept open for H2), identifier case derived
  from `DatabaseMetaData`
- The `integration-test` module (PR #207) replaces the separate `harmonica_test`
  repository: SQLite embedded suite runs in `./gradlew build`; the PostgreSQL
  and MySQL suites run via `:integration-test:integrationTest` and skip when
  the DB is unreachable (Docker/CI landed in PR #209; the full `harmonica_test`
  port is DONE as of PR #2xx)
- Migration `.kts` scripts are compiled/evaluated with a direct
  `BasicJvmScriptingHost` from a `MigrationScript` `@KotlinScript` template
  (PR #202) — no JSR-223 engine — over the plugin's `harmonica` configuration
  classpath (PR #201)
- License headers stripped from all source files (PR #180); README badges fixed
  (PR #181)
- No Exposed dependency in `core`. The optional `harmonica-exposed` module
  (`exposed/`, pinned to Exposed 0.61.0) ships the `exposedTransaction` bridge
  (Phase 3, PR B); runtime reflection detection was removed from
  `gradle-plugin` (PR A) — see
  [`exposed-integration.md`](exposed-integration.md)
- Tests for real DBMS are being merged in Phase 4: the `integration-test`
  module is in the root build (SQLite always-green; PostgreSQL and MySQL gated
  and run against Docker/CI, PR #209); the plugin-flow TestKit tests (PR #219)
  verify the issue #196 with/without-Exposed migration flow; the full
  `harmonica_test` port is DONE (PR #2xx). The `demo/` module (seed for the
  plugin-flow demo and the ported 4-migration fixtures) is wired into the root
  build as a composite `includeBuild`, so `integration-test` reuses its
  migration classes via `com.improve_future:harmonica-demo:2.0.0`.
- **`develop` is 117 commits ahead of `master`** — Phase 4 (real-DB tests) must
  pass before `master` advances. See the risk register in [`plan.md`](plan.md).

## Machine environment (current)

- OpenJDK 25.0.3 (only JDK installed)
- No standalone `gradle`; **Docker installed 2026-08-15** (user added to the
  `docker` group) for the Phase 4 DB-backed tests
- Toolchain upgrade done (Phase 0, PR #183): the Gradle 9.7.0 wrapper runs on
  Java 25, so `./gradlew build` works locally.
