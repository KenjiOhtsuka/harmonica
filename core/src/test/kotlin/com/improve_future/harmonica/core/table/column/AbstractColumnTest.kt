/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
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