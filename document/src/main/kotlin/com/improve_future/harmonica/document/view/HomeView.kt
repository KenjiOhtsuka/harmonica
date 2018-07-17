package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.row
import kotlinx.html.*

object HomeView : AbstractView() {
    override val articleTitle = "Home"
    override val pathKey = "index"

    override fun index(): String {
        return Template.default(articleTitle) {
            row {
                div("col") {
                    h1 { +articleTitle }
                }
            }
            row {
                div("col") {
                    p {
                        +"Welcome to Harmonica!"
                    }
                    p {
                        +"Harmonica is Kotlin database migration library written in Kotlin using Exposed. Harmonica provides migration in two way."
                    }
                    ul {
                        li { +"Use Kotlin Script" }
                        li { +"Compile as Kotlin Application" }
                    }
                }
            }
        }
    }
}