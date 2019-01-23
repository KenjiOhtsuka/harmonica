package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.col
import com.improve_future.harmonica.document.helper.row
import kotlinx.html.*

object JarmonicaView : AbstractView() {
    override val articleTitle = "Jarmonica introduction"
    override val pathKey = "jarmonica"

    override fun index(): String {
        return Template.default(articleTitle) {
            row {
                col {
                    h1 { +articleTitle }
                }
            }
            row {
                col {
                    p {
                        +"Jarmonica, migration with compiled Kotlin classes."
                        +"Here, I will explain how to introduce Jarmonica into your project."
                    }
                }
            }
            section {
                row {
                    col {
                        h2 { +"How to use" }
                    }
                    col {
                        p { +"I will explain how to use Jarmonica step by step." }
                    }
                }
                section {
                    row {
                        col {
                            h3 { +"Preparation" }
                        }
                    }
                    section {
                        row {
                            col {
                                h4 {
                                    +"build.gradle"
                                }
                                p {
                                    +"Write "
                                    code { +"build.gradle" }
                                    +" as follows."
                                }

                                pre {
                                    code {
                                        +"""
buildscript {
    ext.kotlin_version = '1.2.51'
    ext.harmonica_version = '1.1.10'

    repositories {
        jcenter()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:${'$'}kotlin_version"
        // required for plugin
        classpath "com.github.KenjiOhtsuka:harmonica:harmonica_version"
    }
}

group 'com.improve_future'
version ext.harmonica_version

apply plugin: 'kotlin'
// required for gradle commands
apply plugin: 'jarmonica'

// Default value is src/main/kotlin/db
extensions.extraProperties["directoryPath"] =
        "src/main/kotlin/com/improve_future/harmonica_test/jarmonica"
// Default value is db
extensions.extraProperties["migrationPackage"] =
        "com.improve_future.harmonica_test.jarmonica"

sourceCompatibility = 1.8

repositories {
    mavenCentral()
    // required
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${'$'}kotlin
_version"
    // required
    compile group: 'org.reflections', name: 'reflections', version: '0.9.11'
    compile group: 'org.jetbrains.kotlin', name: 'kotlin-script-runtime', version: kotlin_version
    compile group: 'org.jetbrains.kotlin', name: 'kotlin-script-util', version: kotlin_version
    compile group: 'org.jetbrains.kotlin', name: 'kotlin-reflect', version: kotlin_version

    // JDBC Driver (SQLite, MySQL or PostgreSQL), prepare for your DBMS
    compile files('/lib/postgresql-42.2.2.jar')
    compile files('/lib/mysql-connector-java-8.0.11.jar')

    //required
    compile "com.github.KenjiOhtsuka:harmonica:${'$'}harmonica_version"
}
                                            """.trimIndent()
                                    }
                                }
                            }
                        }
                    }
                    section {
                        row {
                            col {
                                h4 {
                                    +"Directory and file structure"
                                }
                                p {
                                    +("Directory and file structure must be as follows." +
                                            " You must create at least one configuration class" +
                                            " There are 2 configuration files, ")
                                    code { +"Default.kt" }
                                    +" and "
                                    code { +"Custom.kt" }
                                    +"."
                                }
                                pre {
                                    code {
                                        +"""
                                            `- src
                                              `- main
                                                `- kotlin
                                                  `- db
                                                    |- config
                                                    | |- Default.kt <- default configuration file
                                                    | `- Custom.kt <- you can create own configuration file
                                                    `- migration
                                        """.trimIndent()
                                    }
                                }
                                p {
                                    +""
                                }
                            }
                        }
                    }
                }
                row {
                    col {
                        section {
                            h3 { +"Migrate" }
                            section {
                                h4 { +"Create migration file" }
                                pre {
                                    code {
                                        +"./gradlew jarmonicaCreate"
                                    }
                                }
                                section {
                                    h5 { +"Option" }
                                    dl {
                                        dt { +"env" }
                                        dd {}
                                        dt { +"migrationName" }
                                        dd {
                                            +"Specify migration name."
                                        }
                                    }
                                }
                            }
                            section {
                                h4 { +"Run migrations" }
                                pre {
                                    code {
                                        "./gradlew jarmonicaUp"
                                    }
                                }
                                section {
                                    h5 { +"Option" }
                                    dl {
                                        dt { +"env" }
                                        dd {
                                            +"Specify config object."
                                        }
                                    }
                                }
                            }
                            section {
                                h4 { +"Revert migrations" }
                                pre {
                                    code {
                                        "./gradlew jarmonicaDown"
                                    }
                                }
                                section {
                                    h5 { +"Option" }
                                    dl {
                                        dt { +"env" }
                                        dd {
                                            +"Specify config object."
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}