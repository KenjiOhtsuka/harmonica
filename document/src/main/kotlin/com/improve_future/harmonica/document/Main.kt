package com.improve_future.harmonica.document

import java.io.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.use

object Main {
    @JvmStatic
    fun main(vararg args: String) {
        val sitePath = Paths.get("..", "doc", "site").toRealPath()
        outputFile(Paths.get(sitePath.toString(), "test.html"), "test")
    }

    private fun outputFile(path: Path, text: String) {
        FileWriter(File(path.toString())).use { it.write(text) }
        println("Output " + path.toString() + ".")
    }
}