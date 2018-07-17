package com.improve_future.harmonica.document

import com.improve_future.harmonica.document.view.*
import java.io.*
import java.nio.file.Paths
import kotlin.io.use

object Main {
    private val sitePath = Paths.get("..", "docs", "site").toRealPath()!!

    @JvmStatic
    fun main(vararg args: String) {
        arrayOf(HomeView, JarmonicaView, HarmonicaView, MigrationMethod).forEach {
            outputFile(it)
        }
    }

    private fun outputFile(view: AbstractView) {
        val file = File(
            Paths.get(sitePath.toString(), view.pathKey).toString() + ".html"
        )
        FileWriter(file).use { it.write(view.index()) }
        println("Output " + view.pathKey + ".")
    }
}