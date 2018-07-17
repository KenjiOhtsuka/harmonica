package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.helper.col
import com.improve_future.harmonica.document.helper.row
import kotlinx.html.h1
import kotlinx.html.p

object HarmonicaView : AbstractView() {
    override val title = "Harmonica introduction"
    override val pathKey = "harmonica"

    override fun index(): String {
        return Template.default(title) {
            row {
                col {
                    h1 { +title }
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