package com.improve_future.harmonica.document.view

import kotlinx.html.*
import kotlinx.html.stream.appendHTML

object Template {
    fun default(title: String = "Harmonica", block: BODY.() -> Unit): String {
        return buildString {
            appendln("<!DOCTYPE html>")
            appendHTML().html {
                head {
                    title(title)
                    link(
                            "https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css",
                            rel = "stylesheet") {
                        attributes["crossorigin"] = "anonymous"
                        attributes["integrity"] = "sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4"
                    }
                }
                body {
                    header {

                    }
                    block()
                    footer {

                    }
                }
            }
            appendln()
        }
    }
}