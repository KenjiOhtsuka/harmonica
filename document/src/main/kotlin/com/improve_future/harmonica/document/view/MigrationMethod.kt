package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.col
import com.improve_future.harmonica.document.helper.row
import kotlinx.html.*

object MigrationMethod : AbstractView() {
    override val articleTitle = "Migration method"
    override val pathKey = "migration_method"

    override fun index(): String {
        return Template.default(articleTitle) {
            row {
                col {
                    h1 { +articleTitle }
                }
            }
            row {
                col {

                }
            }
            section {
                row {
                    col {
                        h2 {
                            +"Create migration file"
                        }
                    }
                }
                row {
                    col {
                        p {
                            +"First, create migration files."
                        }
                        pre {
                            code {
                                +"./gradlew jarmonicaCreate"
                            }
                        }
                    }
                }
                section {
                    row {
                        col {
                            h3 { +"Parameters" }
                        }
                    }
                    row {
                        col {
                            dl {
                                dt { +"migrationName" }
                                dd {
                                    p {
                                        +"This parameter "
                                        code { +"migrationName" }
                                        +"specifies migration name."
                                    }
                                    figure {
                                        figcaption { +"Usage" }
                                        pre {
                                            code {
                                                +"./gradlew jarmonicaCreate -PmigrationName=\"HelloWorld\""
                                            }
                                        }
                                    }
                                    figure {
                                        figcaption { +"Output" }
                                        pre {
                                            code {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            section {
                row { col { h2 { +"Migrate" } } }
                row {
                    col {
                        p {
                            +"Migrate by the following command."
                        }
                        pre {
                            code {
                                +"./gradlew jarmonicaUp"
                            }
                        }
                        p {
                            +"This command migrates all migrations"
                            +"which are not migrated at that time."
                        }
                    }
                }
                section {
                    row { col { h3 { +"Parameter" } } }
                    row {
                        col {
                            dl {
                                dt { +"env" }
                                dd {

                                }
                                dt { +"step" }
                                dd {

                                }
                            }
                        }
                    }
                }
            }
            section {
                row {
                    col {
                        h2 { +"Table creation" }
                    }
                }
                row {
                    col {
                        p {
                            +"For"
                        }
                        pre {
                            col {

                            }
                        }
                    }
                }
            }
        }
    }
}