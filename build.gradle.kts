plugins {
    kotlin("jvm") version "2.3.20" apply false
    id("com.gradle.plugin-publish") version "2.1.1" apply false
    id("org.jetbrains.dokka") version "2.2.0" apply false
}

buildscript {
    configurations.classpath {
        resolutionStrategy {
            force(
                "com.fasterxml.jackson.core:jackson-core:2.18.9",
                "com.fasterxml.jackson.core:jackson-databind:2.18.9",
                "com.fasterxml.jackson.module:jackson-module-kotlin:2.18.9",
                "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.9"
            )
        }
    }
}
