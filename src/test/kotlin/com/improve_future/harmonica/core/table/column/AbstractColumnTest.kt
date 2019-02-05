package com.improve_future.harmonica.core.table.column

import kotlin.test.Test
import kotlin.test.assertEquals

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
    }
}