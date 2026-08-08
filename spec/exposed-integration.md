# Exposed Optionality — Design

> **Status: design only (Phase 3, not started).** `harmonica-exposed` does not
> exist yet and `exposedTransaction` is a proposed **future** API. Nothing in
> this document is shipped or supported until the artifact, the script-classpath
> handling, transaction tests, and real-DB verification land in Phase 3. Issue
> #91 stays open until then.

## Problem

Users should be able to decide whether to use the Exposed ORM in their
migrations. Consequences:

- Harmonica must **compile and run without Exposed on the classpath**.
- Users who *want* Exposed must be able to use the Exposed DSL inside a
  migration's `up()`/`down()`.
- The fear that motivated removing earlier code: *"if the user doesn't have
  Exposed, compilation might fail."* Compilation fails only if harmonica's own
  classes reference Exposed types at compile time. It must not.

## Current state (from the code)

- `core` has **no Exposed dependency** — good.
- `core/.../Connection.kt` still carries a `hasExposed: Boolean` constructor
  flag whose ternary returns the same value in both branches (dead code), plus
  a commented-out `TransactionManager.current().connection`.
- `gradle-plugin/.../PluginConfig.kt` detects Exposed at runtime via
  `Class.forName("org.jetbrains.exposed.sql.Database")` — this is the right
  *technique* (no compile-time reference), but the flag is not actually used
  anywhere meaningful.
- **Bridge contract**: the bridge targets a **single supported Exposed major**
  — the current `exposed-jdbc` line (package `org.jetbrains.exposed.sql`,
  `Database.connect(DataSource)` + `transaction()` APIs). If a future Exposed
  major changes these APIs, the detection class name in `PluginConfig.kt` and
  the bridge must move together; never support two majors in one bridge.
- GitHub issues that this resolves: #91 (Exposed must be loadable by
  `Class.forName`), #160 ("how to use with Exposed — AbstractMigration has no
  `create` method"), #80 (Exposed version update — out of harmonica's control
  once Exposed is user-supplied).

## Design

### 1. `core` = pure JDBC, zero Exposed knowledge

- Remove the `hasExposed` constructor parameter and the dead ternary from
  `Connection`.
- Expose the underlying connection: add `val jdbcConnection: java.sql.Connection`
  on **`ConnectionInterface`** (not only the concrete `Connection`). Semantics:
  this returns the raw `coreConnection` — it does **not** trigger the silent
  reconnect path (that would mask the close bug in issue #7).
- Migration DSL (`AbstractMigration`, `TableBuilder`, adapters) stays JDBC-only.

### 2. Optional integration module `harmonica-exposed`

A tiny **separate artifact** (Gradle submodule, e.g. `exposed/`) that:

- `api`-depends on `:core` and on `org.jetbrains.exposed:exposed-jdbc` (plus
  `exposed-core`) at a pinned current version. `Database.connect(DataSource)`
  and `transaction()` live in `exposed-jdbc`, so it must be the JDBC module,
  not just `exposed-core`.
- Provides a bridge, e.g.:

```kotlin
fun AbstractMigration.exposedTransaction(block: () -> Unit) {
    // Register Exposed ONCE per connection lifecycle, then reuse.
    // Do NOT call Database.connect() on every invocation (that accumulates
    // global registrations in TransactionManager and breaks on reconnect).
    val db = (connection as? Connection)?.exposedDatabase()
        ?: error("requires a com.improve_future.harmonica.core.Connection")
    transaction(db) { block() }
}
```

  `exposedDatabase()` adapts the underlying `jdbcConnection` (exposed on
  `ConnectionInterface`, §1) into a **single-connection `DataSource`** and calls
  `Database.connect(dataSource)` **once** per connection lifecycle. The
  `DataSource` overload is used because it is the supported connect form across
  the pinned Exposed major; the same DataSource is reused for the whole
  migration so the transaction owner is stable.

- The bridge is an extension on **`AbstractMigration`** (or the receiver is the
  migration itself), because inside `up()` the receiver is `AbstractMigration`
  and its `connection` field is typed `ConnectionInterface` — an extension on
  the concrete `Connection` would not resolve. See Pitfall E below.

### 2.1 Transaction ownership (the critical design point)

The migration runner already wraps `up()`/`down()` in
`Connection.transaction` (`MigrationUpTask.kt:21-27`, `JarmonicaUpMain.kt:27-34`),
which commits, and on failure rolls back **and closes** the connection
(`Connection.kt:107-117`). Running Exposed's own `transaction(db){}` on the
**same physical connection** produces a nested commit/rollback pair (harmless
on success; on failure a double-rollback plus a stale Exposed `Database`
pointing at the closed connection).

Decide **one owner** (open question #3):

- **Option A (recommended): harmonica owns the transaction.** The bridge binds
  harmonica's connection into Exposed's manager and runs the Exposed DSL
  *without* an Exposed-managed commit (i.e. `TransactionManager`/stack-based
  registration, or `transaction(db){}` where the commit is a no-op because the
  outer `Connection.transaction` commits). Users write the Exposed DSL; harmonica
  does commit/rollback/close.
- **Option B: Exposed owns the transaction.** Then the harmonica runner must
  NOT wrap migrations in `Connection.transaction` when the bridge is used
  (require an explicit opt-in), and harmonica's `Connection.transaction` is
  bypassed.

Until this is decided, the snippet is illustrative only and must not be shipped
as written.

### 2.2 Other pitfalls to design around

- **`setTransactionIsolation` no-op proxy**: `Connection.connect()` wraps every
  non-SQLite JDBC connection in a proxy that overrides `setTransactionIsolation`
  to a no-op (`Connection.kt:18`). Exposed may derive isolation from its own
  config and try to apply it — it will be silently ignored. Acceptable for now;
  document it, or decide whether the proxy override is still needed at all.
- **Reconnect invalidation**: the proxy is recreated on every reconnect
  (`Connection.kt:29-38`). A cached Exposed `Database` bound to an old connection
  is stale. The bridge must re-register after reconnect (key the cache by
  connection instance) or, per Option A, register fresh within each migration.
- **Single-threaded assumption**: `Database.connect(DataSource)` wrapping one
  connection reuses that connection for every transaction and is not
  thread-safe. The migration runner is single-threaded, so this holds —
  document it.
- **`autoCommit`**: harmonica forces `autoCommit = false` (connect + at the top
  of `transaction`). Exposed transactions assume `autoCommit = false` too;
  verify the pinned Exposed version doesn't restore `autoCommit = true` after a
  transaction (harmonica's next `transaction` self-heals via `Connection.kt:108`).
- **Name collision**: `core` has an (empty) `com.improve_future.harmonica.core.Database`
  class (`Database.kt`). Avoid wildcard imports in the bridge so it doesn't
  collide with `org.jetbrains.exposed.sql.Database`.
- **Script classpath (Pitfall F)**: migration `.kts` files are evaluated by the
  Gradle plugin's JSR-223 engine on the **plugin's classpath**
  (`AbstractMigrationTask.kt:33-37`). Adding `harmonica-exposed` via the user's
  `implementation(...)` is not enough — the bridge/Exposed must be reachable by
  the script engine. Options: a plugin-managed configuration appended to the
  script classpath, or `buildscript { dependencies { classpath(...) } }`.
  Design this explicitly in Phase 3.

### 3. No `Class.forName` special-casing in core

- Runtime reflection detection in the Gradle plugin was a workaround for when
  core *did* reference Exposed. Once core is clean, delete it unless we keep
  `PluginConfig.hasExposed()` purely for diagnostics/docs.
- Plugin behavior must be identical with or without Exposed present.

### 4. User story (both ways)

Without Exposed (default):

```kotlin
class M20260801_Migrate : AbstractMigration() {
    override fun up() {
        createTable("users") { varchar("name", 100) }
    }
}
```

With Exposed (opt-in artifact + user's own Exposed dependency):

```kotlin
class M20260801_Migrate : AbstractMigration() {
    override fun up() {
        exposedTransaction {
            // Exposed DSL here; runs inside harmonica's connection/transaction
        }
    }
}
```

## Verification / Definition of done

- `./gradlew :core:compileKotlin` and full build succeed **without** Exposed on
  the classpath anywhere.
- `./gradlew :core:dependencies` shows no `exposed` modules.
- A test/demo project compiles a migration using the Exposed DSL and runs it
  against a real DB.
- `Connection` has no `hasExposed` flag; `PluginConfig` no longer special-cases
  Exposed (or uses it only for diagnostics).
- Documented in README + wiki with the two usage paths above.

## Open questions

- Publish `harmonica-exposed` as a separate artifact, or start with a
  documented snippet and promote to an artifact later?
- Exact Exposed version to use in the bridge (match user expectations; keep it
  a normal version range).
- Whether `exposedTransaction` should manage commit/rollback or delegate to
  `Connection.transaction`.
