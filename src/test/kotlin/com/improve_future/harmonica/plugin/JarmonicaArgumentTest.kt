/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
            "package", "Up", "directory", "env", "false"
        )
        var actualArray = arg.toArray()
        assertEquals(expectedArray.size, actualArray.size)
        for (i in 0 until expectedArray.size)
            assertEquals(expectedArray[i], actualArray[i])

        arg.add("")
        expectedArray += arrayOf("")
        actualArray = arg.toArray()
        for (i in 0 until expectedArray.size)
            assertEquals(expectedArray[i], actualArray[i])
    }

    @Test
    fun testParse() {
        var argument = JarmonicaArgument.parse(
            arrayOf("package", "pass", "directory", "env", "true")
        )
        assertEquals("package", argument.migrationPackage)
        assertEquals("directory", argument.migrationDirectory)
        assertEquals("env", argument.env)
        assertEquals(true, argument.tableNamePluralization)
    }
}