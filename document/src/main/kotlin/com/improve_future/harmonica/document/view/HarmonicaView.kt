package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.col
import com.improve_future.harmonica.document.helper.row
import kotlinx.html.*

object HarmonicaView : AbstractView() {
    override val articleTitle = "Harmonica introduction"
    override val pathKey = "harmonica"

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
                        +"Harmonica migrates your database with Kotlin scripts ("
                        code { +".kts" }
                        +"). This page explains how to introduce Harmonica into your project."
                    }
                    p {
                        +"Version 3.0.0 is the up-coming maintenance-restart release of this page's "
                        +"content. The legacy, compiled-class "
                        a("jarmonica.html") { +"jarmonica" }
                        +" plugin is covered separately."
                    }
                }
            }
            section {
                row { col { h2 { +"Getting started" } } }
                section {
                    row { col { h3 { +"1. Apply the plugin" } } }
                    row {
                        col {
                            p { +"Add the plugin to your build script:" }
                            pre {
                                code {
                                    +"""
plugins {
    id("harmonica") version "3.0.0"
}
                                    """.trimIndent()
                                }
                            }
                            p {
                                +"The plugin registers the tasks "
                                code { +"harmonicaUp" }
                                +", "
                                code { +"harmonicaDown" }
                                +", and "
                                code { +"harmonicaCreate" }
                                +"."
                            }
                        }
                    }
                }
                section {
                    row { col { h3 { +"2. Point the plugin at the migration directory" } } }
                    row {
                        col {
                            p {
                                +"Set the "
                                code { +"directoryPath" }
                                +" extra property to the directory that contains "
                                code { +"migration/" }
                                +" and "
                                code { +"config/" }
                                +" (the default is "
                                code { +"src/main/kotlin/db" }
                                +"):"
                            }
                            pre {
                                code {
                                    +"""
extra["directoryPath"] = "src/main/kotlin/com/example/myapp"
                                    """.trimIndent()
                                }
                            }
                            p {
                                +"Add whatever the scripts need — the core library, your JDBC driver, "
                                +"and optionally the Exposed bridge — to the "
                                code { +"harmonica" }
                                +" configuration:"
                            }
                            pre {
                                code {
                                    +"""
dependencies {
    harmonica("com.improve_future:core:3.0.0")
    harmonica("org.xerial:sqlite-jdbc:3.45.3.0")
    //harmonica("com.improve_future:exposed:3.0.0")
}
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
                                +"Create "
                                code { +"config/<env>.kts" }
                                +" for each environment. The environment is selected by the "
                                code { +"env" }
                                +" extra property or the "
                                code { +"-Penv" }
                                +" project property and defaults to "
                                code { +"default" }
                                +". The file evaluates to a "
                                code { +"DbConfig" }
                                +":"
                            }
                            pre {
                                code {
                                    +"""
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.Dbms

DbConfig {
    dbms = Dbms.SQLite
    dbName = "example_app"
}
                                    """.trimIndent()
                                }
                            }
                            p {
                                +"Available settings: "
                                code { +"dbms" }
                                +", "
                                code { +"host" }
                                +", "
                                code { +"port" }
                                +", "
                                code { +"dbName" }
                                +", "
                                code { +"user" }
                                +", "
                                code { +"password" }
                                +", and "
                                code { +"sslmode" }
                                +". Executed migration versions are tracked in the "
                                code { +"harmonica_migration" }
                                +" table."
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
                                code { +"./gradlew harmonicaCreate -PmigrationName=CreateUsers" }
                                +" to scaffold a migration in "
                                code { +"migration/" }
                                +", or create one manually. Migration files are named "
                                code { +"<timestamp>_<Name>.kts" }
                                +" and run in lexical order:"
                            }
                            pre {
                                code {
                                    +"""
import com.improve_future.harmonica.core.AbstractMigration

object : AbstractMigration() {
    override fun up() {
        createTable("users") {
            varchar("name", size = 100, nullable = false)
            integer("age")
            boolean("active", default = true)
        }
        createIndex("users", "name")
        addTextColumn("users", "address")
    }

    override fun down() {
        dropTable("users")
    }
}
                                    """.trimIndent()
                                }
                            }
                            p {
                                +"The full set of available methods is listed on the "
                                a("../api/core/core/com.improve_future.harmonica.core/-abstract-migration/index.html") {
                                    +"AbstractMigration API page"
                                }
                                +" and the "
                                a("migration_method.html") { +"migration method reference" }
                                +"."
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
./gradlew harmonicaUp      # apply all pending migrations
./gradlew harmonicaDown    # revert the latest migration
                                    """.trimIndent()
                                }
                            }
                            p {
                                +"Select a different environment with "
                                code { +"-Penv=sqlite" }
                                +". Supported database systems are PostgreSQL, MySQL, SQLite, "
                                +"Oracle, and H2 (the SQL Server adapter is registered but not "
                                +"yet implemented)."
                            }
                        }
                    }
                }
            }
        }
    }
}