# Exposed Optionality — Design

> **Status: shipped in part (2026-08-09, PR B).** The `exposed/` module
> (artifact `harmonica-exposed`), the `exposedTransaction` bridge, and embedded
> SQLite transaction tests (commit + rollback paths) exist and pass
> (`./gradlew :exposed:test`, 3 tests). Still open in Phase 3: script-classpath
> wiring for `.kts` migrations (Pitfall F) and a demo project. Issue #91 stays
> open until the whole flow ships.

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

- `core` has **no Exposed dependency** — good. The dead `hasExposed` flag on
  `Connection` was removed (Phase 3, PR A): `Connection` no longer carries it
  and `Connection.create` no longer takes it.
- `ConnectionInterface` exposes **`jdbcConnection`** (raw
  `java.sql.Connection`, no silent reconnect) — added in PR A so the bridge can
  reach the underlying connection.
- `gradle-plugin` no longer special-cases Exposed: `PluginConfig.hasExposed()`
  (the `Class.forName` runtime detection) was **deleted** in PR A. Plugin
  behavior is identical with or without Exposed on the classpath.
- `harmonica-exposed` exists as module `exposed/` (PR B): `api`-depends on
  `:core` and `exposed-jdbc:0.61.0` (per spec/tech-notes.md), provides
  `AbstractMigration.exposedTransaction { }`, and is covered by SQLite
  transaction tests. `core` and `gradle-plugin` are unchanged by it.
- **Bridge contract**: the bridge targets a **single supported Exposed major**:
  **Exposed 0.x — pinned 0.61.0** — the major whose JDBC API lives in package
  `org.jetbrains.exposed.sql` (`Database.connect(DataSource)` +
  `transaction()`).
  Exposed 1.x moved these classes to `org.jetbrains.exposed.v1.jdbc.*`, so
  adopting 1.x requires updating the detection class name and bridge APIs
  together; never support two majors in one bridge.
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

A tiny **separate artifact** (Gradle submodule `exposed/`) that:

- `api`-depends on `:core` and on `org.jetbrains.exposed:exposed-jdbc` (plus
  `exposed-core` transitively) at a pinned current version.
  `Database.connect(...)` and `transaction()` live in `exposed-jdbc`, so it
  must be the JDBC module, not just `exposed-core`.
- Provides the bridge (as shipped in PR B):

```kotlin
fun AbstractMigration.exposedTransaction(block: Transaction.() -> Unit) {
    val connection = connection as? Connection
        ?: error("exposedTransaction requires a com.improve_future.harmonica.core.Connection")
    transaction(connection.exposedDatabase()) { block() }
}
```

  `exposedDatabase()` (private, in `ExposedMigration.kt`) registers Exposed
  **once per `Connection` lifecycle** and reuses it:

  - Cache: `WeakHashMap<Connection, Database>` keyed by the `Connection`
    instance. `Database.connect` is called only when a new key appears, so
    repeated `exposedTransaction` calls inside one migration reuse the same
    `Database` instead of accumulating global registrations in
    `TransactionManager`.
  - Connect form: `Database.connect(getNewConnection = { proxy }, databaseConfig =
    DatabaseConfig { defaultMaxAttempts = 1 })`. `defaultMaxAttempts = 1`
    disables Exposed's SQLException retry loop so DDL is never re-executed.
  - The connector returns a **`java.lang.reflect.Proxy`** implementing
    `java.sql.Connection` that delegates **every method to the connection's
    current `jdbcConnection`** except `commit`, `rollback`, and `close`, which
    are **no-ops** — harmonica's outer `Connection.transaction` owns the real
    commit/rollback/close (Option A, §2.1). `JdbcConnectionImpl` (exposed-jdbc)
    delegates through the proxy, so no Exposed path reaches the physical
    connection's commit/rollback/close.
  - The `Database` holds only a `WeakReference` to the `Connection` (the proxy
    resolves `jdbcConnection` on every call), avoiding the classic
    weak-key/strong-value leak.

- The bridge is an extension on **`AbstractMigration`** (or the receiver is the
  migration itself), because inside `up()` the receiver is `AbstractMigration`
  and its `connection` field is typed `ConnectionInterface` — an extension on
  the concrete `Connection` would not resolve. See Pitfall E below.
- The block receiver is Exposed's `Transaction` (`block: Transaction.() -> Unit`),
  so DSL calls that need the transaction receiver (e.g. `exec`) work inside
  `exposedTransaction {}`.

### 2.1 Transaction ownership (the critical design point)

The migration runner already wraps `up()`/`down()` in
`Connection.transaction` (`MigrationUpTask.kt:21-27`, `JarmonicaUpMain.kt:27-34`),
which commits, and on failure rolls back **and closes** the connection
(`Connection.kt:106-117`). Running Exposed's own `transaction(db){}` on the
**same physical connection** produces a nested commit/rollback pair (harmless
on success; on failure a double-rollback plus a stale Exposed `Database`
pointing at the closed connection).

Decide **one owner** — **resolved (2026-08-08): Option A, harmonica owns the
transaction.**

- **Option A (chosen, implemented in PR B): harmonica owns the transaction.** The
  bridge binds harmonica's connection into Exposed's manager and runs the
  Exposed DSL *without* an Exposed-managed commit: `transaction(db){}` runs
  against the no-op proxy (§2), so Exposed's commit/rollback/close are inert and
  the outer `Connection.transaction` commits. Users write the Exposed DSL;
  harmonica does commit/rollback/close. Verified by the SQLite tests: the commit
  path persists the Exposed DDL+insert; the failure path (`error("boom")` inside
  `exposedTransaction`) rolls back the CREATE TABLE and reconnects the closed
  connection.
- **Option B (rejected): Exposed owns the transaction.** Then the harmonica runner must
  NOT wrap migrations in `Connection.transaction` when the bridge is used
  (require an explicit opt-in), and harmonica's `Connection.transaction` is
  bypassed.

### 2.2 Other pitfalls to design around

- **`setTransactionIsolation` no-op proxy**: `Connection.connect()` wraps every
  non-SQLite JDBC connection in a proxy that overrides `setTransactionIsolation`
  to a no-op (`Connection.kt:18`). The bridge proxy delegates isolation through
  to that proxy, so Exposed's isolation setting is silently ignored for
  non-SQLite. For SQLite, core exposes the raw connection and Exposed's default
  (SERIALIZABLE) applies. Acceptable for now; revisit whether core's proxy
  override is still needed at all.
- **Reconnect invalidation (resolved in PR B)**: harmonica reconnects a closed
  connection lazily (`Connection.kt:29-38`). The bridge proxy resolves
  `jdbcConnection` **on every method call**, so once a reconnect has been
  triggered by any core call, Exposed automatically targets the fresh
  connection. The cache is keyed by the `Connection` instance, so a *new*
  `Connection` (e.g. a new migration run) gets its own `Database`.
  `testExposedDslSurvivesHarmonicaReconnect` proves this end-to-end: a failed
  migration closes the connection, then a second `exposedTransaction` on the
  same `Connection` creates + inserts successfully through the reconnected
  connection.
- **Single-threaded assumption**: the cached `Database` wraps one connection and
  is reused for every Exposed transaction; it is not thread-safe. The migration
  runner is single-threaded, so this holds. The `WeakHashMap` cache is likewise
  not thread-safe — document the single-threaded assumption.
- **`autoCommit`**: harmonica forces `autoCommit = false` (connect + at the top
  of `transaction`). Exposed 0.61's `ThreadLocalTransaction` init also sets
  `autoCommit = false` on the wrapped connection; it does not restore
  `autoCommit = true` after a transaction, so the two agree. Verified by the
  transaction tests.
- **Retry re-execution (guarded)**: Exposed retries failed transactions up to
  `defaultMaxAttempts` times, which would re-execute DDL. The bridge sets
  `defaultMaxAttempts = 1` (attempts counter starts at 0, so exactly one
  execution).
- **Name collision**: `core` has an (empty) `com.improve_future.harmonica.core.Database`
  class (`Database.kt`). Avoid wildcard imports in the bridge so it doesn't
  collide with `org.jetbrains.exposed.sql.Database`.
- **`setReadOnly` is delegated**: Exposed 0.61's transaction setup calls
  `setTransactionIsolation(...)` (already no-op'd for non-SQLite by core's proxy)
  **and** `setReadOnly(false)` on the wrapped connection. `setReadOnly` is *not*
  overridden anywhere, so for PostgreSQL/MySQL it executes driver-side state
  changes on the open transaction. Deferred to the Phase 4 real-DB work.
- **Exposed's global registry grows in long-lived JVMs (known limitation)**: 
  `Database.connect` registers every `Database` in Exposed's global
  `TransactionManager` (`ConcurrentHashMap` + `ConcurrentLinkedDeque`) for the
  JVM lifetime, and the bridge never calls `closeAndUnregister` (core purity
  forbids a close hook). The bridge's own `WeakHashMap` cache evicts correctly,
  but each distinct `Connection` (e.g. each migration run inside a Gradle
  daemon) leaves one permanently-registered `Database` whose connector holds a
  dead `WeakReference`. Bounded per run, unbounded over a long-lived JVM.
  Acceptable for a migration tool (short-lived task JVMs); revisit if harmonica
  gains a long-lived server mode. Note also that a new `Database.connect`
  overwrites Exposed's *global default* manager, so a bare `transaction {}` in
  user code afterwards routes to the bridge's proxy-backed `Database` —
  harmless for harmonica's flow.
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

**Shipped in PR B (2026-08-09):** the `exposed/` module builds with
`exposed-jdbc:0.61.0`; `exposedTransaction` runs the Exposed DSL inside
harmonica's `Connection.transaction` (Option A) proven by three SQLite tests
(`:exposed:test`): commit (DDL+insert persist; Exposed did not close the
connection or restore autoCommit), rollback (harmonica DSL + Exposed DSL in one
migration both rolled back — one shared transaction), and reconnect (a second
`exposedTransaction` succeeds through harmonica's reconnected connection). Full
`./gradlew build` is green with and without Exposed on the classpath; `:core`
still has zero Exposed references.

**Remaining for Phase 3:** script-classpath wiring for `.kts` migrations
(Pitfall F) and the demo project against a real DB (SQLite here; PostgreSQL/MySQL
deferred to Phase 4 integration tests).

## Open questions

Resolved (2026-08-08):

- **Separate artifact** — ship `harmonica-exposed` as a Gradle module, not a
  snippet first.
- **Exposed 0.61.0** — latest 0.x, JDBC API in `org.jetbrains.exposed.sql`.
- **Option A** — harmonica owns the transaction (no Exposed-managed commit).

Resolved (2026-08-09, PR B):

- **No-op-commit wrapper needed? Yes** — verified empirically. Plain
  `transaction(db){}` on harmonica's raw connection would commit/rollback/close
  the shared physical connection (a double commit on success, double rollback +
  stale Database on failure). The bridge therefore wraps the connection in a
  `java.lang.reflect.Proxy` that no-ops `commit`/`rollback`/`close` and
  delegates everything else (see §2). Both the commit path and the rollback
  path are covered by the SQLite tests.

Remaining:

- Script classpath wiring for `.kts` migrations (Pitfall F).
