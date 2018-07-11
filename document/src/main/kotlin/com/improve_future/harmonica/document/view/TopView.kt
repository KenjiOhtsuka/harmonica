package com.improve_future.harmonica.document.view

object TopView : ViewInterface {
    override val path = "index.html"

    override fun index(): String {
        return Template.default("Home") {

        }
    }
}