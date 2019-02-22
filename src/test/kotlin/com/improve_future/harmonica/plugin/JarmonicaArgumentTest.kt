/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Harmonica.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Harmonica.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.plugin

import org.junit.jupiter.api.Test
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