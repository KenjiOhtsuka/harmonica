package com.improve_future.harmonica.document.view

import com.improve_future.harmonica.document.Site
import com.improve_future.harmonica.document.helper.row
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

object Template {
    fun default(title: String = "Harmonica", block: DIV.() -> Unit): String {
        return buildString {
            appendln("<!DOCTYPE html>")
            appendHTML().html {
                head {
                    meta(name="charset", content="UTF-8")
                    meta(name="viewport", content="width=devise-width, initial-scale=1.0")
                    title(title)
                    link(
                        "https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css",
                        rel = "stylesheet"
                    ) {
                        attributes["crossorigin"] = "anonymous"
                        attributes["integrity"] =
                                "sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4"
                    }
//                    link(
//                            "https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0-rc.2/css/materialize.min.css",
//                            rel = "stylesheet"
//                    )
//                    script(src = "https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0-rc.2/js/materialize.min.js") {}
                }
                body {
                    header {
                    }
                    div("container") {
                        row {
                            // side bar
                            div("col-xl-1 col-lg-2 col-md-3 col-sm-3 hidden-xs-down") {
                                section {
                                    ul {
                                        Site.structure.forEach {
                                            val view = it as AbstractView
                                            li {
                                                a(it.path) { +it.articleTitle }
                                            }
                                        }
                                    }
                                }
                            }
                            // main content
                            div("col-xl-11 col-lg-10 col-md-9 col-sm-9 col-xs-12") {
                                block()
                            }
                        }
                    }
                    footer {

                    }
                }
            }
            appendln()
        }
    }
}