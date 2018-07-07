package com.improve_future.harmonica.document.helper

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.div

@HtmlTagMarker
fun FlowContent.row(
        classes : String? = null,
        block : DIV.() -> Unit = {}
) {
    return div(if (classes.isNullOrEmpty()) "row" else "$classes,row", block)
}