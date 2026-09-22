import org.gradle.api.attributes.java.TargetJvmVersion
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("maven-publish")
    id("java-library")
    id("com.gradle.plugin-publish")
    kotlin("jvm")
    id("java-gradle-plugin")
    id("org.jetbrains.dokka")
}

// Plugin Portal ownership is verified via the owner's GitHub account, so the
// published coordinate lives under io.github.kenjiohtsuka; the historical
// com.improve_future group is not available for new publishes.
group = "io.github.kenjiohtsuka"
version = "4.0.0"

repositories {
    mavenCentral()
}

dependencies {
    val kotlinVersion = property("kotlin_version") as String

    implementation(project(":core"))

    /* Implementation */
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")

    // Dependencies to be able to run tests within gradle
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.3")
    testImplementation(gradleTestKit())
    testImplementation("org.xerial:sqlite-jdbc:3.45.3.0")

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

dokka {
    dokkaPublications.html {
        outputDirectory.set(rootProject.layout.projectDirectory.dir("docs/api/gradle-plugin"))
    }
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")
}

// Core is bundled into the jar (not a published dependency), so portal
// consumers apply the plugin without extra repositories.
tasks.jar {
    val coreMain =
        project(":core").the<SourceSetContainer>()["main"]
    from(coreMain.output)
}

tasks.register<Jar>("javadocJar") {
    from(tasks.named("javadoc"))
    archiveClassifier.set("javadoc")
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
            id = "io.github.kenjiohtsuka.harmonica"
            implementationClass = "com.improve_future.harmonica.plugin.HarmonicaPlugin"
            displayName = "DB Migration Plugin"
            description = "Kotlin Database Migration Tool"
            tags = listOf("kotlin", "database", "migration")
        }
        register("jarmonica") {
            id = "io.github.kenjiohtsuka.jarmonica"
            implementationClass = "com.improve_future.harmonica.plugin.JarmonicaPlugin"
            displayName = "DB Migration Plugin (legacy)"
            description = "Kotlin Database Migration Tool (legacy id)"
            tags = listOf("kotlin", "database", "migration")
        }
    }
}

val githubUrl = "https://github.com/KenjiOhtsuka/harmonica"

gradle.projectsEvaluated {
    publishing {
        publications {
            named<MavenPublication>("pluginMaven") {
                // POM-only publish: core ships in the jar, so no core metadata
                // is generated and the core dependency is stripped from the POM.
                tasks.named("generateMetadataFileForPluginMavenPublication") {
                    enabled = false
                }
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
                pom.withXml {
                    @Suppress("UNCHECKED_CAST")
                    val dependenciesNode =
                        (asNode().get("dependencies") as? List<groovy.util.Node>)?.firstOrNull()
                            ?: return@withXml
                    val coreDeps =
                        (dependenciesNode.get("dependency") as List<groovy.util.Node>)
                            .filter {
                                (it.get("groupId") as List<groovy.util.Node>).first().text() ==
                                    "com.improve_future" &&
                                    (it.get("artifactId") as List<groovy.util.Node>).first().text() ==
                                    "core"
                            }
                    coreDeps.forEach { (it.parent() as groovy.util.Node).remove(it) }
                }
            }
        }
    }
}

// OSSRH staging repo kept for a future Maven Central release (deferred);
// unused while the Plugin Portal is the only publishing channel.
publishing {
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
