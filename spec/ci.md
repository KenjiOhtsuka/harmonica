# CI, GitHub Actions & Dependabot

Design for continuous integration, GitHub Actions workflows, and automated
dependency updates. Feeds into **Phase 0** (CI rewrite, part of the toolchain
PR), Phase 4 (DB integration tests) and Phase 6 (release publishing).

## 1. Current state

**Phase 0 implemented 2026-08-01 (merged via PR #183).** `.github/workflows/gradle.yml` (broken: bare
`gradle`, `checkout@v2`, push-only) and `.circleci/config.yml` (broken:
`circleci/openjdk:8-jdk`, bare `gradle`, checksum of a nonexistent file) are
**deleted**. Replaced by:

- `.github/workflows/ci.yml` — main build & unit tests (JDK 25, PR/push).
- `.github/workflows/jvm8-bytecode.yml` — asserts JVM 8 bytecode.
- `.github/workflows/dependency-submit.yml` — submits the dependency graph that
  `ci.yml` uploads (least-privilege split, see §3.1).
- `.github/dependabot.yml` — landed earlier as its own PR (#169).

## 2. Constraints

- Gradle 9.1+ is required to *run* on the JDKs we use (Gradle 9.x daemon needs
  JDK 17+; this project runs on JDK 25).
- Target library bytecode is **JVM 8** (`jvmTarget = 1.8`). We cannot run
  Gradle 9 on a JDK 8 runtime to "prove" this — verification is done via the
  javap class-version check (§3.2).
- Small changes per PR → CI is PR-driven, fast, and cancels stale runs.
- User is on the GitHub **Student Developer Pack** (free tier). Public repos
  get unlimited Actions minutes; the student plan covers private repos too.
  Dependabot is free on all plans. Conservation is still good practice.

## 3. Workflows

### 3.1 `ci.yml` — main build & unit tests

As landed:

- **Triggers**: `pull_request` (any) and `push` to `develop`/`master`.
  `paths-ignore` for `README.md`, `spec/`, `docs/`, `document/` so docs-only
  changes don't burn minutes.
- **Permissions**: `contents: read` (least privilege) on every job — the build
  job **no longer escalates to `contents: write`**. The dependency graph is
  *generated and uploaded* here (`dependency-graph: generate-and-upload`) and
  *submitted* by the separate `workflow_run` workflow `dependency-submit.yml`
  (which alone carries `actions: read, contents: write`). This is the
  documented GitHub/Gradle pattern for dependency submission; it also works for
  PRs from forks.
- **Concurrency**: group on `github.ref`, `cancel-in-progress: true`.
- **Steps**:
  - `actions/checkout@v7` with `persist-credentials: false` (no step needs Git
    authentication after checkout; Gradle doesn't use the persisted credential).
  - `actions/setup-java@v4` with Temurin **JDK 25** — `setup-gradle` does **not**
    install a JDK and ignores `distribution`/`java-version` inputs (only
    `actions/setup-java` selects the JDK).
  - `gradle/actions/setup-gradle@v4` with wrapper validation, build cache, and
    `dependency-graph: generate-and-upload` (+
    `dependency-graph-continue-on-failure: true`) so Dependabot PRs resolve
    against an up-to-date dependency graph.
  - `./gradlew build --no-daemon` (unit tests only by default; DB-gated tests
    excluded — see `spec/testing.md`).
- **Matrix (later, Phase 4)**: a `db-integration` job with `postgres:16` /
  `mysql:8` service containers running the gated integration suite.

### 3.2 `jvm8-bytecode.yml` — prove JVM 8 target without JDK 8

Since Gradle 9 cannot run on JDK 8:

- After `./gradlew build`, runs `javap -verbose` on **every `.class` file** in
  each module's Kotlin main output directory (`core/.../kotlin/main`,
  `gradle-plugin/.../kotlin/main`) and asserts **class-file major version 52**
  (JDK 8) for all of them. Major version → Java mapping: 52=Java 8, 61=17,
  69=25.
- **Phase 0 sub-decision (resolved)**: keep the lightweight javap check. The
  alternative — running the test suite on a JDK 8 runtime via Gradle toolchains
  (foojay-resolver-convention + `JavaLanguageVersion.of(8)`) — adds a toolchain
  download for no test coverage gain today (unit tests don't touch bytecode
  compatibility). Revisit only if javap proves insufficient.

### 3.3 `dependency-submit.yml` — least-privilege dependency graph submission

- Triggered by `workflow_run` of `ci.yml` (`types: [completed]`), runs only
  when that run succeeded, and does `dependency-graph: download-and-submit`.
- Minimal by design: `download-and-submit` runs no Gradle build, so it needs no
  checkout and no JDK — a single `setup-gradle` step.
- Runs with `actions: read` + `contents: write` — the only job in the repo that
  carries write access, and it never executes untrusted PR code.
- `dependency-graph-continue-on-failure: false`: the job's sole purpose is the
  submission, so a generate/submit failure must fail the workflow loudly
  (contrast with `ci.yml`, where the graph is a side task of the build).

### 3.4 `release.yml` (optional, Phase 6)

- Trigger: tag push (`v*`).
- Build + publish the Gradle plugin to the Plugin Portal
  (`plugin-publish`), and optionally publish `core`/`exposed` artifacts to
  JitPack (out-of-band, triggered by the tag) and/or Maven Central (OSSRH).
- Requires secrets (`GRADLE_PUBLISH_KEY/SECRET`, `MAVEN_USERNAME/PASSWORD`,
  GPG key) — configure in repo settings only when publishing is ready.

### 3.5 `dependabot.yml` — landed (#169, merged)

```yaml
version: 2
updates:
  - package-ecosystem: "gradle"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 10
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 5
```

- `directory: "/"` scans the whole multi-module build (`core`,
  `gradle-plugin`; `document` is excluded from the build) including the Gradle
  wrapper and `gradle.properties`.
- **No grouping** — keeps each dependency bump its own small PR, matching the
  workflow rule.
- Security updates are enabled by default and stay on (free).
- Dependabot PRs run `ci.yml`; public-repo minutes are free, so this is fine.
- Note: since Phase 0 removed most dead deps' resolution paths, Dependabot's
  PRs against `kotlinx-html`, `kotlin-reflect`, JDBC drivers, etc. still open —
  close (not merge) those;   the Phase 2 dead-dep PR removes them. Specifically,
  the Phase 0 toolchain PR applied the same bumps as dependabot PRs **#171**
  (dokka 2.2.0), **#172** (plugin-publish 2.1.1) and **#178** (wrapper 9.6.1) —
  all three are now closed (Phase 0 merged).

## 4. Action version policy

- Pin by **major tag** (`@v4`) and let Dependabot keep them current. Full
  SHA-pinning is optional hardening for stricter projects.
- Recommended actions: `actions/checkout@v7` (in use since PR #170, Dependabot
  bump v4 → v7), `gradle/actions/setup-gradle@v4`
  (official Gradle action; replaces manual `setup-java` + cache for Gradle
  projects), `actions/upload-artifact@v4` (if artifacts needed later).

## 5. Student-plan notes

- Public repo → unlimited Actions minutes; student pack covers private repos
  (2,000 min/month). Treat minutes as plentiful but not free-for-all.
- Conservation tactics used: PR-driven triggers, `paths-ignore` for docs,
  `cancel-in-progress`, Gradle build cache via `setup-gradle`.
- Dependabot has no per-run cost beyond the CI runs it triggers.

## 6. Sequencing

- **`dependabot.yml`**: **done** (#169, merged).
- **`ci.yml` + `jvm8-bytecode.yml` + `dependency-submit.yml` + CircleCI
  removal**: **done**, part of the Phase 0 toolchain PR.
- **DB matrix job**: Phase 4 (with the `integration-test` module).
- **`release.yml` + secrets**: Phase 6.

## Definition of done

- `ci.yml` green on JDK 25 for `./gradlew build` on PRs and `develop`/`master`
  pushes; stale runs cancelled; docs-only changes skipped. — **done** (green on
  PR #183: `build` + `jvm8-bytecode` both pass on JDK 25)
- `jvm8-bytecode.yml` asserts class-file major version 52 for every produced
  class on every build. — **done**
- Dependency graph submitted via least-privilege split
  (`generate-and-upload` in `ci.yml` → `download-and-submit` in
  `dependency-submit.yml`); no job executes untrusted PR code with write
  access. — **done**
- Dependabot configured for `gradle` + `github-actions`, weekly, ungrouped. —
  **done**
- CircleCI config removed. — **done**
