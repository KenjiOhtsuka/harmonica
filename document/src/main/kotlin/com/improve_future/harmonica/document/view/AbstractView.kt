package com.improve_future.harmonica.document.view

abstract class AbstractView {
    abstract val articleTitle: String
    abstract val pathKey: String
    val path: String = "$pathKey.html"

    abstract fun index(): String
}