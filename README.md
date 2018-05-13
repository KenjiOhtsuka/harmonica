# Kotlin Database Migration Tool

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
        classpath 'com.github.KenjiOhtsuka:harmonica:0.0.2'
    }
}
```

add plugin

```
apply plugin: 'harmonica'
```

#### Create Config File

Current version harmonica needs the following file structure

```
`- src
  `- main
    `- kotlin
      `- db
        |- config
        | `- default.kts <- configuration file
        `- migration
```

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

#### Create Migration file

Execute `harmonicaCreate` task.

```
./gradlew harmonicaCreate
```

Then, migration file will be created

After editin the migration file, you can migrate with command `./gradlew harmonicaUp`.

## Command

* `harmonicaUp`: migrate
* `harmonicaDown`: migrate down
* `harmonicaCreate`: create migration file
    * `./gradlew harmonicaCreate`: create migration file `XXXXXXXXXX_Migration`
    * `./gradlew harmonicaCreate -PmigrationName=Abcdefg`: create migration file `XXXXXXXXX_Abcdefg`

