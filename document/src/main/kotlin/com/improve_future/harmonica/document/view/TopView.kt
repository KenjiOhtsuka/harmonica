package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.row
import kotlinx.html.*

object TopView : ViewInterface {
    override val path = "index.html"

    override fun index(): String {
        return Template.default("Home") {
            row {
                div("col") {
                    h1 { "Home" }
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