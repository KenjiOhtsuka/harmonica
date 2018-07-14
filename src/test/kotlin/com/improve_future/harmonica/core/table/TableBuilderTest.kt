package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.table.TableBuilder
import org.junit.Test
import kotlin.test.assertEquals

class TableBuilderTest {
    @Test
    fun testInteger() {
        val tb = TableBuilder()
        tb.integer("name", false, 1)
        val integerColumn = tb.columnList.first() as IntegerColumn
        assertEquals("name", integerColumn.name)
        assertEquals(false, integerColumn.nullable)
        assertEquals(1L, integerColumn.default)
    }
}