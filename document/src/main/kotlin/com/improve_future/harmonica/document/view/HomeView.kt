package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.row
import com.improve_future.harmonica.document.helper.col
import kotlinx.html.*

object HomeView : AbstractView() {
    override val articleTitle = "Home"
    override val pathKey = "index"

    override fun index(): String {
        return Template.default(articleTitle) {
            row {
                col {
                    h1 { +articleTitle }
                }
            }
            row {
                col {
                    p { +"Welcome to Harmonica!" }
                    p {
                        +"Harmonica is a database migration tool for the JVM written in Kotlin: "
                        +"a Gradle plugin backed by a JDBC core library, similar in spirit to "
                        +"Phinx and Rails migrations."
                    }
                    p { +"It provides migration in two ways:" }
                    ul {
                        li {
                            a("harmonica.html") { +"Migrate with Kotlin scripts (.kts)" }
                            +" — the recommended flow."
                        }
                        li {
                            a("jarmonica.html") { +"Migrate with compiled Kotlin classes" }
                            +" — the legacy jarmonica flow."
                        }
                    }
                    p {
                        +"See also the "
                        a("../api/index.html") { +"API documentation" }
                        +" and the "
                        a("migration_method.html") { +"migration method reference" }
                        +"."
                    }
                }
            }
        }
    }
}