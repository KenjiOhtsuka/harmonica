---
name: harmonica-dev
description: Use when working on the Harmonica Kotlin DB migration tool — planning, phase work, PRs, or anything touching this repo. Encodes the restart workflow: read spec/ before proposing work, small PRs per issue, toolchain constraints (JVM 8 target, Gradle 9.7.0 + Kotlin 2.3.20 on the Java 25 dev machine), and conventions (no comments, no unused deps, core stays Exposed-free).
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
- `./gradlew build` works on this machine (Gradle 9.7.0 + Kotlin 2.3.20 on
  OpenJDK 25). A red build is a real bug — fix it, don't defer.
- One change per PR against `develop`.
- Never push directly to `develop` — docs/spec and `.opencode/**` changes go
  through the same small-PR flow as code.
- Never commit unless explicitly asked.

## Conventions

- No comments in code unless asked.
- No new dependencies unless actually used (repo has a history of unused deps).
- `core` must never import Exposed.
- `document/` is a stale nested build — excluded from the root build since
  Phase 0; do not expect it to compile in `./gradlew build`.
- When editing any file under `.opencode/**`, load the `customize-opencode`
  skill and consult the OpenCode configuration schema.

## Workflow

- Cut a phase/feature branch from the **current `origin/develop`** (fetch
  first). Never cut from a sibling unmerged feature branch — it contaminates
  the PR diff.
- Do the work, then open a small PR. When integrating `develop` into the PR
  branch, prefer `git merge` over rebase; never `git pull --rebase` a branch
  that already contains merge commits, and never amend or force-push commits
  that exist on the remote.
- After finishing a phase, update the relevant `spec/` doc (plan phase status,
  DoD, open decisions) as part of the same PR.
- After a phase PR or milestone merges, run the `plan-review` skill to
  reconcile `spec/plan.md`, `spec/tech-notes.md`, and `spec/issues-triage.md`
  with reality (git log, issues/PRs, versions, test counts) — the reconcile
  itself lands as a docs PR, never a direct push.
- Use the review subagents (`spec-review`, `build-review`, `exposed-review`)
  to validate spec-adjacent work before opening a PR.

## Current phase status

See `spec/plan.md` — phase status is reconciled by the `plan-review` skill
after each merge, so don't hard-code the current phase here.
