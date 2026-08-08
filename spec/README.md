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

> Baseline snapshot taken before Phase 0. Toolchain work implemented 2026-08-01
> on `feature/toolchain` (Gradle 9.6.1, Kotlin 2.3.20, CI rewrite) and **merged
> via PR #183** — update this list when the baseline advances.

- Kotlin 1.4.20, Gradle wrapper 6.5, `jvmTarget = 1.8`
- Gradle plugin-publish 0.9.10, Dokka 0.9.17
- Publish target: jcenter/bintray (both dead) and OSSRH staging config
- CI: CircleCI (`circleci/openjdk:8-jdk`) + GitHub Actions (bare `gradle test`)
- Three modules: `core`, `gradle-plugin`, `document` (nested standalone build)
- License header block at the top of **86** source files (79 `.kt`, 4 `.kts`, 3
  `.properties`) + 1 extensionless `META-INF/services/...` registration file —
  since stripped (PR #180)
- `jcenter()`/bintray already removed from all build files (2024 work); README
  badges fixed (#181); dead reference remains only in a `document` doc-string
- No Exposed dependency in `core` anymore (reflection-based detection remains)
- Tests for real DBMS live in a separate repository `harmonica_test`
- **`develop` is 30 commits ahead of `master` but never fully tested** — it
  contains an untested module split (`feature/split`) and jcenter-removal
  build work. See the risk register in [`plan.md`](plan.md).

## Machine environment (current)

- OpenJDK 25.0.3 (only JDK installed)
- No standalone `gradle`, no `docker`
- Gradle wrapper 6.5 cannot run on Java 25 → toolchain upgrade is step 1
