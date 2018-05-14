# Kotlin Database Migration Tool

[![](https://jitpack.io/v/KenjiOhtsuka/harmonica.svg)](https://jitpack.io/#KenjiOhtsuka/harmonica) [![Twitter Follow](https://img.shields.io/twitter/follow/escamilloIII.svg?style=social)](https://twitter.com/escamilloIII)


Gradle Plugin Page: https://plugins.gradle.org/plugin/com.improve_future.harmonica

This is Database Migration Tool, gradle plugin, made with Kotlin.
It is made similar to Phinx, Rails.

## Usage

### Preparation

#### Gradle

You have to add the following jar to buildscript classpath

* kotlin-script-util
* jdbc
* harmonica

This tool ca be retrieved from jitpack.io, so please add the dependency.

```
repositories {
    maven { url 'https://jitpack.io' }
}
```


Example, using PostgreSQL

```
buildscript {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath group: 'org.jetbrains.kotlin', name: 'kotlin-script-util', version: '1.2.41'
        classpath 'org.postgresql:postgresql:9.4.1212.jre6'
        classpath 'com.github.KenjiOhtsuka:harmonica:0.0.4'
    }
}
```

add plugin

```
apply plugin: 'harmonica'
```

#### Create Config File

Harmonica needs the following file structure

```
`- src
  `- main
    `- kotlin
      `- db
        |- config
        | |- default.kts <- default configuration file
        | `- develop.kts <- you can create own configuration file
        `- migration
```

As default, `db` directory must be in `src/main/kotlin` directory,
but you can change it in `build.gradle` as follows.

```
extensions.extraProperties["directoryPath"] =
        "src/main/kotlin/com/improve_future/test/db"
```

In this case, config files must be in `src/main/kotlin/com/improve_future/test/db/config`,
and migration files must be in `src/main/kotlin/com/improve_future/test/db/migration`.

##### default.kts

This file must includes database configuration as below.

```
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.Dbms

DbConfig.create {
    dbms = Dbms.PostgreSQL
    dbName = "harmonica_test"
    host = "127.0.0.1"
    user = "developer"
    password = "developer"
}
```

If you create another database configuration file, you can use multi databases.

#### Create Migration file

Execute `harmonicaCreate` task.

```
./gradlew harmonicaCreate
```

##### Sample Migration

```
import com.improve_future.harmonica.core.AbstractMigration

object : AbstractMigration() {
    override fun up() {
        createTable("sample_table") {
            integer("column_1")
            varchar("column_2")
            bool("column_3")
        }
    }

    override fun down() {
        dropTable("sample_table")
    }
}
```

Then, migration file will be created

After editing the migration file, you can migrate with command `./gradlew harmonicaUp`.

## Command

* `harmonicaUp`: migrate
    * `./gradlew harmonicaUp`: migrate
    * `./gradlew harmonicaUp -Penv=develop`: migrate using the configuration file, `develop.kts` instead of `default.kts`
* `harmonicaDown`: migrate down
    * `./gradlew harmonicaDown`: migrate down
    * `./gradlew harmonicaDown -Penv=develop`: migrate down using the configuration file, `develop.kts` instead of `default.kts`
* `harmonicaCreate`: create migration file
    * `./gradlew harmonicaCreate`: create migration file `XXXXXXXXXX_Migration`
    * `./gradlew harmonicaCreate -PmigrationName=Abcdefg`: create migration file `XXXXXXXXX_Abcdefg`

