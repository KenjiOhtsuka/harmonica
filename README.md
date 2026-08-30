# Harmonica — Kotlin Database Migration Tool

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Build: CI](https://github.com/KenjiOhtsuka/harmonica/actions/workflows/ci.yml/badge.svg)](https://github.com/KenjiOhtsuka/harmonica/actions/workflows/ci.yml)
[![Build: DB integration](https://github.com/KenjiOhtsuka/harmonica/actions/workflows/ci.yml/badge.svg?job=db-integration)](https://github.com/KenjiOhtsuka/harmonica/actions/workflows/ci.yml)
[![Release](https://jitpack.io/v/KenjiOhtsuka/harmonica.svg)](https://jitpack.io/#KenjiOhtsuka/harmonica)

Harmonica is a database migration tool for the JVM, written in Kotlin — a Gradle
plugin backed by a JDBC core library. It is similar in spirit to Phinx and
Rails migrations.

Version **3.0.0** is the maintenance-restart release: the project was dormant
for years and has been rebuilt on a modern toolchain (Kotlin 2.3, Gradle 9.7,
published bytecode targets JVM 8). The biggest change is that Exposed support is
now an **optional, separate module** — the core library no longer depends on
Exposed.

## Supported databases

PostgreSQL, MySQL, SQLite, Oracle, and H2.

- SQLite and H2 are embedded and exercised on every build.
- PostgreSQL and MySQL are verified by the gated integration suite
  (`docker-compose up` + `:integration-test:integrationTest`, required mode in CI).
- The SQL Server adapter is registered but not implemented yet.

You supply the JDBC driver for your database on the runtime classpath.

## Requirements

- Gradle 9.x (built and tested with Gradle 9.7.0 and Kotlin 2.3.20).

## Getting started

### 1. Apply the plugin

```kotlin
plugins {
    id("harmonica") version "3.0.0"
}
```

The plugin registers the tasks `harmonicaUp`, `harmonicaDown`, and
`harmonicaCreate`. The legacy `jarmonica` plugin is also available.

### 2. Point the plugin at your migration scripts

Migrations are `.kts` scripts. Set the root directory via the `directoryPath`
project property, then put scripts in `migration/` and DB config in `config/`:

```kotlin
extra["directoryPath"] = "src/main/kotlin/com/example/myapp/migration"
```

### 3. Write a migration

Run `./gradlew harmonicaCreate -PmigrationName=CreateUsers` to scaffold a
migration file, or create one manually:

```kotlin
import com.improve_future.harmonica.core.AbstractMigration

object : AbstractMigration() {
    override fun up() {
        createTable("users") {
            varchar("name", size = 100, nullable = false)
            integer("age")
            boolean("active", default = true)
        }
        createIndex("users", "name")
        addTextColumn("users", "address")
    }

    override fun down() {
        dropTable("users")
    }
}
```

See `AbstractMigration` and `TableBuilder` for the full migration DSL —
column types, indexes, foreign keys, renames, and raw `executeSql`.

### 4. Run

```console
./gradlew harmonicaUp     # apply pending migrations
./gradlew harmonicaDown   # revert the last migration
```

## Artifacts

| Module | Current coordinates | Channel |
| ------ | ------------------- | ------- |
| Core library | `com.improve_future:core` | JitPack |
| Exposed bridge (optional) | `com.improve_future:exposed` | JitPack |
| Gradle plugin | `harmonica` / `jarmonica` | Gradle Plugin Portal |

The exact publication coordinates for 3.0.0 are being finalized (see Phase 6 of
`spec/plan.md` and issue #1); Maven Central publishing is optional.

## Exposed integration (optional)

If you want to write migrations against Exposed tables, add the bridge to the
plugin's script classpath:

```kotlin
dependencies {
    harmonica("com.improve_future:exposed:2.0.0")
}
```

The bridge currently targets Exposed 0.61.x (Exposed 1.x support is tracked in
issue #215). The plugin-flow test suite verifies migrations both with and
without the Exposed module on the script classpath.

## API documentation

KDoc is generated with Dokka per module and published in the `docs/api`
directory of this repository (served on GitHub Pages at
<https://kenjiohtsuka.github.io/harmonica/>).

## Demo

- [harmonica_demo](https://github.com/KenjiOhtsuka/harmonica_demo) — a working
  example application (Spring Boot + migrations), kept outside this repository.
- [Development instruction](https://improve-future.com/en/spring-boot-with-db-migration.html)

## Contributing

Pull requests are welcome. Work is planned in `spec/plan.md` and organized as
small PRs against `develop`. Real-database integration tests run with Docker
(`docker-compose up`); SQLite and H2 embedded tests run on every build.

## License

MIT