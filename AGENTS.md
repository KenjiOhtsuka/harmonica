# AGENTS.md

Guidance for AI agents working on the Harmonica codebase.

## Project

Harmonica is a Kotlin DB migration tool (core library + a Gradle plugin,
historically bundled with the Exposed ORM). Development was paused for years
and is being restarted. See `spec/plan.md` for the master plan.

## Important constraints

- **Target bytecode is JVM 8** (`jvmTarget = 1.8`). Never bump this.
- The only JDK installed on the dev machine is **OpenJDK 25**.
- The repo is on **Gradle wrapper 9.7.0** and **Kotlin 2.3.20** (Phase 0, merged
  via PR #183; wrapper bumped via PR #211). `./gradlew build` works on this machine.
- **Small changes per PR** against `develop`.
- **Never push directly to `develop`.** Every change — code *and* docs/spec —
  goes through a small PR against `develop`.
- **Never commit** unless explicitly asked. Stage only intended files.

## Layout

- `core/` — the migration library (JDBC, no Exposed dependency).
- `gradle-plugin/` — Gradle plugin + scripting host for `.kts` migrations.
- `document/` — nested standalone Gradle build (site generator), stale.
- `spec/` — planning docs; the source of truth for the restart. Read them
  before proposing work; update them when decisions change.
- `.opencode/` — opencode config, review agents, and this project's skill.

## Conventions

- Use `bin/gw` to run Gradle for the root build (plain console, output trimmed
  to ~60 lines) instead of `./gradlew`; it saves output tokens. `bin/gw` changes
  to the repository root, so it cannot run tasks of the nested `document/`
  build — use `document/gradlew` there. Use `./gradlew` directly when full
  output is needed.
- Follow existing code style; do not add comments unless asked.
- Do not add dependencies that are not actually used (there is a history of
  unused deps: `commons-codec`, `kotlin-reflect`, `kotlinx-html-jvm` in the
  plugin, JDBC test drivers).
- Keep `core` free of any Exposed import. See `spec/exposed-integration.md`.
- When editing opencode config (`opencode.json`, `.opencode/**`), consult the
  `customize-opencode` skill and the schema at https://opencode.ai/config.json.

## Git workflow

- Cut feature branches from the current `origin/develop` (fetch first). Never
  cut from a sibling unmerged feature branch — it contaminates the PR diff.
- When integrating `develop` into a PR branch, prefer `git merge` over rebase.
  Never `git pull --rebase` a branch that already contains merge commits, and
  never amend or force-push commits that exist on the remote.
- Before opening a PR, run the applicable review subagents against the branch
  diff (spec-review for `spec/`, build-review for build/dependency/CI changes,
  exposed-review for core connection / Exposed work) and fix any BLOCKERs. The
  `/pr` command encodes this flow.
