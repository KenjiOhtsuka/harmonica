/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.cesarferreira:kotlin-pluralizer:1.0.0")

    // Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test:${property("kotlin_version")}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${property("kotlin_version")}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("commons-codec:commons-codec:1.15")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

tasks.test {
    useJUnitPlatform()
}
