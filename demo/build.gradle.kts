buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.xerial:sqlite-jdbc:3.45.3.0")
        classpath("org.postgresql:postgresql:42.7.13")
        classpath("com.mysql:mysql-connector-j:26.7.0")
    }
}

group = "com.improve_future"
version = "3.0.3"

plugins {
    kotlin("jvm") version "2.3.20"
    id("io.github.kenjiohtsuka.harmonica") version "3.0.3"
    id("io.github.kenjiohtsuka.jarmonica") version "3.0.3"
}

extra["directoryPath"] = "src/main/kotlin/com/improve_future/harmonica/demo/script"
extra["migrationPackage"] = "com.improve_future.harmonica.demo.jarmonica"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.kenjiohtsuka:gradle-plugin:3.0.3")
    implementation("com.improve_future:core:3.0.3")
    harmonica("com.improve_future:exposed:3.0.3")
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
}
