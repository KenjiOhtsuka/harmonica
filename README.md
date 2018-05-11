You have to add the following jar to buildscript classpath

* kotlin-script-util
* jdbc
* harmonica

```
buildscript {
    dependencies {
        classpath group: 'org.jetbrains.kotlin', name: 'kotlin-script-util', version: '1.2.41'
        classpath 'org.postgresql:postgresql:9.4.1212.jre6'
        classpath files('/lib/harmonica-1.0-SNAPSHOT.jar')
    }
}
```

add plugin

```
apply plugin: 'harmonica'
```


configure db setting

```
harmonica {
    host = ""
    dbName = ""
    username = ""
    password = ""
    migrationDirPath =
            projectDir.path + "/src/main/..."
}
```