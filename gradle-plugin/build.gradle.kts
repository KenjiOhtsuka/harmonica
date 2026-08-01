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

    /* JDBC */
    testImplementation("mysql:mysql-connector-java:5.1.44")
    //testCompile ("mysql:mysql-connector-mxj:5.0.12")
    testImplementation("org.postgresql:postgresql:9.4.1212.jre6")
    //testCompile ("com.opentable.components:otj-pg-embedded:0.9.0")
    testImplementation("org.xerial:sqlite-jdbc:3.21.0.1")
    // testCompile("com.oracle:ojdbc6:12.1.0.1-atlassian-hosted")
    testImplementation("com.microsoft.sqlserver:mssql-jdbc:6.2.1.jre7")

    /* Implementation */
    implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223:$kotlinVersion")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("org.reflections:reflections:0.9.11")

    // Latest version of kotlinx-html
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")

    // Dependencies to be able to run tests within gradle
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")

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

gradlePlugin {
    plugins {
        register("harmonica") {
            id = "harmonica"
            implementationClass = "com.improve_future.harmonica.plugin.HarmonicaPlugin"
            displayName = "DB Migration Plugin"
            description = "Kotlin Database Migration Tool"
            tags = listOf("kotlin", "database", "migration")
            website = "https://github.com/KenjiOhtsuka/harmonica"
            vcsUrl = "https://github.com/KenjiOhtsuka/harmonica"
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
