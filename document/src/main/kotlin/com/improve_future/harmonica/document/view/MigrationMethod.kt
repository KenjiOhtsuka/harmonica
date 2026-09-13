package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.col
import com.improve_future.harmonica.document.helper.row
import kotlinx.html.*

object MigrationMethod : AbstractView() {
    override val articleTitle = "Migration method"
    override val pathKey = "migration_method"

    override fun index(): String {
        val migrationApi =
            "../api/core/core/com.improve_future.harmonica.core/-abstract-migration/index.html"
        return Template.default(articleTitle) {
            row {
                col {
                    h1 { +articleTitle }
                }
            }
            row {
                col {
                    p {
                        +"A migration extends "
                        code { +"AbstractMigration" }
                        +" and implements "
                        code { +"up()" }
                        +" and "
                        code { +"down()" }
                        +". Inside those methods the whole DSL is available, and column builders "
                        +"inside "
                        code { +"createTable { }" }
                        +". The authoritative signatures are on the "
                        a(migrationApi) { +"AbstractMigration API page" }
                        +"."
                    }
                }
            }
            row {
                col {
                    h2 { +"Creating and dropping tables" }
                    ul {
                        li {
                            code { +"createTable(\"users\") { ... }" }
                            +" — create a table; the block declares its columns and indexes."
                        }
                        li {
                            code { +"dropTable(\"users\")" }
                            +" — drop a table."
                        }
                        li {
                            code { +"renameTable(\"members\", \"users\")" }
                            +" — rename a table."
                        }
                    }
                }
            }
            row {
                col {
                    h2 { +"Column builders (inside createTable)" }
                    ul {
                        li {
                            code { +"varchar(\"name\", size = 100)" }
                            +", "
                            code { +"string(...)" }
                            +" — a text column of a given size."
                        }
                        li {
                            code { +"integer(...)" }
                            +", "
                            code { +"bigInteger(...)" }
                            +", "
                            code { +"decimal(...)" }
                            +" — numeric columns."
                        }
                        li {
                            code { +"boolean(...)" }
                            +" — a boolean column."
                        }
                        li {
                            code { +"date(...)" }
                            +", "
                            code { +"time(...)" }
                            +", "
                            code { +"dateTime(...)" }
                            +", "
                            code { +"timestamp(...)" }
                            +" — temporal columns."
                        }
                        li {
                            code { +"text(...)" }
                            +" — a long text column."
                        }
                        li {
                            code { +"blob(...)" }
                            +" — a binary column."
                        }
                        li {
                            code { +"refer(\"company\")" }
                            +" — a foreign key column (e.g. "
                            code { +"company_id" }
                            +") referencing the given table; also accepts "
                            code { +"nullable" }
                            +", "
                            code { +"default" }
                            +", and "
                            code { +"columnName" }
                            +"."
                        }
                        li {
                            code { +"comment(\"...\")" }
                            +" — a column comment."
                        }
                        li {
                            +"Builders accept "
                            code { +"nullable" }
                            +" and "
                            code { +"default" }
                            +" (and "
                            code { +"size" }
                            +" for "
                            code { +"varchar" }
                            +" — MySQL defaults to 255 — plus "
                            code { +"unsigned" }
                            +" for integer columns on MySQL)."
                        }
                    }
                }
            }
            row {
                col {
                    h2 { +"Adding columns to an existing table" }
                    ul {
                        li {
                            code { +"addVarcharColumn" }
                            +", "
                            code { +"addIntegerColumn" }
                            +", "
                            code { +"addBigIntegerColumn" }
                            +", "
                            code { +"addDecimalColumn" }
                            +", "
                            code { +"addBooleanColumn" }
                            +", "
                            code { +"addTextColumn" }
                            +", "
                            code { +"addBlobColumn" }
                            +" — each takes the table and column name (and column settings)."
                        }
                        li {
                            code { +"addDateColumn" }
                            +", "
                            code { +"addDateTimeColumn" }
                            +", "
                            code { +"addTimeColumn" }
                            +", "
                            code { +"addTimestampColumn" }
                            +" — temporal variants."
                        }
                        li {
                            code { +"removeColumn(\"users\", \"address\")" }
                            +" — drop a column."
                        }
                        li {
                            code { +"renameColumn(\"users\", \"name\", \"username\")" }
                            +" — rename a column."
                        }
                    }
                }
            }
            row {
                col {
                    h2 { +"Indexes and foreign keys" }
                    ul {
                        li {
                            code { +"createIndex(\"users\", \"name\")" }
                            +" — create an index on one or more columns (unique and index-name "
                            +"variants exist)."
                        }
                        li {
                            code { +"dropIndex(tableName, indexName)" }
                            +" — drop an index."
                        }
                        li {
                            code { +"renameIndex(tableName, oldIndexName, newIndexName)" }
                            +" — rename an index."
                        }
                        li {
                            code { +"addForeignKey(\"users\", \"companyId\", \"companies\", \"id\")" }
                            +" — add a foreign key constraint."
                        }
                        li {
                            code { +"dropForeignKey(tableName, columnName)" }
                            +" — drop a foreign key."
                        }
                    }
                }
            }
            row {
                col {
                    h2 { +"Low-level SQL" }
                    p {
                        code { +"executeSql(\"INSERT INTO ...\")" }
                        +" runs raw SQL against the connected database."
                    }
                }
            }
            row {
                col {
                    h2 { +"Supported database systems" }
                    p {
                        +"The "
                        code { +"Dbms" }
                        +" enum covers "
                        code { +"PostgreSQL" }
                        +", "
                        code { +"MySQL" }
                        +", "
                        code { +"SQLite" }
                        +", "
                        code { +"SQLServer" }
                        +" (adapter registered, not yet implemented), "
                        code { +"Oracle" }
                        +", and "
                        code { +"H2" }
                        +"."
                    }
                }
            }
        }
    }
}