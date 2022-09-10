/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

buildscript {
    repositories {
        jcenter()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        // for release
        classpath("com.novoda:bintray-release:${property("bintray_version")}")
        classpath("com.gradle.publish:plugin-publish-plugin:0.9.10")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin_version")}")
        //classpath(group = "org.jetbrains.kotlin", name = "kotlin-script-runtime", version = property("kotlin_version") as String)
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.17")
    }
}

plugins {
    id("com.jfrog.bintray") version "1.8.4" apply false
    kotlin("jvm") version "1.4.20" apply false
}
