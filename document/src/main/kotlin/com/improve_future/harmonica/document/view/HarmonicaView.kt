package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.col
import com.improve_future.harmonica.document.helper.row
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

object HarmonicaView : ViewInterface{
    override val path = "harmonica.html"

    override fun index(): String {
        return Template.default("Harmonica introduction") {
            row {
                col {
                    h1 { +"Harmonica" }
                }
            }
            row {
                col {
                    p { +"Jarmonica, migration with compiled Kotlin classes." }
                }
            }
        }
    }
}