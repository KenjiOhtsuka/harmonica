---
name: plan-review
description: Use when reviewing the Harmonica restart plan against reality — after a phase PR or milestone merges, before proposing the next phase, or when asked "are the specs up to date" / "what should we do next". Reconciles spec/plan.md, spec/tech-notes.md, and spec/issues-triage.md with the actual repo (git log, GitHub issues/PRs, dependency versions, test counts), applies updates to spec/, then verifies with the spec-review agent.
---

# Plan Review

The `spec/` planning docs are the source of truth and go stale every time a
phase PR merges. This workflow reconciles them with reality: gather ground
truth, diff the docs, apply updates, verify. **Edit spec/ only; every change
goes through a small PR against `develop`, never a direct push.**

## When to run

- After any phase PR or milestone merge.
- Before proposing the next phase of work.
- When the user asks "are the specs up to date?" / "what should we do next?".

## Scope

Keep in sync: `spec/plan.md`, `spec/tech-notes.md`, `spec/issues-triage.md`.
Do not touch `spec/ci.md`, `spec/testing.md`, `spec/exposed-integration.md`,
or `spec/README.md` unless a finding specifically requires it.

## Steps

### 1. Gather ground truth

Run these and record the results before touching any doc:

- `git fetch origin` then `git log --oneline origin/develop -15` — recent merges.
- `gh pr list --state open` and `gh pr list --state merged --limit 25` — PR states.
- `gh issue list --state open` — note the exact open count.
- Grep versions from the build: `gradle/wrapper/gradle-wrapper.properties`,
  `gradle.properties`, root/`core`/`gradle-plugin` `build.gradle.kts`,
  `settings.gradle.kts` (wrapper, kotlin, junit, plugin-publish, dokka, test deps).
- `./gradlew test` (JDK 25, this machine) for the authoritative test count and
  build health. Static `@Test` grep counts drift from the real JUnit count —
  trust the Gradle output, not the source grep.

### 2. Diff the docs against ground truth

For each of the three docs, find every claim that disagrees with reality:

- **plan.md**: phase status blocks (`implemented`/`merged`, PR numbers, dates,
  test counts); the phase checklists; §6 resolved/open decisions; §3 branch
  strategy notes.
- **tech-notes.md**: dependency table rows still marked `pending`/`untouched`
  that are now `DONE` (add the PR ref); self-contradictions between a table row
  and a later note; test counts; repository/protocol notes.
- **issues-triage.md**: the open-issue count in the header (re-snapshot);
  issues resolved by the work (move them out of the active table, note the
  closing PR); new issues created since the snapshot (add them); stale
  cross-references.

Cross-check the three docs against each other — no contradictions.

### 3. Apply updates

- One logical change per edit; preserve the existing style and table format.
- Mark resolved issues as resolved **in the docs** with the PR reference, but
  do not close the GitHub issues — that is a separate user decision.
- Update the "Snapshot ... <date>" header in issues-triage.md to the **actual
  review date** and the current open-issue count.

### 4. Verify

- Delegate a final pass to the `spec-review` agent (read-only) against the
  updated docs. Fix any BLOCKER/WARNING findings it reports.

### 5. Open a PR

- Commit the spec changes on a `docs/...` branch cut from the current
  `origin/develop` (fetch first; never cut from a sibling unmerged branch).
- Push it and open a small PR against `develop` titled around what the
  reconcile covers.
- Do not close GitHub issues yourself — that remains a user decision; list in
  the PR body the issues this reconcile marks as resolved, with the PR that
  resolves each.

### 6. Report

Summarize: what changed in each doc, the PR URL, the authoritative test count,
and a list of GitHub issues the user should close.
