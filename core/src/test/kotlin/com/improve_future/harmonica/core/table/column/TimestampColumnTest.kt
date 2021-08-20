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

package com.improve_future.harmonica.core.table.column

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TimestampColumnTest {
    @Test
    fun testInstanceVariable() {
        val timestampColumn = TimestampColumn("name")
        assertEquals("name", timestampColumn.name)
        assertEquals(false, timestampColumn.hasDefault)
        timestampColumn.default = "2018-02-13 11:12:13"
        assertEquals(true, timestampColumn.hasDefault)
        assertEquals("'2018-02-13T11:12:13'", timestampColumn.sqlDefault)
    }
}