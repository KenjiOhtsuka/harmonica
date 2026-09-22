# Harmonica — Kotlin Database Migration Tool

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Build: CI](https://github.com/KenjiOhtsuka/harmonica/actions/workflows/ci.yml/badge.svg)](https://github.com/KenjiOhtsuka/harmonica/actions/workflows/ci.yml)
[![Release](https://jitpack.io/v/KenjiOhtsuka/harmonica.svg)](https://jitpack.io/#KenjiOhtsuka/harmonica)

Harmonica is a database migration tool for the JVM, written in Kotlin — a Gradle
plugin backed by a JDBC core library. It is similar in spirit to Phinx and
Rails migrations.

Release **3.0.1** is the maintenance-restart release: the project was
dormant for years and has been rebuilt on a modern toolchain (Kotlin 2.3,
Gradle 9.7, published bytecode targets JVM 8). The biggest change is that Exposed
support is now an **optional, separate module** — the core library no longer
depends on Exposed. The 3.x line publishes to the Gradle Plugin Portal under
the `io.github.kenjiohtsuka` namespace (portal ownership is verified against the
owner's GitHub account; the historical `com.improve_future.harmonica` id
(1.1.24 and older) is untouched for existing users).

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
    id("io.github.kenjiohtsuka.harmonica") version "3.0.3"
}
```

The plugin registers the tasks `harmonicaUp`, `harmonicaDown`, and
`harmonicaCreate`. The legacy `io.github.kenjiohtsuka.jarmonica` plugin is also
published.

The published plugin is self-contained (the core library is bundled into the
plugin jar), so applying it from the Plugin Portal needs no extra
`pluginManagement` repositories. Only the optional Exposed bridge (below) is
resolved from JitPack. Core ships binary-only inside the plugin jar; the
JitPack artifacts under "Download" provide core's sources. The legacy
`jarmonica` plugin forks a JVM on the project's `runtimeClasspath`, so there
you still need `io.github.kenjiohtsuka:gradle-plugin` (and core) as project
dependencies — see "Download".

To develop against a source checkout instead, apply the id without a version
and add `includeBuild("..")` for the repository root to your
`settings.gradle.kts`.

### 2. Point the plugin at your migration scripts

Migrations are `.kts` scripts. Set the root directory via the `directoryPath`
extra property, then put scripts in `migration/` and DB config in `config/`:

```kotlin
extra["directoryPath"] = "src/main/kotlin/com/example/myapp"
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

## Download

The Gradle plugin is published to the Gradle Plugin Portal (see "Getting
started") without a core dependency: the library ships inside the plugin jar.
The 3.0.3 core library (and the optional Exposed bridge) are served from
[JitPack](https://jitpack.io/#KenjiOhtsuka/harmonica), which builds them from
the `3.0.3` tag. Keep Maven Central in the repositories (the Exposed bridge
depends on Exposed artifacts from Central), and add the JitPack repository for
the core library:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.KenjiOhtsuka.harmonica:core:3.0.3")
}
```

The optional Exposed bridge is a separate artifact:

```kotlin
dependencies {
    implementation("com.github.KenjiOhtsuka.harmonica:exposed:3.0.3")
}
```

| Module | Coordinate (3.0.3) |
| ------ | ------------------- |
| Core library | `com.github.KenjiOhtsuka.harmonica:core` |
| Exposed bridge (optional) | `com.github.KenjiOhtsuka.harmonica:exposed` |

Maven Central for the libraries is deferred past the first release.

## Exposed integration (optional)

If you want to write migrations against Exposed tables, add the bridge to the
plugin's script classpath:

```kotlin
dependencies {
    harmonica("com.github.KenjiOhtsuka.harmonica:exposed:3.0.3")
}
```

The JitPack repository from "Download" must be on the build's repository list
so the `harmonica` configuration can resolve the bridge. The bridge targets
Exposed 1.x (currently pinned to 1.5.0). The plugin-flow test suite verifies
migrations both with and without the Exposed module on the script classpath.

## API documentation

KDoc is generated with Dokka per module and published in the `docs/api`
directory of this repository (served on GitHub Pages at
<https://kenjiohtsuka.github.io/harmonica/api/>).

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