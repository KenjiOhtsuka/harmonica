package com.improve_future.harmonica.document.view

object JarmonicaView : ViewInterface {
    override fun index(): String {
        return Template.default("Home") {

        }
    }
}