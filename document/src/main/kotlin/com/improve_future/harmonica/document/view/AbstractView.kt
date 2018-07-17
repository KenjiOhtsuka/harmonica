package com.improve_future.harmonica.document.view

abstract class AbstractView {
    abstract val title: String
    abstract val pathKey: String
    val path: String = "$pathKey.html"

    abstract fun index(): String
}