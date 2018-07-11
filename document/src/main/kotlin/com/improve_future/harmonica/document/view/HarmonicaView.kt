package com.improve_future.harmonica.document.view

object HarmonicaView : ViewInterface{
    override val path = "harmonica.html"

    override fun index(): String {
        return Template.default("Home") {

        }
    }
}