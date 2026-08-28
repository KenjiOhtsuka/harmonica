# Harmonica — Master Plan

Restart development of Harmonica (Kotlin DB migration tool) after a pause of
several years. This plan is the single source of truth for the restart.

## 1. Goals

1. Make the project build and test green again on a modern toolchain.
2. Remove license header comments from all source files.
3. Upgrade all library versions to current, maintained releases.
4. Make the Exposed ORM an **optional** user choice (no compile-time dependency
   on Exposed in `core`; users opt in).
5. Bring real-DB tests into this repository (merge `harmonica_test`).
6. Publish via JitPack (jcenter/bintray are dead) and keep the Gradle plugin on
   the Plugin Portal.
7. Work through the GitHub issue backlog, quick wins first.
8. Prepare local DB environments for development/testing.

## 2. Constraints

- Target bytecode for the published library: **JVM 8** (`jvmTarget = 1.8`).
- The only JDK on this machine is **OpenJDK 25** → Gradle must run on Java 25.
- Kotlin version: **2.3.20** (chosen by maintainer). Note: Kotlin 2.3 no
  longer supports `-language-version=1.8` (source level), but `jvmTarget =
  1.8` bytecode remains supported — verified in Phase 0 with no deprecation
  warning under 2.3.20 (only the old `kotlinOptions` DSL is deprecated, which
  this build doesn't use).
- Docker/Compose is a dev+CI dependency (Phase 4, item 5) — DB-backed tests
  must not break local builds when the daemon is absent.
- Do not force Exposed onto users; their migration classpath decides.

## 3. Branch & release strategy (master is stale)

State:

- `master` (origin/master @ `3b423c6`) has not been touched in years and is the
  direct ancestor of `develop` (merge-base == master HEAD).
- `develop` is **117 commits ahead** (module split into `core`/`gradle-plugin`,
  jcenter removal work, MIT license change, CI-action bumps, Phase 3 Exposed
  bridge, Phase 4 start, etc.) but **never fully tested or released** — this is
  why master was left behind.

Policy going forward (Git Flow, simplified):

1. `develop` is the integration branch; feature/phase branches are cut from it.
2. A release branch/tag is created from `develop` only when CI is fully green
   and manual DB tests pass.
3. The first milestone of this restart is: make `develop` build green and
   DB-backed tests (Phase 4) pass → then fast-forward `master` to `develop` →
   tag the first new release there. (Because `master` is an ancestor, this is a
   clean fast-forward, no merge conflict risk.)
4. `master` becomes the source of released tags (JitPack builds from tags).
5. Old branches on the remote (`feature/core_split`, `feature/maven-plugin`,
   `feature/version_up`, `feature/exposed`, `feature/show_sql`,
   `feature/split`) are either resurrected as issues or deleted after review.
   (`feature/mit-license` was merged via PR #166 and is gone.)

## 3.5. Risk register — unverified changes already on `develop`

`develop` contains changes that were merged with limited or no testing:

- **Module split** (`feature/split`, PR #161): the project was split into
  `core`, `gradle-plugin`, and `document`. The maintainer merged it based on
  review rather than thorough testing. This is the biggest untested change and
  may carry API/packaging/plugin breakage.
- **jcenter removal + POM/publication work** (commits after the split): build
  config was edited to remove bintray/jcenter, but never verified end-to-end
  (see issue #167, which surfaced later).
- **Gradle Groovy → Kotlin DSL migration** (included in the split work).

Consequences for the restart:

- Treat all of `develop` as **unverified baseline**. Phase 0 must get the split
  build green and prove `core`, `gradle-plugin`, and `document` all build,
  test, and package correctly.
- Do not assume the module split is correct as-is; budget time in Phase 0 to
  fix packaging, plugin application, and publication.
- These unverified changes are the reason `master` was left behind — do not
  fast-forward `master` until Phase 0 is green and DB tests (Phase 4) pass.
- **Status (post-Phase 0/2):** the split build is verified — `core` and
  `gradle-plugin` build, test (72 tests green; snapshot before the Phase 3
  `exposed` module — see spec/README for current test counts), and package correctly on
  Java 25 and in CI; `document/` is excluded from the build. Phase 0 also proved
  the POM/publication config (PR #183) and the Groovy→Kotlin DSL migration. The
  remaining unverified risk is real-DB behavior, which Phase 4 covers.

## 4. Phases

Each phase lands as its own branch + PR into `develop`. Definition of done for
each phase: `./gradlew build` passes on this machine (Java 25) with the phase's
changes, and CI is green.

### Phase 0 — Toolchain upgrade & CI (blocking, do first)

Outcome: project compiles and tests on Java 25; CI works.

Status: **implemented and merged (2026-08-01, PR #183, merge commit
`69344da`).** `./gradlew clean build` is green locally on Java 25 and in CI.

- Upgrade Gradle wrapper to **9.1.x or newer** — done: **9.7.0** (wrapper jar/
  scripts regenerated via `./gradlew wrapper`). Bootstrap trick used: the old
  6.5 wrapper jar can still download a new distribution, so
  `gradle/wrapper/gradle-wrapper.properties` was hand-edited first.
- Upgrade Kotlin to **2.3.x** — done: **2.3.20**; `jvmTarget = 1.8` set via
  `kotlin.compilerOptions { jvmTarget.set(JvmTarget.JVM_1_8) }` +
  `java.sourceCompatibility/targetCompatibility` in `core` and
  `gradle-plugin`. Verified: no deprecation warning for `jvmTarget 1.8`, and
  all main classes are class-file major 52.
- Rewrite `build.gradle.kts` for modern Gradle/Kotlin DSL — done: root now a
  `plugins {}` block (`kotlin("jvm") 2.3.20 apply false`, plugin-publish
  **2.1.1**, dokka   **2.2.0**); `settings.gradle.kts` has `pluginManagement`;
  `document/` removed from `include(...)`.
- **Source modernization (compile-blocker on Gradle 9)** — done:
  `JarmonicaPlugin.kt` uses `JavaPluginExtension` + `tasks.register` (no
  `JavaPluginConvention`/`conventionMapping`/`groovyClosure`); `HarmonicaPlugin.kt`
  uses `tasks.register(name, Class) { task -> ... }`. Gradle 9-specific: the
  Jarmonica task subclasses are declared `abstract` (`JavaExec` is abstract in
  Gradle 9) and every task type carries `@DisableCachingByDefault`
  (`validatePlugins` requires a caching annotation).
- **Script engine migration** — done: `kotlin-script-runtime` +
  `kotlin-script-util` replaced by `kotlin-scripting-jsr223:2.3.20`
  (`kotlin-script-util` no longer exists for 2.3.x). The engine class moved to
  `kotlin.script.experimental.jsr223`; `AbstractMigrationTask` now uses plain
  `javax.script.ScriptEngine` + `getEngineByName("kotlin")`, and the plugin's
  committed `META-INF/services/javax.script.ScriptEngineFactory` was **deleted**
  (it pointed at the removed `org.jetbrains.kotlin.script.jsr223.*` factory; the
  dependency registers its own). **Superseded 2026-08-11 (PR #202):** the
  JSR-223 engine was replaced by a direct `BasicJvmScriptingHost` — see the
  decision in §6 and Phase 3 status.
- **Coordinates/IDs**: still open. The legacy `pluginBundle` block (published
  id `com.improve_future.harmonica`) was removed by the plugin-publish 2.x
  migration; applied/published ids are now `harmonica`/`jarmonica`. The stale
  descriptor `META-INF/gradle-plugins/com.improve_future.harmonica.properties`
  is still bundled. Reconcile in Phase 6.
- **`document/` module**: decided — **dropped from the root build**, folder
  left as-is (own Gradle 4.9 wrapper, version-less Kotlin plugin, deprecated
  `mainClassName`). No longer compiled or released. Future: convert or remove
  (Phase 7).
- Fix CI — done, part of this PR (see [ci.md](ci.md)): `ci.yml` (PR-driven,
  JDK 25, `./gradlew build`, dependency-graph submission) + `jvm8-bytecode.yml`
  (javap major-version 52 check) replace `gradle.yml` and `.circleci/config.yml`
  (both deleted). `.github/dependabot.yml` already landed (#169).
- After green: fast-forward `master` to `develop` **deferred** — policy is to
  wait until Phase 4 DB tests pass (see §3.5 and Phase 4).

### Phase 1 — License header removal

Outcome: all source files have no license comment header; LICENSE/README keep
the MIT notice.

Status: **landed 2026-08-01.** License headers stripped from all 87 files
(86 source files + 1 extensionless `META-INF/services/...` registration file;
PR #180; a strip script was drafted but removed before merge (commit
`5e191b9`); README badges fixed (PR #181 — MIT URL → opensource.org, bintray
badge → JitPack). Remaining jcenter reference: `document/.../JarmonicaView.kt:61` (doc-string only).

Original plan (kept for reference):

- Remove the 7-line MIT header block from every `.kt`, `.kts`, `.gradle`,
  `.properties`, and resource file (86 source files; do not touch generated
  `docs/api/**`).
- Do not touch `LICENSE` (repo-level license file stays).
- Add a small script (e.g. `scripts/strip-license-headers.sh`) or use `sed`
  in one commit; keep it reproducible.
- Fix README badges: bintray badge → JitPack badge; MIT badge URL points at
  GPL-3.0 — fix to the MIT URL (issue #165 cleanup).

### Phase 2 — Dependency upgrades

Outcome: no dead repositories or unmaintained libraries in the build.

Status: **merged 2026-08-01 (PRs #184-#188).** All build files now use only
Maven Central (`document/` excluded from build). 72 tests green (68 core + 4
gradle-plugin; Phase 0 baseline was 66; `ScriptClasspathTest` later added 2
  plugin tests (PR #201) — see spec/README for current test counts).

- `gradle.properties`: `kotlin_version` → 2.3.x. — **done in Phase 0** (2.3.20).
- Dead repositories: **gone** — jcenter/bintray/space removed; jitpack removed
  from `core` and `gradle-plugin` with pluralizer removal (PR #187).
- `core`:
  - `kotlin-pluralizer` (jitpack, unmaintained, issue #140) → **removed**;
    internal `singularize()` ported into `core/.../table/Inflections.kt`
    (PR #187, decision in §6).
  - `commons-codec` 1.15 → **dropped** (no usage; PR #184).
  - JUnit Jupiter 5.4.2 → **6.1.3** (PR #186; decision in §6);
    `kotlin-test` → current (**done in Phase 0**, pinned to 2.3.20).
- `gradle-plugin`:
  - `kotlin-script-runtime`/`kotlin-script-util` → `kotlin-scripting-jsr223` —
    **done in Phase 0** (JSR-223 migration); **superseded 2026-08-11 (PR #202)**
    by the direct `BasicJvmScriptingHost` — see Phase 3 status and §6.
  - `org.reflections` 0.9.11 → **replaced** with a lightweight classpath
    scanner in `JarmonicaTaskMain` (PR #185; `loadClass` narrowed to
    recoverable exceptions in PR #188 — remaining `isSubtypeOf` `catch
    (Throwable)` tracked in issue #189).
  - `kotlinx-html-jvm` 0.7.3 → **dropped here** (unused in this module; only
    `document` uses it — upgrade there, Phase 7).
  - `kotlin-reflect` → **dropped** (only stdlib `KClass` usage; PR #184).
  - `kotlin-test`/`kotlin-test-junit5` are **unversioned** here — pin to 2.3.x.
    **done in Phase 0** (now pinned).
  - JDBC test drivers (mysql 5.1.44, postgresql 9.4.1212.jre6, sqlite 3.21.0.1,
    mssql 6.2.1.jre7) → **removed** (unused by any test; PR #184); integration
    DB drivers move to the Phase 4 integration module.
  - `com.gradle.plugin-publish` 0.9.10 → current 1.x; Dokka 0.9.17 → 2.x.
    **done in Phase 0** (plugin-publish 2.1.1, dokka 2.2.0).
- `document` module: see Phase 0 — convert to a proper subproject (upgrade its
  own wrapper, pin Kotlin, fix `mainClassName`) or drop it from the release.
  Upgrade `kotlinx-html-jvm` 0.7.3 → latest there; drop `groovy-all` 2.3.11 if
  unused after conversion.

### Phase 3 — Exposed optionality

Outcome: `core` has zero Exposed references; users decide whether to use
Exposed, and integration is documented. Full design: [exposed-integration.md].

Status: **merged 2026-08-09 (PRs #197, #198, #199).** `core` has zero Exposed
references (PR A removed the dead `hasExposed` flag and exposed `jdbcConnection`
on `ConnectionInterface`). The `exposed/` module (`harmonica-exposed`, pinned to
Exposed 0.61.0) ships the `exposedTransaction` bridge (Option A transaction
ownership via a no-op commit/rollback/close proxy; `WeakHashMap`-cached
`Database` per `Connection`; `defaultMaxAttempts = 1`) with 4 SQLite tests:
commit, rollback, reconnect, and SQLException propagation through the proxy
(exceptions unwrapped from `InvocationTargetException` so they keep their
`SQLException` type). **78 tests green** (68 core + 6 plugin + 4 exposed) by the
  end of Phase 3 — see spec/README for current test counts.
Script-classpath wiring for `.kts` migrations (Pitfall F) shipped **2026-08-11
(PRs #201, #202)**: the plugin evaluates `.kts` scripts directly with
`BasicJvmScriptingHost` from a `MigrationScript` `@KotlinScript` template (no
JSR-223 engine), and `ScriptClasspathTest` (2 tests) covers the classpath.
Remaining in Phase 3: a demo project against a real DB (SQLite here;
PostgreSQL/MySQL deferred to Phase 4) — built locally but not yet merged.
Plan:

- Wire the script classpath so `.kts` migrations can reach `harmonica-exposed`
  and Exposed (Pitfall F) — **DONE** (PRs #201/#202; the direct scripting host
  loads the base class from the plugin classloader and derives script
  dependencies from the thread context classloader over the `harmonica`
  configuration).
- Demo project compiling a migration using the Exposed DSL and running it
  against a real DB (SQLite here; PostgreSQL/MySQL deferred to Phase 4).
- Issue #91 was closed at the merge (2026-08-09); close #80 and the concern
  behind #160 once the flow ships (document, upgrade, reflect).

### Phase 4 — Tests & DB environment

Outcome: real-DB integration tests live in this repo; runnable locally and in CI.

**Status: complete (2026-08-28).** All five items have landed: item 1
(integration-test module, PR `#207`), item 2 (full `harmonica_test` port, PR
`#221`), item 3 (H2 embedded path, PR `#206`), item 4 (plugin-flow TestKit
tests, PR `#219`, merged), and item 5 (Docker + CI, PR `#209`).
Docker/Compose is a reproducible
dev+CI dependency (item 5, [`ci.md`](ci.md) §3.1), not a machine-local detail.
Breakdown (each item is its own small PR against `develop`):

1. **Integration module scaffold** (`integration-test` subproject, per
   [`testing.md`](testing.md)). JUnit 6.1.3, `useJUnitPlatform`. **DONE
   (2026-08-15, PR #207).**
   - Connection info from env vars with known local defaults; precedence is
     env-var > default:
     - PostgreSQL — `HARMONICA_TEST_POSTGRES_HOST`/`_PORT`/`_DB`/`_USER`/
       `_PASSWORD` → `127.0.0.1`/`5432`/`harmonica_test`/`developer`/`developer`
     - MySQL — `HARMONICA_TEST_MYSQL_HOST`/`_PORT`/`_DB`/`_USER`/`_PASSWORD`
       → `127.0.0.1`/`3306`/`harmonica_test`/`developer`/`developer`
   - Gating (optional profile, default/local): short-timeout connectivity
     probe in `@BeforeAll` (≤2 s connect timeout) → `Assumptions.abort`; a
     down/absent DB **skips** the suite, so `./gradlew build` stays green
     without Docker.
   - Wiring: SQLite runs in `./gradlew build` (always-green fast path, `test`
     source set); PostgreSQL/MySQL run via `:integration-test:integrationTest`
     (separate `integrationTest` source set) — never in `build`.
2. **Port `harmonica_test`** — the 4 migrations (normal/not-null/default/other)
   and Postgres/MySql/Sqlite configs move in as JVM-level JDBC assertions
   (table/columns/index/data) after `Connection.transaction { up() }` / `down()`.
   SQLite is always-green (embedded); PostgreSQL/MySQL run when available.
   **Partial (PRs #207/#209):** SQLite embedded test runs in `./gradlew build`;
   gated PostgreSQL and MySQL create/drop smoke tests run via `integrationTest`.
   The full 4-migration port (columns/indexes/data assertions, MySQL config) is
   pending. **DONE (2026-08-28, PR #221):** full 4-migration port. The four
   `jarmonica` migrations are exercised end-to-end (up then down) via shared
   `AbstractMigrationSuite` helpers (`DemoMigrations`, `MigrationAssertions`)
   on all three DBMSes: SQLite (always-green `test` task), and PostgreSQL/MySQL
   (gated `integrationTest`). Assertions cover tables, columns, nullability,
   index and data (row counts). The `demo/` module is wired in as an
   `includeBuild` so tests reuse the same migration classes. Cross-DBMS
   correctness fixes surfaced by the port: `NotNullMigration` skips
   `addForeignKey` on SQLite (unsupported) and uses an unsigned referencing
   column for MySQL (FK type match); `SqliteAdapter` renders BLOB column
   defaults as SQLite `X'hex'` literals (the `E'\x…'` form defaults to is
   PostgreSQL-only syntax, rejected by SQLite, whereas TEXT defaults remain
   supported).
3. **H2 embedded path** — add `Dbms.H2` connection-URI support to `core`
   (previously returned `""`; `SqlServerAdapter` stays TODO, Phase 5) + H2 test
   driver. Second Docker-free DBMS alongside SQLite. URI and lifecycle contract:
   - In-process tests: `jdbc:h2:mem:<dbName>;DB_CLOSE_DELAY=-1`, with a unique
     `<dbName>` per test (or explicit schema cleanup) so tests do not collide.
   - TestKit tests (item 4): file-backed URL with a unique absolute path;
     delete the H2 DB files after all TestKit processes have closed.
   - The H2 driver is test/integration runtime-only — never published from
     `core`.
   - The expected H2 URI is asserted in `ConnectionTest`; `testing.md`
     keeps the same contract.
   **DONE (2026-08-15, PR #206).** `Dbms.H2` builds `jdbc:h2:<dbName>`;
   `H2Adapter` is complete; H2 2.4.240 is `testImplementation`-only. In-memory
   state across rollback/reconnect is preserved by keeping the connection open
   after a failed `transaction` for H2 (no `DB_CLOSE_DELAY` needed); identifier
   case is normalized from `DatabaseMetaData` (`DATABASE_TO_LOWER` supported).
   The `DB_CLOSE_DELAY=-1` bullet above is **superseded** by this
   connection-keeping choice; `testing.md` was updated to match (2026-08-15).
4. **Plugin-flow tests (issue #196)** — Gradle TestKit runs the `harmonica`
   up/down tasks against a real DB **with and without** `harmonica-exposed` on
   the script classpath. Seeds from the local `demo/` scratch project (rewritten
   for the direct scripting host), which gets committed here.
   **PR #219 (merged 2026-08-16):** `PluginFlowTest` (gradle-plugin, `test`
   source set, always-green) spawns real Gradle builds via TestKit that apply
   the `harmonica` plugin from a composite `includeBuild` of the repo root and
   run `harmonicaUp`/`harmonicaDown` against an embedded SQLite DB (absolute
   path in `<projectDir>/build/`); one case has `harmonica("com.improve_future:exposed:2.0.0")`
   on the script classpath (Exposed migration), one does not (plain JDBC
   migration). Assertions check `harmonica_migration` version rows + table
   existence. The `demo/` project is committed as the seed (script/ + jarmonica/
   trees, no wrapper, drivers on the buildscript classpath). This PR also set
   `group`/`version` (com.improve_future:2.0.0) on `core`/`exposed` so the
   composite substitutes the `com.improve_future:*` coordinates, and fixed a
   real bug: migration/config paths are now resolved relative to the project
   directory (were relative to the daemon JVM cwd → `FileNotFoundException`
   from TestKit).
5. **Docker + CI** — `docker-compose.yml` at the repo root (postgres:16,
   mysql:8, `developer`/`developer`, DB `harmonica_test`); a `db-integration`
   CI job runs the gated suite against Postgres/MySQL service containers in
   **required** mode: `HARMONICA_TEST_DB_REQUIRED` set only in CI makes the
   probe bounded-retry and **fails** on invalid config, missing credentials,
   unavailable service, or any skipped DB test — never skips (see
    [`ci.md`](ci.md) §3.1); SQLite and H2 stay in the fast path.
   **DONE (2026-08-15, PR #209):** `docker-compose.yml` (postgres:16.14,
   mysql:8.4.11, `developer`/`developer`, DB `harmonica_test`) is committed;
   the `db-integration` CI job runs `:integration-test:integrationTest` against
   health-gated Postgres/MySQL service containers in required mode
   (`HARMONICA_TEST_DB_REQUIRED=true`); `TestDb.requireDb` bounded-retries
   (10 × 2 s) and then fails with a clear message when a required DB is
   unreachable — verified locally: down DB in required mode fails the build,
   default mode skips.

Test framework/tooling decision: **JUnit 6.x** (in `core`/`gradle-plugin` since
Phase 2, PR #186). Testcontainers optional later; env-var config + compose
services are the baseline so the suite runs without container APIs.

Definition of done:

- `integration-test` module exists in this repo, ported from `harmonica_test`.
- `./gradlew build` runs the SQLite (integration-test `test` task) and H2
  (`core` `test` task) embedded tests and passes without Docker; the gated
  PostgreSQL/MySQL suite runs via the `integrationTest` task and passes with
  Docker.
- Postgres + MySQL suites green locally (Docker) and in CI (the `db-integration`
  service-container job, required mode).
- Issue #196 satisfied — the migration flow is verified with and without
  `harmonica-exposed` on the classpath.

### Phase 5 — Issue backlog (quick wins first)

Full triage: [issues-triage.md]. Order:

1. Urgent blockers: #153 (ties into #97). #167, #165, #140, #162 are resolved
   and closed (toolchain/CI fixes in Phase 0 PR #183; README/license PR #181;
   pluralizer PR #187); #158, #159 already closed (verified; not tracked here).
2. Small: #26 (migration file naming), #47 (prepared statements), #138 (custom
   columns), #141 (more column types), #145 (alter column), #139 (FK options),
   #69 (query execution API), #4 (created_at/updated_at), #189 (scanner
   `isSubtypeOf` swallows all `Throwable`).
   #91 (Exposed) is **closed** — the bridge shipped in Phase 3 (PRs #197-#199).
   Also added in Phase 4/5: #220 (SQLite DB parent directory not created),
   #222 (integration-test warnings) — both quick wins.
3. Medium: #7 (closed connection), #85 (SQLite defaults), #67 (timestamp
   default), #80 (Exposed version), #71 (seeding), #97 (JavaExec task tests),
   #155 (programmatic migration docs). Also #215 (Exposed 0.61.0 → 1.x bridge
   rewrite): Exposed 1.x moved the JDBC API out of `org.jetbrains.exposed.sql`,
   which breaks `harmonica-exposed` (`:exposed:compileKotlin` fails); plan the
   bridge update when scheduling this — see §6.
4. Large/strategic (separate designs/PRs): #1 (Maven Central), #125 (multiple
   DBs), #121 (dry run), #148 (Maven support — see `feature/maven-plugin`
   branch), #147 (Java support), #105 (adapter plugin API), #107 (SQL Server
   adapter), #41 (auto-migrations), #20 (research).

### Phase 6 — Release & publishing

- Configure **JitPack**: build from git tags; multi-module must produce
  `com.improve_future.harmonica:harmonica-core` (or keep group/module names
  stable). Verify with a snapshot tag.
- Keep Gradle Plugin Portal publication (`plugin-publish`) for
  `com.improve_future.harmonica`.
- Decide on Maven Central (OSSRH) as a secondary channel; needs GPG signing
  and credentials — treat as optional.
- Update README: install instructions, JitPack badge, wiki command docs,
  remove bintray references, update "not developed actively" notice.

### Phase 7 — Documentation refresh

- Rewrite README with current usage (also for the new Exposed-optional flow).
- Update/regenerate API docs with modern Dokka.
- Keep `document/` site in sync or remove it if unmaintainable.

## 5. Definition of done (overall restart)

- `./gradlew build` green on this machine (Java 25) and on CI.
- License headers gone from all source files.
- No dead repositories (jcenter/bintray) anywhere in the build or docs.
- Exposed fully optional, with docs and at least one example each way.
- Real-DB tests merged and runnable; local DB setup documented.
- First new release tagged; `master` fast-forwarded; JitPack build verified.
- Open-issue count reduced (all "urgent/small" closed or converted to tasks).
- `harmonica_demo` left untouched (documented only, not part of the restart).

## 5.5. Workflow rule

- **Small changes per PR**: each license-strip, dependency removal, badge fix,
  or issue fix is its own PR against `develop`. The only sanctioned exception
  is the Phase 0 toolchain PR, which must move Gradle 9 + Kotlin 2.3 +
  buildscript rewrite + source modernization together (they cannot be split
  without leaving the build permanently red).

## 6. Open decisions

Resolved (2026-08-01):

- Kotlin patch pinned: **2.3.20**.
- Gradle pinned: **9.7.0** (bumped from 9.6.1 via dependabot PR #211, 2026-08-16).
- `document` module: **dropped from the root build** (folder kept, excluded from
  settings).
- JVM-8 verification: **javap class-version check** (`jvm8-bytecode.yml`), not a
  JDK-8 toolchain test run (see [`ci.md`](ci.md) §3.2).
- Pluralization: **implement internally** — `singularize()` ported into
  `core/.../table/Inflections.kt`, kotlin-pluralizer + jitpack removed
  (Phase 2, PR #187).
- JUnit: **6.1.3** (Java 17 baseline; bumped from 6.1.2 via dependabot PRs
  #210/#214). Test configurations carry
  `org.gradle.jvm.version = 17` via `TargetJvmVersion` attribute override;
  published bytecode stays JVM 8 (Phase 2, PR #186).
- Reflections: **replaced with an internal classpath scanner**; `loadClass`
  catches only `ClassNotFoundException` + `LinkageError`. `isSubtypeOf` still
  catches `Throwable` — issue #189 (Phase 2, PRs #185/#188).
- Kotlin `jvm` plugin bump 2.3.20 → 2.4.10 (PR #193): **declined** — no
  functional need; stay on 2.3.20. PR #193 is still open (dependabot) as of
  2026-08-28 — revisit/close before starting Phase 5.
- Dependabot batch 2026-08-15 (PRs #210-#216): wrapper 9.7.0 (#211), JUnit
  6.1.3 (#210/#214), postgresql 42.7.13 (#213), H2 2.4.240 (#212),
  mysql-connector-j 26.7.0 (#216) — **merged 2026-08-16**; spec pins in
  tech-notes.md updated. **Not merged:** exposed 1.4.0 (#215) — Exposed 1.x
  moved the JDBC API out of `org.jetbrains.exposed.sql`, breaking the
  `harmonica-exposed` bridge (`:exposed:compileKotlin` fails: unresolved
  `sql`/`Database`/`Transaction`). Deferred — a bridge rewrite is its own piece
  of work; #215 stays open (tracked in Phase 5, Medium).

Still open:

- Maven Central vs JitPack-only for the first release.

Resolved for Phase 3 (2026-08-08):

- Exposed bridge artifact: **ship as a separate Gradle module** (`exposed/`,
  artifact `harmonica-exposed`), not snippet-first.
- Bridge targets **Exposed 0.61.0** (latest 0.x; JDBC API in
  `org.jetbrains.exposed.sql`).
- Transaction ownership: **Option A — harmonica owns the transaction** (bridge
  binds harmonica's connection into Exposed's manager; no Exposed-managed
  commit).
- Usage restriction (resolved during PR #198): `harmonica-exposed` is supported
  only in **short-lived, single-run JVMs** — its `Database.connect` overwrites
  Exposed's global default manager and is never unregistered (core purity
  forbids a close hook), so it must not be combined with other Exposed code
  (bare `transaction {}`, another `Database.connect`) in a shared JVM. See
  exposed-integration.md §2.2.

Resolved (2026-08-11):

- Script engine: **direct `BasicJvmScriptingHost`**, not the JSR-223 engine
  (PR #202). `.kts` migrations compile/evaluate via a `MigrationScript`
  `@KotlinScript` template using `createJvmCompilationConfigurationFromTemplate`
  + `dependenciesFromCurrentContext(wholeClasspath = true)`; `kotlin-scripting-common`
  / `kotlin-scripting-jvm` / `kotlin-scripting-jvm-host` replace
  `kotlin-scripting-jsr223`. See tech-notes.md.
