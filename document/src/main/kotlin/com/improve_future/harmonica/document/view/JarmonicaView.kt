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
                        +"Jarmonica is the legacy migration flow, in which migrations are "
                        code { +"compiled Kotlin classes" }
                        +" rather than scripts. The "
                        code { +"harmonica" }
                        +" plugin described on the "
                        a("harmonica.html") { +"Harmonica introduction" }
                        +" page is recommended for new projects."
                    }
                }
            }
            section {
                row { col { h2 { +"How to use" } } }
                section {
                    row { col { h3 { +"1. Apply the plugin" } } }
                    row {
                        col {
                            p { +"Add the plugin to your build script:" }
                            pre {
                                code {
                                    +"""
plugins {
    id("jarmonica") version "3.0.0"
    kotlin("jvm") version "2.3.20"
}
                                    """.trimIndent()
                                }
                            }
                            p {
                                +"Jarmonica relies on the Kotlin compiler to produce the migration "
                                +"classes, so it also needs a runtime classpath that carries the "
                                +"JDBC driver and the Harmonica library."
                            }
                            p {
                                +"The plugin registers the tasks "
                                code { +"jarmonicaCreate" }
                                +", "
                                code { +"jarmonicaUp" }
                                +", "
                                code { +"jarmonicaDown" }
                                +", and "
                                code { +"jarmonicaVersion" }
                                +"."
                            }
                        }
                    }
                }
                section {
                    row { col { h3 { +"2. Set the migration package" } } }
                    row {
                        col {
                            p {
                                +"Compiled migrations live in the package configured by the "
                                code { +"migrationPackage" }
                                +" extra property or, failing that, derived from "
                                code { +"directoryPath" }
                                +":"
                            }
                            pre {
                                code {
                                    +"""
extra["migrationPackage"] = "com.example.myapp.db"
extra["env"] = "Default"   // optional; defaults to "Default"
                                    """.trimIndent()
                                }
                            }
                        }
                    }
                }
                section {
                    row { col { h3 { +"3. Configure the database connection" } } }
                    row {
                        col {
                            p {
                                +"Configuration is a compiled "
                                code { +"DbConfig" }
                                +" subclass or object named after the environment, in the migration "
                                +"package. For each environment you use, create a "
                                code { +"config/" }
                                +" file such as "
                                code { +"config/Default.kt" }
                                +":"
                            }
                            pre {
                                code {
                                    +"""
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.Dbms

class Default : DbConfig() {
    init {
        dbms = Dbms.SQLite
        dbName = "example_app"
    }
}
                                    """.trimIndent()
                                }
                            }
                        }
                    }
                }
                section {
                    row { col { h3 { +"4. Write a migration" } } }
                    row {
                        col {
                            p {
                                +"Run "
                                code { +"./gradlew jarmonicaCreate -PmigrationName=CreateUsers" }
                                +" to scaffold one, or create the class manually. The class name "
                                +"must start with "
                                code { +"M" }
                                +" and end with "
                                code { +"_" }
                                +", with the "
                                code { +"yyyyMMddHHmmssSSS" }
                                +" timestamp in between — e.g. "
                                code { +"M<timestamp>_<Name>" }
                                +" — because the timestamp is the migration version:"
                            }
                            pre {
                                code {
                                    +"""
import com.improve_future.harmonica.core.AbstractMigration

class M20260830120000000_CreateUsers : AbstractMigration() {
    override fun up() {
        createTable("users") {
            varchar("name", size = 100, nullable = false)
        }
    }

    override fun down() {
        dropTable("users")
    }
}
                                    """.trimIndent()
                                }
                            }
                        }
                    }
                }
                section {
                    row { col { h3 { +"5. Run the migrations" } } }
                    row {
                        col {
                            pre {
                                code {
                                    +"""
./gradlew jarmonicaCreate -PmigrationName=CreateUsers  # scaffold a migration
./gradlew jarmonicaUp -Pstep=2                         # apply up to two steps
./gradlew jarmonicaDown -Pstep=1                       # revert one step
./gradlew jarmonicaVersion                             # show the current version
                                    """.trimIndent()
                                }
                            }
                            p {
                                +"Select a different environment with "
                                code { +"-Penv=test" }
                                +", matching the name of a "
                                code { +"DbConfig" }
                                +" you have defined."
                            }
                        }
                    }
                }
            }
        }
    }
}