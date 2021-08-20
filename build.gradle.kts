/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2020  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
}
