import org.gradle.api.attributes.java.TargetJvmVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("maven-publish")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

dependencies {
    // Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test:${property("kotlin_version")}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${property("kotlin_version")}")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.3")
    testImplementation("com.h2database:h2:2.4.240")
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
    useJUnitPlatform()
}

// JUnit 6 requires Java 17+, but published bytecode must stay JVM 8
// (jvmTarget = JVM_1_8 above). The Gradle metadata of JUnit 6 artifacts
// declares org.gradle.jvm.version = 17, while these configurations carry
// 8 from targetCompatibility, which would reject the dependency. Override
// the attribute on the test classpaths only so resolution succeeds and
// tests run on the installed JDK (25), which satisfies the 17+ baseline.
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
}
