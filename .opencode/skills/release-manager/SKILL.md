---
name: release-manager
description: Use when cutting a Harmonica release — version bump PR, annotated git tag, GitHub Release, and the Gradle Plugin Portal + JitPack publishes. Covers the full release operation: bumping X.Y.Z across all modules/docs, verifying tag/version and CI, pushing the tag to trigger release.yml, and creating GitHub Releases for any tag (including backfilling tags like 3.0.2/3.0.3/4.0.0). Trigger words: "release", "bump version", "tag", "publish", "GitHub release", "portal".
---

# Harmonica Release Manager

End-to-end procedure for releasing Harmonica. Every step below is followed as
of the 4.0.x line; the channel split (Plugin Portal for the plugin, JitPack
for core/exposed) is decided and stable.

## Release topology (reference)

| Artifact | Channel | Trigger |
|---|---|---|
| `gradle-plugin` (`io.github.kenjiohtsuka.harmonica` / `.jarmonica`) | Gradle Plugin Portal | tag push → `.github/workflows/release.yml` → `./gradlew :gradle-plugin:publishPlugins` |
| `core`, `exposed` (`com.improve_future:*`) | JitPack | on-demand; first resolution of `com.github.KenjiOhtsuka.harmonica:{core,exposed}:<tag>` builds from the tag |
| Maven Central (OSSRH) | deferred | not configured for publishing |

- `release.yml` is gated: trigger pattern `4.0.*` (bump it together with the
  feature version), preflight validates the tag matches `version` in
  `gradle-plugin/build.gradle.kts`, and it requires secrets
  `GRADLE_PUBLISH_KEY`/`GRADLE_PUBLISH_SECRET` in the `publish action`
  environment.
- The signal that a release ran is a passing `release.yml` run for the tag;
  the portal ids auto-register on first publish and then go through manual
  review.
- A **GitHub Release** (the "Releases" page entry) is a separate, manual
  step — nothing creates it automatically. Every tag should get one.

## Step 1 — Bump the version (small PR against `develop`)

Never cut a release directly on `develop`. Version bump PRs are code + spec,
one change per PR.

1. `git fetch origin && git checkout -b feature/version-<X.Y.Z> origin/develop`
2. Update `version = "X.Y.Z"` in all published modules and every coordinate
   that references the old version (grep the repo for the old version first):
   - `core/build.gradle.kts`, `exposed/build.gradle.kts`,
     `gradle-plugin/build.gradle.kts`
   - `demo/build.gradle.kts` (its own version, the two plugin ids it applies,
     and the three `implementation(...)`/`harmonica(...)` coordinates)
   - `integration-test/build.gradle.kts` (the two `harmonica-demo` coords)
   - `gradle-plugin/src/test/kotlin/com/improve_future/harmonica/plugin/PluginFlowTest.kt`
     (the TL fixture plugin id + `harmonica("...exposed:X.Y.Z")`)
   - `.github/workflows/release.yml`: trigger `X.Y.*` PLUS the preflight case
     `refs/tags/X.Y.*` and its error string
   - `README.md` (install snippets, JitPack coordinates, module-coordinate
     table) — the anywhere else the old version reads as *current*
3. Update the docs that describe current version (not history):
   `spec/README.md`, `spec/ci.md` (`release.yml` trigger), `spec/plan.md`
   (the "current / next version" records), `spec/tech-notes.md`,
   `spec/exposed-integration.md`. Preserve historical release records at
   their real versions; only re-pin the current-state/next-release claims.
4. `bin/gw build` must pass (also `bin/gw :gradle-plugin:test --rerun-tasks`
   since the TL fixture changed).
5. Open the PR against `develop` (use the `/pr` command flow: run
   `build-review` and `spec-review` first). After the merge, re-anchor onto
   the new `origin/develop`.

## Step 2 — Tag the release

1. `git fetch origin && git checkout develop && git pull --ff-only
   origin/develop`. Confirm `git log --oneline -1` is the merge commit that
   contains the version bump (the tag/version preflight depends on it).
2. Confirm CI is green on that commit (`gh api
   repos/<owner>/<repo>/commits/<sha>/check-runs --jq '.check_runs[]'`) — the
   blue build/check/db-integration checks should be `conclusion=success`.
   `release.yml` (the "submit" check) will be `completed` too.
3. Confirm the tag doesn't already exist: `git ls-remote --tags origin | grep
   <X.Y.Z>`. (Tags are annotated; prior tags carry only the version as the
   message.)
4. `git tag -a X.Y.Z -m "X.Y.Z"` then `git push origin X.Y.Z`.
5. Watch the publish: `gh run list --workflow=release.yml --limit 3`. The tag
   push fires `release.yml` → `publishPlugins`. Track with `gh run watch
   <run-id> --exit-status`. A green run = plugin published to the portal (or
   already-published version short-circuited).
6. JitPack: nothing to trigger. `core`/`exposed` build lazily on first
   resolution of the versioned coordinates.

## Step 3 — Create the GitHub Release

Nothing auto-publishes to the Releases page; do it explicitly per tag.
Existing release names use a short human title like "Harmonica 3.0.1".

1. Inventory tags vs releases:
   - `git tag -l --sort=v:refname`
   - `gh release list --limit 100 --json tagName,name`
   - Diff: which tags lack a Release. Backfill any missing tag the release
     line cares about (e.g. `3.0.2`, `3.0.3`, `4.0.0` were missing and got
     backfilled in the 4.0.1 cycle).
2. Per missing tag: capture the change scope for the notes
   (`git log --oneline --no-merges <prev>..<tag>` and
   `git diff --stat <prev>..<tag>`) and create it:
   `gh release create <tag> --title "Harmonica <tag>" --notes "<notes>"`
   Notes should summarize the user-facing change (e.g. "publishes the plugin
   to the Gradle Plugin Portal under `io.github.kenjiohtsuka.*`") and note
   where artifacts live (portal + JitPack coordinates).
3. Backfilled releases of closed-topic tags stay brief; current releases
   should mention the portal/JitPack artifact locations.

## Step 4 — Post-release reconcile

- Flag the released tag/version to the human (the `develop` HEAD and the tag
  are the two commits that matter).
- The next feature work should mark the version bump / plan records done:
  run `plan-review` to reconcile `spec/` with the new tag, or fold the note
  into the next docs PR.

## Gotchas

- Never tag under an old `com.improve_future.*` plugin id — the portal
  rejects short/legacy ids for new publishes (that is why the 3.0.2 attempt
  failed and ids moved to `io.github.kenjiohtsuka.*`).
- Do not rebase or amend a pushed tag/branch; if a tag is wrong, it cannot be
  force-updated safely once CI/portal have seen it.
- JitPack answers the group `com.github.KenjiOhtsuka.harmonica`; the module
  coordinates keep `com.improve_future:*` for Gradle-local builds but the
  README shows readers the JitPack rewrite.
- The `demo` composite (`rootProject.name = "harmonica-demo"`) is
  coordinate-substituted, so its version, the plugin ids, AND the
  `integration-test` `harmonica-demo` coords must all move together.