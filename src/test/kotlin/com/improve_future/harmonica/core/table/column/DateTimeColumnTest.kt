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
import kotlin.test.assertNull

class DateTimeColumnTest {
    @Test
    fun testInstanceVariable() {
        val dateTimeColumn = DateTimeColumn("name")
        assertEquals("name", dateTimeColumn.name)
        assertEquals(false, dateTimeColumn.hasDefault)
        dateTimeColumn.default = "2018-01-02 21:24:23.234"
        assertEquals(true, dateTimeColumn.hasDefault)
        assertEquals(2018, dateTimeColumn.defaultLocalDateTime?.year)
        assertEquals(1, dateTimeColumn.defaultLocalDateTime?.monthValue)
        assertEquals(2, dateTimeColumn.defaultLocalDateTime?.dayOfMonth)
        assertEquals(21, dateTimeColumn.defaultLocalDateTime?.hour)
        assertEquals(24, dateTimeColumn.defaultLocalDateTime?.minute)
        assertEquals(23, dateTimeColumn.defaultLocalDateTime?.second)
        assertEquals(234000000, dateTimeColumn.defaultLocalDateTime?.nano)
        dateTimeColumn.defaultLocalDateTime = null
        assertNull(dateTimeColumn.sqlDefault)
    }
}