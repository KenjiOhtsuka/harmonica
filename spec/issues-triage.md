# GitHub Issue Triage

Snapshot of **open** issues on 2026-08-08 (**28 open** total). Grouped by
urgency/size. Many are already fixed (or fixable) by the toolchain/dependency
upgrade in Phase 0/2.

## URGENT — blocking restart

| # | Title | Plan |
| --- | --- | --- |
| 153 | Tasks in jarmonica gradle plugin don't work anymore | Investigate during Phase 5; add tests (ties into #97). |

**Resolved and closed by Phase 0/2:** #167 (jcenter removed, CI rewritten,
PR #183), #165 (LICENSE already MIT; README badge URLs fixed, PR #181), #140
(kotlin-pluralizer removed, internal `singularize()`, PR #187).

**Closed / superseded (were listed as URGENT, now resolved on `develop`):**
#159 (build failure Win10 openjdk 15), #158 (build failure Win/Linux
openjdk 16), and #162 (Build on Java 17) are all **closed** — same root cause
(old Kotlin 1.4.20 toolchain on new JDKs), fixed by the Phase 0 toolchain
upgrade; drop them from active tracking. PR #163 ("update to JDK 17") is now
**closed** — superseded by Phase 0.

## SMALL — quick wins (Phase 5)

| # | Title | Plan |
| --- | --- | --- |
| 189 | `isSubtypeOf` in scanner swallows all `Throwable` | Narrow to recoverable exceptions to match `loadClass` (PR #188); small fix in `JarmonicaTaskMain`. |
| 182 | README JitPack badge shows nothing | Fix JitPack badge so it renders (README badge URLs fixed in PR #181, but this badge still broken). |
| 26 | Consolidate the migration file name between harmonica and jarmonica | Pick one naming convention; update both tasks + tests. |
| 47 | Use prepared statement | Escape/`PreparedStatement` for default values (esp. varchar with quotes). |
| 91 | Exposed Library must be loaded by Class.forName | Resolved by [exposed-integration.md](exposed-integration.md): core has no Exposed reference. |
| 138 | Is there a need to make AbstractColumn internal? | Open up custom-column extension points (make `AbstractColumn`/`ColumnBuilder` public, document custom column pattern). |
| 141 | Add support to more column types | Long is done; add Enumeration and other missing types (see Exposed type list). |
| 145 | Add alter column function | Add `alterColumn` to change type/options. |
| 67 | Default value for timestamp | Medium-ish small: default `DEFAULT CURRENT_TIMESTAMP` (or constant) for `createTimestamp` etc.; ties into #85 SQLite-defaults research. |
| 139 | Update addForeignKey | Support ON DELETE / ON UPDATE / constraint options. |
| 69 | Add sophisticated query executing method | Provide a richer `executeQuery`/query-returning API. |
| 4 | add timestamp syntax to add created_at and updated_at | Convenience columns/timestamps helper. |

## MEDIUM

| # | Title | Plan |
| --- | --- | --- |
| 7 | Sometimes migration stops because of closed connection | Reproduce & fix connection lifecycle (README "Caution" section documents a workaround). |
| 85 | Research SQLite default value in other migration tools | Research rails/phinx behavior; align `createTime`/`createDateTime`/`createTimestamp` defaults for SQLite. |
| 80 | Update exposed version | Out of harmonica's control once Exposed is optional (see exposed-integration.md); document supported version. |
| 71 | consider to add or not seeding function | Decide scope; if yes, design a `seed` task. |
| 97 | Add Test to Sub Classes of JavaExec | Gradle TestKit tests for `JarmonicaMigrationTask` and friends. |
| 155 | [feature] Programmatic migrations and/or documentation for it | Document programmatic API + add convenience entry points. |

## LARGE / STRATEGIC (separate designs & PRs)

| # | Title | Plan |
| --- | --- | --- |
| 1 | put into maven central | Publishing target — decide JitPack vs Maven Central (Phase 6 open decision). |
| 125 | Handle Multiple database | Multi-DB config; sizable design. |
| 121 | Dry run | `-Pdry` SQL preview (PostgreSQL first). |
| 148 | Maven support | There is a `feature/maven-plugin` branch — evaluate resurrecting it. |
| 147 | Java support | Kotlin-first but make Java API ergonomic (Java-friendly DSL/annotations). |
| 105 | Separate DB Adapter files and migration | Plugin-able adapter SPI so others can add DBMS without modifying core. |
| 107 | Update SQL Server Adapter | Finish missing methods in `SqlServerAdapter`. |
| 41 | Autogenerate migrations | Diff schemas and generate migration files. |
| 20 | Research other tools | Compare rails/phinx/flyway/liquibase features; use to prioritize. |

## Closed-but-related (context)

- #168 (PR, merged): bug-build-issue — build fixes already on `develop`.
- #160 (closed): "How it should be used with Exposed" — reopened conceptually in
  exposed-integration.md.
- #164 (PR, not merged): actual title **"Task/boko 49"** — a closed PR; not an
  issue and not part of active triage (was previously miscatalogued).
- #163 (PR, "update to JDK 17") — **closed**; superseded by Phase 0.

## Process

- Close issues that the Phase 0/2 upgrades resolve, referencing the PR.
- Convert "small" items into a backlog with acceptance criteria before starting
  Phase 5.
- Keep "large/strategic" items open as RFCs until a design doc exists.
