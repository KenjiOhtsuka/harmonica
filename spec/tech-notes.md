# Toolchain & Dependency Research

Facts gathered on 2026-08-01 (test counts updated 2026-08-15). Update this
file as versions change.

## Phase 0 status (implemented 2026-08-01; merged via PR #183, merge commit `69344da`)

- Gradle wrapper **6.5 → 9.6.1**, Kotlin **1.4.20 → 2.3.20**.
  `./gradlew clean build` is green on Java 25; **85 tests pass, 0 failures**
  (73 core, 6 gradle-plugin, 4 exposed, 2 integration-test of which 1 is the
  gated PostgreSQL suite that **skips** without a DB — Docker-less runs are
  84/84). Phase 0 baseline was 66; Phase 2 added 6 InflectionsTest cases;
  Phase 3, PR B added 4 ExposedMigrationTest cases; PR #201 added 2
  ScriptClasspathTest cases; Phase 4 added 5 H2 cases in `core` (PR #206) and
  the 2 integration-test cases (PR #207).
- `jvmTarget = 1.8` is set via `kotlin.compilerOptions { jvmTarget.set(JvmTarget.JVM_1_8) }`
  + `java.sourceCompatibility/targetCompatibility = 1.8` in all four modules
  (`core`, `exposed`, `gradle-plugin`, `integration-test`). Verified by
  `javap`: all main classes are class-file major **52** (JVM 8).
- `document/` is **dropped from the root build** (`settings.gradle.kts` includes
  only `core`, `exposed`, `gradle-plugin`, `integration-test`). The nested
  Gradle 4.9 build is untouched.
- CI replaced: `gradle.yml` + `.circleci/config.yml` → `ci.yml` +
  `jvm8-bytecode.yml` (see [ci.md](ci.md)).

## JDK / Gradle / Kotlin compatibility

| Component | In repo now | Notes |
| --- | --- | --- |
| JDK to run Gradle | OpenJDK 25 | Gradle 9.1.0+ required (Java 25); 9.6.1 in use. |
| Gradle wrapper | 9.6.1 | `gradle-9.6.1-bin.zip`; wrapper jar/scripts regenerated via `./gradlew wrapper`. |
| Kotlin | 2.3.20 | `jvmTarget = 1.8` (bytecode) still supported — verified in 2.3.20 with **no** deprecation warning (only the old `kotlinOptions` DSL is deprecated; this build uses `compilerOptions`). |
| Java language level | 1.8 | `java.sourceCompatibility/targetCompatibility` = 1.8 (no Java sources today). |
| Test runtime JDK | Java 25 | JVM-8 target proven by the `jvm8-bytecode.yml` javap check (decision, ci.md §3.2), not a JDK-8 run. |

Key references:

- Gradle compatibility matrix: <https://docs.gradle.org/current/userguide/compatibility.html>
- Gradle 9.1.0 release notes (Java 25 support): <https://docs.gradle.org/9.1.0/release-notes.html>
- Kotlin 2.3 compatibility guide (drops `-language-version=1.8`; no change to
  `jvmTarget 1.8`): <https://kotlinlang.org/docs/compatibility-guide-23.html>

## Dependencies

### core

| Dependency | Status | Notes |
| --- | --- | --- |
| `org.jetbrains.kotlin:kotlin-test*` | **DONE** | 2.3.20 via `gradle.properties`. |
| JUnit Jupiter | **DONE** | 5.4.2 → **6.1.2** (Phase 2, PR #186); see the note below on the `TargetJvmVersion` attribute override. |
| `commons-codec` | **DONE** | 1.15 dropped — unused (PR #184). |
| `com.github.cesarferreira:kotlin-pluralizer` | **DONE** | removed (PR #187); internal `singularize()` port, see the note below (issue #140 resolved). |
| `com.h2database:h2` | **DONE** | **2.2.224**, `testImplementation`-only (Phase 4, PR #206) for the embedded-H2 migration/adapter tests; never published from `core`. |

### exposed

| Dependency | Status | Notes |
| --- | --- | --- |
| `org.jetbrains.exposed:exposed-jdbc` | **DONE** | 0.61.0 pinned (api; Phase 3, PR B). `exposed-core` arrives transitively; never declare it directly. |
| `org.xerial:sqlite-jdbc` | **DONE** | testImplementation-only (Phase 3, PR B) for embedded-DB bridge tests; see the gradle-plugin row note on driver placement. |

### gradle-plugin

| Dependency | Status | Notes |
| --- | --- | --- |
| `kotlin-compiler-embeddable` | **DONE** | 2.3.20. |
| `kotlin-script-runtime` / `kotlin-script-util` → `kotlin-scripting-jvm-host` | **DONE** | `kotlin-script-util` does not exist for 2.3.20 (404; Maven Central metadata frozen at 1.8.22), so Phase 0 adopted `kotlin-scripting-jsr223`. **Replaced 2026-08-11 (PR #202)** by `kotlin-scripting-common` + `kotlin-scripting-jvm` + `kotlin-scripting-jvm-host`: `AbstractMigrationTask` evaluates `.kts` directly with `BasicJvmScriptingHost` from a `MigrationScript` `@KotlinScript` template (`createJvmCompilationConfigurationFromTemplate`, `dependenciesFromCurrentContext(wholeClasspath = true)`, base class loaded from the plugin classloader); no `javax.script.ScriptEngine` / registered `ScriptEngineFactory` anymore. |
| `kotlin-reflect` | **DONE** | dropped — only stdlib `KClass` usage (PR #184). |
| `org.reflections:reflections` | **DONE** | replaced with a classpath scanner in `JarmonicaTaskMain` (Phase 2, PRs #185/#188). |
| `kotlinx-html-jvm` | **DONE** | dropped here (PR #184); real consumer is `document` (Phase 7). |
| `kotlin-test` / `kotlin-test-junit5` | **DONE** | pinned to 2.3.20 (were unversioned). |
| JDBC test drivers (mysql, postgresql, mssql) | **DONE** | removed (PR #184); server DB drivers move to the Phase 4 integration module. Exception: `org.xerial:sqlite-jdbc` is re-added as `testImplementation`-only in the `exposed` module (Phase 3, PR B) for embedded-DB Option-A transaction tests — nothing leaks into published artifacts. |
| `com.gradle.plugin-publish` | **DONE** | 0.9.10 → **2.1.1**; legacy `pluginBundle` block removed (2.x moved config into `gradlePlugin`). |
| `org.jetbrains.dokka` | **DONE** | 0.9.17 → **2.2.0**; removed the removed `outputFormat` DSL. Docs refresh is Phase 7. |

### integration-test (Phase 4, PR #207)

| Dependency | Status | Notes |
| --- | --- | --- |
| `org.junit.jupiter:junit-jupiter` | **DONE** | 6.1.2; same `TargetJvmVersion` attribute override as `core`/`gradle-plugin` (test classpaths only, JVM 8 published). |
| `org.xerial:sqlite-jdbc` | **DONE** | 3.45.3.0, `testImplementation`-only; embedded always-green SQLite suite in `test`. |
| `org.postgresql:postgresql` | **DONE** | 42.7.4, `integrationTestImplementation`-only; gated suite. |
| `com.mysql:mysql-connector-j` | **DONE** | 8.4.0, `integrationTestImplementation`-only; gated suite. |

Two source sets: `test` (SQLite, runs in every `build`) and `integrationTest`
(gated `Test` task — PostgreSQL smoke test landed in PR #207; MySQL driver is
declared but the suite is pending; connectivity probe in `@BeforeAll` aborts
the suite when the DB is down — no Docker required for a green build).

### document (separate subproject, excluded from the root build)

| Dependency | Status | Notes |
| --- | --- | --- |
| `kotlinx-html-jvm` | untouched | real consumer; upgrade in Phase 7 or drop the module. |
| wrapper Gradle 4.9 / version-less Kotlin plugin / `mainClassName` | untouched | not part of the build since Phase 0. |

### Source-code blockers (all resolved in Phase 0)

- `JarmonicaPlugin.kt` — `JavaPluginConvention`/`conventionMapping`/`groovyClosure`
  removed; tasks created with `tasks.register(name, Class) { task -> ... }`.
- `HarmonicaPlugin.kt` — legacy `tasks.create(String, Class)` → `tasks.register`.
- Gradle 9 task validation: `JavaExec` is abstract in Gradle 9, so the
  Jarmonica task subclasses are `abstract`; every task class carries
  `@DisableCachingByDefault` (validatePlugins requirement).
- `Connection.kt:152` — `toUpperCase()` → `uppercase()` (deprecation was an
  error under Kotlin 2.3).

Still open (not Phase 0):

- `buildConnectionUriFromDbConfig` returns `""` for `Dbms.SQLServer`;
  `SqlServerAdapter` is 100% `TODO` (Phase 5). (`Dbms.H2` was added 2026-08-15,
  PR #206 — `jdbc:h2:<dbName>`.)
- Coordinates/IDs reconciliation (plugin id `harmonica`/`jarmonica` applied vs
  published; stale `META-INF/gradle-plugins/com.improve_future.harmonica.properties`
  still bundled) — Phase 6.

## Repositories

- `jcenter()` / `jcenter.bintray.com` — already removed from all build files
  before Phase 0; the README badges were fixed (#181); the only dead reference
  left is `document/.../JarmonicaView.kt:61` (doc-string, module excluded from
  the build).
- `maven.pkg.jetbrains.space/public/p/kotlinx-html/maven` — **removed from
  `gradle-plugin` in Phase 0** (kotlinx-html 0.7.3 resolves from Maven Central);
  still declared in `document/build.gradle` (module excluded from build).
- `https://jitpack.io` — **removed from both `core` and `gradle-plugin`**
  (Phase 2, PR #187). Note: while `kotlin-pluralizer` existed, `gradle-plugin`
  needed jitpack transitively (`api(project(":core"))` exposes it on the
  runtime classpath) — removal would have failed resolution.
- `com.github.cesarferreira:kotlin-pluralizer:0.2.9` — **removed** (Phase 2,
  PR #187). Latest is 1.0.0 (repo dormant since 2023-05-24, not archived).
  Replaced with an internal `singularize()` port
  (`core/.../table/Inflections.kt`), behavior-identical (quirks included,
  e.g. `leaves → leafe`).
- **JUnit 6.1.2** (Phase 2, PR #186): JUnit 6 has a **Java 17 baseline** and
  declares `org.gradle.jvm.version = 17` in its Gradle metadata. Our test
  configurations carry jvm.version 8 (from `targetCompatibility = 1.8`), so
  resolution initially rejected the artifacts. Fix: override
  `TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE` to 17 on
  `testCompileClasspath`/`testRuntimeClasspath` only. **Published (main)
  bytecode stays JVM 8.** `kotlin-test-junit5:2.3.20` pins junit 5.10.1 /
  platform 1.10.1; Gradle conflict resolution bumps them to 6.1.2.
- `org.reflections:0.9.11` — **removed** (Phase 2, PR #185); replaced with a
  small classpath scanner in `JarmonicaTaskMain` (file + jar protocols).
  `loadClass` catches only `ClassNotFoundException` + `LinkageError` (PR #188);
  `isSubtypeOf` still catches `Throwable` — issue #189.
- `plugins.gradle.org/m2/` (root buildscript) — **gone**; replaced by the
  `plugins {}` block in root + `pluginManagement` in `settings.gradle.kts`.
- OSSRH `s01.oss.sonatype.org` (gradle-plugin) — **removed in Phase 0, then
  restored** (commit `546d956`) as Phase 6 prep; publishing decision open.

## Plugins in `build.gradle.kts` (root)

- `plugins { kotlin("jvm") version "2.3.20" apply false;
  id("com.gradle.plugin-publish") version "2.1.1" apply false;
  id("org.jetbrains.dokka") version "2.2.0" apply false }` — legacy
  `buildscript` + classpath block deleted.

## CI

- `.circleci/config.yml` and `.github/workflows/gradle.yml` **deleted**;
  replaced by `ci.yml` (PR/push, JDK 25, `./gradlew build`) and
  `jvm8-bytecode.yml` (asserts class-file major version 52).
- JVM-8 verification decision: **javap check**, not a JDK-8 toolchain test run
  (see ci.md §3.2).

## Environment setup (this machine)

- Install standalone `gradle` only if desired; wrapper is sufficient.
- Install Docker for DB-backed tests (see [testing.md](testing.md)).
- **Kotlin-daemon flakiness**: on this 2-CPU box, cold `./gradlew` runs
  occasionally hit Kotlin-daemon incremental-cache errors (`Could not close
  incremental caches`, `FilePageCache`) and once corrupted a `:core:test`
  results file — clearing `~/.gradle/kotlin` + daemon caches fixes it. Green
  but noisy; `ubuntu-latest` CI should be unaffected. Note that `clean` +
  `--no-daemon` avoids most of it.
