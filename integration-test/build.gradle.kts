import org.gradle.api.attributes.java.TargetJvmVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

sourceSets {
    create("integrationTest")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":exposed"))

    testImplementation("com.improve_future:harmonica-demo:2.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${property("kotlin_version")}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${property("kotlin_version")}")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.3")
    testImplementation("org.xerial:sqlite-jdbc:3.45.3.0")

    add("integrationTestImplementation", project(":core"))
    add("integrationTestImplementation", project(":exposed"))
    add("integrationTestImplementation", "com.improve_future:harmonica-demo:2.0.0")
    add("integrationTestImplementation", sourceSets["test"].output)
    add("integrationTestImplementation", "org.jetbrains.kotlin:kotlin-test:${property("kotlin_version")}")
    add("integrationTestImplementation", "org.jetbrains.kotlin:kotlin-test-junit5:${property("kotlin_version")}")
    add("integrationTestImplementation", "org.junit.jupiter:junit-jupiter:6.1.3")
    add("integrationTestRuntimeOnly", "org.junit.platform:junit-platform-launcher:6.1.3")
    add("integrationTestImplementation", "org.postgresql:postgresql:42.7.13")
    add("integrationTestImplementation", "com.mysql:mysql-connector-j:26.7.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.test {
    // Always-green embedded tests (SQLite) run in every `build`.
    useJUnitPlatform()
}

val integrationTest = tasks.register<Test>("integrationTest") {
    group = "verification"
    description = "Runs the real-DB integration suite; skips when a database is unreachable"
    useJUnitPlatform()
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
}

// JUnit 6 requires Java 17+, but published bytecode must stay JVM 8
// (jvmTarget = JVM_1_8 above). Override the attribute on the test classpaths
// only; tests run on the installed JDK (25), which satisfies the 17+ baseline.
configurations {
    testCompileClasspath {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
    testRuntimeClasspath {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
    named("integrationTestCompileClasspath") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
    named("integrationTestRuntimeClasspath") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
        }
    }
}
