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

> Snapshot taken 2026-08-08, after Phase 0 (toolchain) and Phase 2 (dead-dep
> removal). Update this list when the baseline advances.

- Kotlin **2.3.20**, Gradle wrapper **9.6.1**, `jvmTarget = 1.8` (class-file
  major 52 asserted in CI)
- Gradle plugin-publish **2.1.1**, Dokka **2.2.0** (Dokka bundles Jackson
  2.15.3 at build time only — not shipped; see the Dependabot alerts)
- Publish target: plugin-publish + OSSRH staging (jcenter/bintray removed)
- CI: GitHub Actions only — `ci.yml` (PR/push, Temurin JDK 25,
  `actions/checkout@v7` + `gradle/actions/setup-gradle@v6` +
  `actions/setup-java@5.6.0`), `jvm8-bytecode.yml` (major-52 assertion),
  `dependency-submit.yml` (Dependabot). CircleCI removed.
- Three active modules: `core`, `exposed`, `gradle-plugin` — **76 tests green**
  (68 core + 4 plugin + 4 exposed); `document` (nested standalone build) is
  excluded from the build
- License headers stripped from all source files (PR #180); README badges fixed
  (PR #181)
- No Exposed dependency in `core`. The optional `harmonica-exposed` module
  (`exposed/`, pinned to Exposed 0.61.0) ships the `exposedTransaction` bridge
  (Phase 3, PR B); runtime reflection detection was removed from
  `gradle-plugin` (PR A) — see
  [`exposed-integration.md`](exposed-integration.md)
- Tests for real DBMS still live in the separate `harmonica_test` repository —
  to be merged in Phase 4
- **`develop` is 56 commits ahead of `master`** — Phase 4 (real-DB tests) must
  pass before `master` advances. See the risk register in [`plan.md`](plan.md).

## Machine environment (current)

- OpenJDK 25.0.3 (only JDK installed)
- No standalone `gradle`, no `docker`
- Toolchain upgrade done (Phase 0, PR #183): the Gradle 9.6.1 wrapper runs on
  Java 25, so `./gradlew build` works locally.
