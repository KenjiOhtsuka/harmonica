package com.improve_future.harmonica.document.view

object HarmonicaView : ViewInterface{
    override fun index(): String {
        return Template.default("Home") {

        }
    }
}