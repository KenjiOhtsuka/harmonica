# GitHub Issue Triage

Snapshot of **open** issues on 2026-09-22 (**25 open** total). Grouped by
urgency/size. Requirements, design, and implementation are tracked in the
other spec docs and GitHub; entries here are only the open issues.

## URGENT — blocking restart

No remaining items in this tier — all original blockers were resolved and
closed by the Phase 0-5 work (phase histories in [plan.md](plan.md)).

## SMALL — quick wins (Phase 5)

| # | Title | Plan |
| --- | --- | --- |
| 26 | Consolidate the migration file name between harmonica and jarmonica | Pick one naming convention; update both tasks + tests. |
| 47 | Use prepared statement | Escape/`PreparedStatement` for default values (esp. varchar with quotes). |
| 138 | Is there a need to make AbstractColumn internal? | Open up custom-column extension points (make `AbstractColumn`/`ColumnBuilder` public, document custom column pattern). |
| 141 | Add support to more column types | Long is done; add Enumeration and other missing types (see Exposed type list). |
| 145 | Add alter column function | Add `alterColumn` to change type/options. |
| 139 | Update addForeignKey | Support ON DELETE / ON UPDATE / constraint options. |
| 69 | Add sophisticated query executing method | Provide a richer `executeQuery`/query-returning API. |
| 4 | add timestamp syntax to add created_at and updated_at | Convenience columns/timestamps helper. |

## MEDIUM

| # | Title | Plan |
| --- | --- | --- |
| 7 | Sometimes migration stops because of closed connection | Reproduce & fix connection lifecycle (README "Caution" section documents a workaround). |
| 85 | Research SQLite default value in other migration tools | Research rails/phinx behavior; align `createTime`/`createDateTime`/`createTimestamp` defaults for SQLite. |
| 67 | Default value for timestamp | `DEFAULT CURRENT_TIMESTAMP` (or constant) for `createTimestamp` etc.; ties into #85 SQLite-defaults research. |
| 80 | Update exposed version | Out of harmonica's control once Exposed is optional (see exposed-integration.md); document supported version. |
| 71 | consider to add or not seeding function | Decide scope; if yes, design a `seed` task. |
| 97 | Add Test to Sub Classes of JavaExec | Gradle TestKit tests for `JarmonicaMigrationTask` and friends. |
| 155 | [feature] Programmatic migrations and/or documentation for it | Document programmatic API + add convenience entry points. |

## LARGE / STRATEGIC (separate designs & PRs)

| # | Title | Plan |
| --- | --- | --- |
| 1 | put into maven central | Publishing target — **decided 2026-09-13/16**: JitPack for `core`/`exposed` (3.0.1/3.0.3), Plugin Portal for the plugin (first publish tag 3.0.3 under `io.github.kenjiohtsuka`, core bundled; the tag-3.0.2 portal attempt was rejected); Maven Central deferred. |
| 243 | add new plugins | Superseded — the id/portal strategy it proposes (3.x under new ids, legacy `com.improve_future.harmonica` untouched, `jarmonica` as an alias) was decided in PRs #242/#244: namespaced ids. **Refined 2026-09-16:** the portal rejected the historical `com.improve_future/*` coordinates, so the 3.x ids moved to `io.github.kenjiohtsuka.harmonica`/`io.github.kenjiohtsuka.jarmonica` (legacy 1.1.24 id untouched, publish on tag 3.0.3). Close. |
| 125 | Handle Multiple database | Multi-DB config; sizable design. |
| 121 | Dry run | `-Pdry` SQL preview (PostgreSQL first). |
| 148 | Maven support | There is a `feature/maven-plugin` branch — evaluate resurrecting it. |
| 147 | Java support | Kotlin-first but make Java API ergonomic (Java-friendly DSL/annotations). |
| 105 | Separate DB Adapter files and migration | Plugin-able adapter SPI so others can add DBMS without modifying core. |
| 107 | Update SQL Server Adapter | Finish missing methods in `SqlServerAdapter`. |
| 41 | Autogenerate migrations | Diff schemas and generate migration files. |
| 20 | Research other tools | Compare rails/phinx/flyway/liquibase features; use to prioritize. |

## Process

- Close issues that the Phase 0/2 upgrades resolve, referencing the PR.
- Small items are worked as a backlog with acceptance criteria as part of
  Phase 5.
- Keep "large/strategic" items open as RFCs until a design doc exists.
