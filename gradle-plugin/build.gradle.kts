import org.gradle.api.attributes.java.TargetJvmVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("maven-publish")
    id("java-library")
    id("com.gradle.plugin-publish")
    kotlin("jvm")
    id("java-gradle-plugin")
    id("org.jetbrains.dokka")
}

group = "com.improve_future"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    val kotlinVersion = property("kotlin_version") as String

    api(project(":core"))
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-compiler-embeddable
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")

    /* Implementation */
    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:$kotlinVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")

    // Dependencies to be able to run tests within gradle
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.2")

    //implementation localGroovy()  // Groovy SDK
    compileOnly(gradleApi())
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    task<Jar>("sourcesJar") {
        from(sourceSets.main.get().allJava)
        archiveClassifier.set("sources")
    }

    task<Jar>("javadocJar") {
        from(javadoc)
        archiveClassifier.set("javadoc")
    }
}

// JUnit 6 requires Java 17+, but the plugin's published bytecode must stay
// JVM 8 (jvmTarget = 1.8). JUnit 6 artifacts declare org.gradle.jvm.version
// = 17 in their metadata while these configurations carry 8 from
// targetCompatibility, so resolution would reject them. Override the
// attribute on the test classpaths only; tests run on the installed JDK
// (25), which satisfies the 17+ baseline.
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

gradlePlugin {
    website = "https://github.com/KenjiOhtsuka/harmonica"
    vcsUrl = "https://github.com/KenjiOhtsuka/harmonica"
    plugins {
        register("harmonica") {
            id = "harmonica"
            implementationClass = "com.improve_future.harmonica.plugin.HarmonicaPlugin"
            displayName = "DB Migration Plugin"
            description = "Kotlin Database Migration Tool"
            tags = listOf("kotlin", "database", "migration")
        }
        register("jarmonica") {
            id = "jarmonica"
            implementationClass = "com.improve_future.harmonica.plugin.JarmonicaPlugin"
        }
    }
}

val githubUrl = "https://github.com/KenjiOhtsuka/harmonica"

// Create the publication with the pom configuration:
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("Harmonica")
                description.set("Kotlin Database Migration Tool")
                url.set(githubUrl)
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("kenjiohtsuka")
                        name.set("Kenji Otsuka")
                        email.set("kok.fdcm@gmail.com")
                    }
                }
                scm {
                    url.set(githubUrl)
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
