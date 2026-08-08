---
name: harmonica-dev
description: Use when working on the Harmonica Kotlin DB migration tool — planning, phase work, PRs, or anything touching this repo. Encodes the restart workflow: read spec/ before proposing work, small PRs per issue, toolchain constraints (JVM 8 target, Gradle 6.5 wrapper unusable on the Java 25 dev machine, Phase 0 prerequisite), and conventions (no comments, no unused deps, core stays Exposed-free).
---

# Harmonica Development

Work on this repository happens in phases tracked in `spec/plan.md`. The
`spec/` directory is the source of truth; keep it in sync when decisions
change.

## Before proposing work

1. Read `spec/plan.md` (master plan + open decisions + risk register).
2. Read the phase-specific spec that applies: `spec/ci.md`, `spec/testing.md`,
   `spec/exposed-integration.md`, `spec/issues-triage.md`, `spec/tech-notes.md`.
3. Check `spec/plan.md` §6 "Open decisions" — if the work depends on an
   unresolved decision, flag it and ask before proceeding.

## Hard constraints

- JVM 8 bytecode target (`jvmTarget = 1.8`) — never bump.
- `./gradlew build` currently does NOT work on the dev machine (Gradle 6.5
  wrapper + Kotlin 1.4.20 vs OpenJDK 25). Build verification is impossible
  until Phase 0. Do not treat red builds as bugs to fix piecemeal.
- One change per PR against `develop`, except the Phase 0 toolchain PR which
  must move together (Gradle 9 + Kotlin 2.3 + buildscript rewrite + source
  modernization + CI rewrite).
- Never commit unless explicitly asked.

## Conventions

- No comments in code unless asked.
- No new dependencies unless actually used (repo has a history of unused deps).
- `core` must never import Exposed.
- `document/` is a stale nested build — assume it is not part of any green
  build until Phase 0 decides its fate.
- When editing opencode config, load the `customize-opencode` skill.

## Workflow

- Cut a phase/feature branch from `develop`, do the work, open a small PR.
- After finishing a phase, update the relevant `spec/` doc (plan phase status,
  DoD, open decisions) as part of the same PR.
- After a phase PR or milestone merges, run the `plan-review` skill to
  reconcile `spec/plan.md`, `spec/tech-notes.md`, and `spec/issues-triage.md`
  with reality (git log, issues/PRs, versions, test counts).
- Use the review subagents (`spec-review`, `build-review`, `exposed-review`)
  to validate spec-adjacent work before opening a PR.

## Current phase status

See `spec/plan.md` — phase status is reconciled by the `plan-review` skill
after each merge, so don't hard-code the current phase here.
