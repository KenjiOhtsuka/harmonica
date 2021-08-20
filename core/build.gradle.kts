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

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.cesarferreira:kotlin-pluralizer:0.2.9")

    // Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test:${property("kotlin_version")}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${property("kotlin_version")}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("commons-codec:commons-codec:1.15")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}
