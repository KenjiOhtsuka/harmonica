---
description: Reconcile the spec/ planning docs (plan.md, tech-notes.md, issues-triage.md) with the actual repo and apply updates. Wrapper for the plan-review skill.
agent: general
---

Run the `plan-review` skill: gather ground truth (`git log` on `origin/develop`,
`gh issue`/`gh pr` lists, dependency versions from the build files,
`./gradlew test` for the authoritative test count), diff `spec/plan.md`,
`spec/tech-notes.md`, and `spec/issues-triage.md` against it, apply the
updates to `spec/` (never commit), then delegate a final verification pass to
the `spec-review` agent. Report what changed and which GitHub issues the user
should close.
