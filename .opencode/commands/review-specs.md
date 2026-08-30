---
description: Reconcile the spec/ planning docs (plan.md, tech-notes.md, issues-triage.md) with the actual repo, land the reconcile as a docs PR against develop, and report closable issues. Wrapper for the plan-review skill.
agent: general
---

Run the `plan-review` skill: gather ground truth (`git log` on `origin/develop`,
`gh issue`/`gh pr` lists, dependency versions from the build files,
`./gradlew test` for the authoritative test count), diff `spec/plan.md`,
`spec/tech-notes.md`, and `spec/issues-triage.md` against it, apply the
updates to `spec/`, delegate a final verification pass to the `spec-review`
agent, then commit on a `docs/...` branch cut from `origin/develop` and open a
small PR against `develop` (never a direct push). Do not close GitHub issues
yourself. Report what changed, the PR URL, and which GitHub issues the user
should close.
