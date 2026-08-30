---
description: Prepare the current branch for review and open a PR against develop. Runs the applicable review subagents, fixes BLOCKERs, then creates the PR with the repo's standard title/body. Pass an optional title as $ARGUMENTS to override the default.
agent: general
---

Prepare the current branch and open a PR against `develop` for the Harmonica
repo, following the PR-first workflow. Do not merge anything.

1. **Confirm the tree and branch**: `git status` must be clean and the current
   branch must not be `develop`. Fetch (`git fetch origin`) and verify the
   branch is not stacked on a sibling feature branch: (a)
   `git merge-base --is-ancestor origin/develop HEAD` must succeed (develop is
   an ancestor of HEAD), and (b) `git log --oneline origin/develop..HEAD`
   lists only this PR's commits, none from another unmerged branch.
2. **Diff scope**: `git diff origin/develop...HEAD --stat` should contain only
   the intended files. Flag unexpected files to the user before proceeding.
3. **Run the applicable review subagents** against the branch diff and fix any
   BLOCKERs:
   - `build-review` when build files / dependencies / CI changed
     (any `build.gradle.kts`, `settings.gradle.kts`, `gradle/`,
     `gradle.properties`, `.github/`).
   - `exposed-review` when core connection / Exposed-optionality changed
     (core `Connection.kt` / `PluginConfig.kt`, harmonica-exposed bridge).
   - `spec-review` when anything under `spec/` changed.
4. **Verify the build** for any non-documentation change that can affect the
   build — source, build files (`build.gradle.kts`, `settings.gradle.kts`,
   `gradle/`, `gradle.properties`), or CI (`.github/`): run `bin/gw build`
   (or `bin/gw test`) and record the pass + test count. Fix any red build
   before opening the PR.
5. **Commit and push the branch** — only when the user explicitly asked to
   open a PR. If uncommitted changes exist that are not part of this PR, stop
   and ask before committing.
6. **Create the PR** against base `develop` with `gh pr create --base
   develop`:
   - Title (or use `$ARGUMENTS`/`$1` if given): `Fix/Add/Refactor <subject>
     (issue #N)` when tied to an issue, else a short descriptive title.
   - Body with sections: **Changes**, **Verification** (build result + test
     counts), **Residual** (anything intentionally left, e.g. issues noted as
     closable).
7. Return the PR URL from `gh pr view`.