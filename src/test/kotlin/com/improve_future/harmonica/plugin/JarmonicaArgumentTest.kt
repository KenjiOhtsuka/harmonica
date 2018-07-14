package com.improve_future.harmonica.plugin

import org.junit.Test
import kotlin.test.assertEquals

class JarmonicaArgumentTest {
    @Test
    fun testToList() {
        val arg = JarmonicaArgument()
        arg.migrationDirectory = "directory"
        arg.taskType = JarmonicaTaskType.Up
        arg.env = "env"
        arg.migrationPackage = "package"

        var expectedList = mutableListOf(
            "package", "Up", "directory", "env"
        )
        assertEquals(expectedList, arg.toList())

        arg.add("")
        expectedList.add("")
        assertEquals(expectedList, arg.toList())
    }
}