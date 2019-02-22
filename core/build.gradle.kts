import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion by extra { "1.3.20" }

    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        // for release
        //classpath("com.novoda:bintray-release:$bintray_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
        //classpath group: 'org.jetbrains.kotlin', name: 'kotlin-script-runtime', version: kotlin_version
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.17")
    }
}

plugins {
    kotlin("jvm") //version "1.3.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    val kotlinVersion = extra["kotlinVersion"] as String
    compileOnly("org.jetbrains.exposed:exposed:0.10.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-compiler-embeddable
    implementation(
        group = "org.jetbrains.kotlin",
        name = "kotlin-compiler-embeddable",
        version = kotlinVersion
    )

    /* JDBC */
    testImplementation("mysql:mysql-connector-java:5.1.44")
    //testCompile "mysql:mysql-connector-mxj:5.0.12"
    testImplementation("org.postgresql:postgresql:9.4.1212.jre6")
    //testCompile 'com.opentable.components:otj-pg-embedded:0.9.0'
    testImplementation("org.xerial:sqlite-jdbc:3.21.0.1")
//    testCompile 'com.oracle:ojdbc6:12.1.0.1-atlassian-hosted'
    testImplementation(
        group = "com.microsoft.sqlserver",
        name = "mssql-jdbc",
        version = "6.2.1.jre7"
    )
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-script-runtime
    implementation(
        group = "org.jetbrains.kotlin",
        name = "kotlin-script-runtime",
        version = kotlinVersion
    )
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-script-util
    implementation(
        group = "org.jetbrains.kotlin",
        name = "kotlin-script-util",
        version = kotlinVersion
    )
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation(
        group = "org.jetbrains.kotlin",
        name = "kotlin-reflect",
        version = kotlinVersion
    )

    implementation(
        group = "org.reflections",
        name = "reflections",
        version = "0.9.11"
    )

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    implementation(localGroovy())  // Groovy SDK
    compileOnly(gradleApi())
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    //useJunitJupiter
}

//def pomConfig = {
//    licenses {
//        license {
//            name "GNU General Public License, Version 3.0"
//            url "https://www.gnu.org/licenses/gpl-3.0"
//            distribution "repo"
//        }
//    }
//    developers {
//        developer {
//            id "kenjiohtsuka"
//            name "Kenji Otsuka"
//            email "kok.fdcm@gmail.com"
//        }
//    }
//
//    scm {
//        url githubUrl
//    }
//}
//
//// Create the publication with the pom configuration:
//publishing {
//    publications {
//        MyPublication(MavenPublication) {
//            from components.java
//                    artifact sourcesJar
//                    artifact javadocJar
//                    groupId 'com.improve_future'
//            artifactId "harmonica"
//            version project.version
//                    pom.withXml {
//                        def root = asNode()
//                        root.appendNode('description', 'Kotlin Database Migration Tool')
//                        root.appendNode('name', 'Harmonica')
//                        root.appendNode('url', githubUrl)
//                        root.children().last() + pomConfig
//                    }
//        }
//    }
//}