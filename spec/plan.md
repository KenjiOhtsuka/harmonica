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
- No Docker installed locally (yet). DB-backed tests must not break local builds.
- Do not force Exposed onto users; their migration classpath decides.

## 3. Branch & release strategy (master is stale)

State:

- `master` (origin/master @ `3b423c6`) has not been touched in years and is the
  direct ancestor of `develop` (merge-base == master HEAD).
- `develop` is **56 commits ahead** (module split into `core`/`gradle-plugin`,
  jcenter removal work, MIT license change, CI-action bumps, etc.) but **never
  fully tested or released** — this is why master was left behind.

Policy going forward (Git Flow, simplified):

1. `develop` is the integration branch; feature/phase branches are cut from it.
2. A release branch/tag is created from `develop` only when CI is fully green
   and manual DB tests pass.
3. The very first milestone of this restart is: make `develop` build green →
   then fast-forward `master` to `develop` → tag the first new release there.
   (Because `master` is an ancestor, this is a clean fast-forward, no merge
   conflict risk.)
4. `master` becomes the source of released tags (JitPack builds from tags).
5. Old branches on the remote (`feature/core_split`, `feature/maven-plugin`,
   `feature/version_up`, `feature/exposed`, `feature/show_sql`, `feature/mit-license`,
   `feature/split`) are either resurrected as issues or deleted after review.

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
  `gradle-plugin` build, test (72 tests green), and package correctly on Java 25
  and in CI; `document/` is excluded from the build. Phase 0 also proved the
  POM/publication config (PR #183) and the Groovy→Kotlin DSL migration. The
  remaining unverified risk is real-DB behavior, which Phase 4 covers.

## 4. Phases

Each phase lands as its own branch + PR into `develop`. Definition of done for
each phase: `./gradlew build` passes on this machine (Java 25) with the phase's
changes, and CI is green.

### Phase 0 — Toolchain upgrade & CI (blocking, do first)

Outcome: project compiles and tests on Java 25; CI works.

Status: **implemented and merged (2026-08-01, PR #183, merge commit
`69344da`).** `./gradlew clean build` is green locally on Java 25 and in CI.

- Upgrade Gradle wrapper to **9.1.x or newer** — done: **9.6.1** (wrapper jar/
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
  dependency registers its own).
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
- After green: fast-forward `master` to `develop` (first milestone).

### Phase 1 — License header removal

Outcome: all source files have no license comment header; LICENSE/README keep
the MIT notice.

Status: **landed 2026-08-01.** License headers stripped from all 87 files
(86 source files + 1 extensionless `META-INF/services/...` registration file;
PR #180, which also added `scripts/strip-license-headers.sh`); README badges
fixed (PR #181 — MIT URL → opensource.org, bintray badge → JitPack). Remaining
jcenter reference: `document/.../JarmonicaView.kt:61` (doc-string only).

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
gradle-plugin; Phase 0 baseline was 66).

- `gradle.properties`: `kotlin_version` → 2.3.x. — **done in Phase 0** (2.3.20).
- Dead repositories: **gone** — jcenter/bintray/space removed; jitpack removed
  from `core` and `gradle-plugin` with pluralizer removal (PR #187).
- `core`:
  - `kotlin-pluralizer` (jitpack, unmaintained, issue #140) → **removed**;
    internal `singularize()` ported into `core/.../table/Inflections.kt`
    (PR #187, decision in §6).
  - `commons-codec` 1.15 → **dropped** (no usage; PR #184).
  - JUnit Jupiter 5.4.2 → **6.1.2** (PR #186; decision in §6);
    `kotlin-test` → current (**done in Phase 0**, pinned to 2.3.20).
- `gradle-plugin`:
  - `kotlin-script-runtime`/`kotlin-script-util` → `kotlin-scripting-jsr223` —
    **done in Phase 0** (JSR-223 migration).
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

Status: **not started — next phase.**

- Remove dead `hasExposed` flag plumbing from `Connection.kt` (currently a
  no-op ternary).
- Expose the underlying `java.sql.Connection` from `Connection`.
- Provide an **optional** artifact/module `harmonica-exposed` that bridges
  Exposed transactions onto harmonica's connection lifecycle (users who add it
  get Exposed; everyone else is unaffected — compilation cannot fail).
- Keep runtime detection (`Class.forName`) only where it adds value; otherwise
  delete `PluginConfig.hasExposed()`.
- Resolve issues #91, #80 and the concern behind #160 (document, upgrade,
  reflect).

### Phase 4 — Tests & DB environment

Outcome: real-DB integration tests live in this repo; runnable locally and in CI.

- Merge relevant tests from `KenjiOhtsuka/harmonica_test` into a new
  `integration-test` module or a dedicated source set.
- Local DBs: install Docker (or use system Postgres/MySQL) and document setup;
  provide `docker-compose.yml` for PostgreSQL/MySQL; SQLite and H2 need no
  server and **will be covered** by Docker-free embedded-DB tests (see
  [`testing.md`](testing.md)).
- Tests that need a DB are gated (skip when DB unavailable) so `./gradlew build`
  never fails locally without Docker.
- CI runs DB-backed tests using Docker services.
- Decide test framework/tooling (**JUnit 6.x** — used in `core`/`gradle-plugin`
  since Phase 2, PR #186 — plus Testcontainers for PostgreSQL/MySQL).

### Phase 5 — Issue backlog (quick wins first)

Full triage: [issues-triage.md]. Order:

1. Urgent blockers: #153 (ties into #97). #167, #165, #140, #162 are resolved
   and closed (toolchain/CI fixes in Phase 0 PR #183; README/license PR #181;
   pluralizer PR #187); #158, #159 already closed (verified; not tracked here).
2. Small: #26 (migration file naming), #47 (prepared statements), #91
   (Exposed), #138 (custom columns), #141 (more column types), #145 (alter
   column), #139 (FK options), #69 (query execution API), #4
   (created_at/updated_at), #182 (JitPack badge).
3. Medium: #7 (closed connection), #85 (SQLite defaults), #67 (timestamp
   default), #80 (Exposed version), #71 (seeding), #97 (JavaExec task tests),
   #155 (programmatic migration docs).
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
- Gradle pinned: **9.6.1**.
- `document` module: **dropped from the root build** (folder kept, excluded from
  settings).
- JVM-8 verification: **javap class-version check** (`jvm8-bytecode.yml`), not a
  JDK-8 toolchain test run (see [`ci.md`](ci.md) §3.2).
- Pluralization: **implement internally** — `singularize()` ported into
  `core/.../table/Inflections.kt`, kotlin-pluralizer + jitpack removed
  (Phase 2, PR #187).
- JUnit: **6.1.2** (Java 17 baseline). Test configurations carry
  `org.gradle.jvm.version = 17` via `TargetJvmVersion` attribute override;
  published bytecode stays JVM 8 (Phase 2, PR #186).
- Reflections: **replaced with an internal classpath scanner**; `loadClass`
  catches only `ClassNotFoundException` + `LinkageError`. `isSubtypeOf` still
  catches `Throwable` — issue #189 (Phase 2, PRs #185/#188).

Still open:

- Exposed bridge: separate artifact vs code snippets/docs only.
- Maven Central vs JitPack-only for the first release.
