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
        classpath("com.gradle.publish:plugin-publish-plugin:2.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin_version")}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.17")
    }
}

plugins {
    kotlin("jvm") version "1.4.20" apply false
}
