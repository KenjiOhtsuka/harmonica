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
                            }
                        }
                        row {
                            col {
                                pre {
                                    code {
                                        +"""

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
                            }
                        }
                        row {
                            col {
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
                            }
                        }
                    }
                }
                section {
                    row {
                        col {
                            h3 { +"Migrate" }
                        }
                    }
                    section {
                        row {
                            col {
                                h4 { +"Create migration file" }
                            }
                        }
                        row {
                            col {

                            }
                        }
                    }
                    section {
                        row {
                            col {
                                h4 { +"Migrate" }
                            }
                        }
                        row {
                            col {

                            }
                        }
                    }
                    section {
                        row {
                            col {
                                h4 { +"Revert the migration" }
                            }
                        }
                        row {
                            col {

                            }
                        }
                    }
                }
            }
        }
    }
}