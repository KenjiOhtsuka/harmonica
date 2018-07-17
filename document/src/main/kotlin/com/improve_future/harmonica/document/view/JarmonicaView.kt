package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.col
import com.improve_future.harmonica.document.helper.row
import kotlinx.html.*

object JarmonicaView : ViewInterface {
    override val path = "jarmonica.html"

    override fun index(): String {
        return Template.default("Jarmonica introduction") {
            row {
                col {
                    h1 { +"Jarmonica" }
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