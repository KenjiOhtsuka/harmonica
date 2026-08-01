/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

import org.gradle.api.publish.maven.MavenPom
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date
import java.nio.file.Paths

plugins {
    id("maven-publish")
    id("java-library")
    id("com.gradle.plugin-publish")
    kotlin("jvm")
    id("java-gradle-plugin")
    id("org.jetbrains.dokka")
    //jacoco
}

group = "com.improve_future"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    val kotlinVersion = property("kotlin_version") as String

    api(project(":core"))
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-compiler-embeddable
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")

    /* JDBC */
    testImplementation("mysql:mysql-connector-java:8.0.33")
    //testCompile ("mysql:mysql-connector-mxj:5.0.12")
    testImplementation("org.postgresql:postgresql:9.4.1212.jre6")
    //testCompile ("com.opentable.components:otj-pg-embedded:0.9.0")
    testImplementation("org.xerial:sqlite-jdbc:3.21.0.1")
    // testCompile("com.oracle:ojdbc6:12.1.0.1-atlassian-hosted")
    testImplementation("com.microsoft.sqlserver:mssql-jdbc:6.2.1.jre7")

    /* Implementation */
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-script-runtime
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-script-util
    implementation("org.jetbrains.kotlin:kotlin-script-util:$kotlinVersion")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("org.reflections:reflections:0.9.11")

    // Latest version of kotlinx-html
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Dependencies to be able to run tests within gradle
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")

    //implementation localGroovy()  // Groovy SDK
    compileOnly(gradleApi())
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    task<Jar>("sourcesJar") {
        from(sourceSets.main.get().allJava)
        archiveClassifier.set("sources")
    }

    task<Jar>("javadocJar") {
        from(javadoc)
        archiveClassifier.set("javadoc")
    }

    dokka {
        outputFormat = "html"
        outputDirectory = Paths.get("docs", "api").toString()
    }
}

gradlePlugin {
    plugins {
        register("harmonica") {
            id = "harmonica"
            implementationClass = "com.improve_future.harmonica.plugin.HarmonicaPlugin"
        }
        register("jarmonica") {
            id = "jarmonica"
            implementationClass = "com.improve_future.harmonica.plugin.JarmonicaPlugin"
        }
    }
}

// for Gradle Plugin
pluginBundle {
    website = "https://github.com/KenjiOhtsuka/harmonica"
    vcsUrl = "https://github.com/KenjiOhtsuka/harmonica"
    description = "Kotlin Database Migration Tool"
    tags = listOf("kotlin", "database", "migration")

    plugins {
        register("harmonica") {
            id = "com.improve_future.harmonica"
            displayName = "DB Migration Plugin"
        }
    }
}

val githubUrl = "https://github.com/KenjiOhtsuka/harmonica"

val pomConfig: MavenPom.() -> Unit = {
    description.set("Kotlin Database Migration Tool")
    name.set("Harmonica")
    url.set(githubUrl)

    licenses {
        license {
            name.set("MIT License")
            url.set("https://www.gnu.org/licenses/gpl-3.0")
            distribution.set("repo")
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
                url.set("https://github.com/KenjiOhtsuka/harmonica")
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
                    url.set("https://github.com/KenjiOhtsuka/harmonica")
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