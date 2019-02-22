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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AbstractColumnTest {
    @Test
    fun testHasReference() {
        val column = object : AbstractColumn("column") {
            override var sqlDefault: String? = null
        }

        assertEquals(false, column.hasReference)
        column.referenceTable = " "
        assertEquals(false, column.hasReference)
        column.referenceColumn = " "
        assertEquals(false, column.hasReference)
        column.referenceTable = "reference_table"
        column.referenceColumn = "reference_column"
        assertEquals(true, column.hasReference)

        assertFalse(column.hasDefault)
        column.sqlDefault = ""
        assertTrue(column.hasDefault)
        column.sqlDefault = null
        assertFalse(column.hasDefault)
    }
}