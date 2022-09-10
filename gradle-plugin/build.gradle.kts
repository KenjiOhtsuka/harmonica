/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

import com.jfrog.bintray.gradle.BintrayExtension.PackageConfig
import com.jfrog.bintray.gradle.BintrayExtension.VersionConfig
import org.gradle.api.publish.maven.MavenPom
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date
import java.nio.file.Paths

plugins {
    id("com.jfrog.bintray")
    id("maven-publish")
    id("java-library")
    id("com.gradle.plugin-publish")
    kotlin("jvm")
    id("java-gradle-plugin")
    //id("com.novoda.bintray-release")
    id("org.jetbrains.dokka")
    //jacoco
}

group = "com.improve_future"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed")
    maven("https://jitpack.io")
}

dependencies {
    val kotlinVersion = property("kotlin_version") as String

    api(project(":core"))
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-compiler-embeddable
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")

    /* JDBC */
    testImplementation("mysql:mysql-connector-java:5.1.44")
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
bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    pkg(closureOf<PackageConfig> {
        repo = "m"
        name = "Harmonica"
        userOrg = "kenjiohtsuka"
        setLicenses("GPL-3.0")
        vcsUrl = githubUrl

        version(closureOf<VersionConfig> {
            name = project.version as String
            released = Date().toString()
            vcsTag = project.version as String
        })
    })
}

// for Bintray (jcenter)
//publish {
//    userOrg = "kenjiohtsuka"
//    groupId = project.group
//    artifactId = "harmonica" // project.artifacts
//    publishVersion = project.version
//    desc = "Kotlin Database Migration Took"
//    website = "https://github.com/KenjiOhtsuka/harmonica"
//    //repoName = ""
//}

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
        register<MavenPublication>("MyPublication") {
            from(components.getByName("java"))
            artifact("sourcesJar")
            artifact("javadocJar")
            groupId = "com.improve_future"
            artifactId = "harmonica"
            version = project.version as String
            pomConfig(pom)
        }
        register<MavenPublication>("GradlePublication") {
            from(components.getByName("java"))
            artifact("sourcesJar")
            artifact("javadocJar")
            groupId = "com.improve_future.harmonica"
            artifactId = "gradle-plugin"
            version = project.version as String
            pomConfig(pom)
        }
    }
}
