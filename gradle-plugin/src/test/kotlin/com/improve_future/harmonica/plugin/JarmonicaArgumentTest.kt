/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.plugin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JarmonicaArgumentTest {
    @Test
    fun testToArray() {
        val arg = JarmonicaArgument()
        arg.migrationDirectory = "directory"
        arg.taskType = JarmonicaTaskType.Up
        arg.env = "env"
        arg.migrationPackage = "package"

        var expectedArray = arrayOf(
            "package", "Up", "directory", "env", "false", "false", "false"
        )
        var actualArray = arg.toArray()
        assertEquals(expectedArray.size, actualArray.size)
        for (i in 0 until expectedArray.size)
            assertEquals(expectedArray[i], actualArray[i])

        arg.dispSql = true
        expectedArray[5] = "true"
        actualArray = arg.toArray()
        assertEquals(expectedArray.size, actualArray.size)
        for (i in 0 until expectedArray.size)
            assertEquals(expectedArray[i], actualArray[i])

        arg.isReview = true
        expectedArray[6] = "true"
        actualArray = arg.toArray()
        assertEquals(expectedArray.size, actualArray.size)
        for (i in 0 until expectedArray.size)
            assertEquals(expectedArray[i], actualArray[i])

        arg.add("")
        expectedArray += arrayOf("")
        actualArray = arg.toArray()
        assertEquals(expectedArray.size, actualArray.size)
        for (i in 0 until expectedArray.size)
            assertEquals(expectedArray[i], actualArray[i])
    }

    @Test
    fun testParse() {
        val argument = JarmonicaArgument.parse(
            arrayOf(
                "package", "pass", "directory", "env", "true", "false", "true"
            )
        )
        assertEquals("package", argument.migrationPackage)
        assertEquals("directory", argument.migrationDirectory)
        assertEquals("env", argument.env)
        assertEquals(true, argument.tableNamePluralization)
        assertEquals(false, argument.dispSql)
        assertEquals(true, argument.isReview)
    }

    @Test
    fun testDefaultArgumentSize() {
        val argument = JarmonicaArgument.parse(
            arrayOf(
                "package", "pass", "directory", "env", "true", "false", "true"
            )
        )
        argument.taskType = JarmonicaTaskType.Up
        assertEquals(
            JarmonicaArgument.DEFAULT_ARGUMENT_SIZE, argument.toArray().size
        )
    }

    @Test
    fun testParseStepString() {
        assertEquals(
            5,
            JarmonicaArgument.parseStepString("5")
        )
        assertEquals(
            null,
            JarmonicaArgument.parseStepString("null")
        )
    }
}