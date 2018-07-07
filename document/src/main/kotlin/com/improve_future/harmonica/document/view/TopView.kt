package com.improve_future.harmonica.document.view

object TopView : ViewInterface {
    override fun index(): String {
        return Template.default("Home") {

        }
    }
}