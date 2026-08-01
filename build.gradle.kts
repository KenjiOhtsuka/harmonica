/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

buildscript {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.gradle.publish:plugin-publish-plugin:0.9.10")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin_version")}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:2.2.0")
    }
}

plugins {
    kotlin("jvm") version "1.4.20" apply false
}
