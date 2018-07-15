package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.table.TableBuilder
import org.junit.Test
import java.util.*
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

    @Test
    fun testVarchar() {
        val tb = TableBuilder()
        tb.varchar("name", 10, false, "test string")
        val varcharColumn = tb.columnList.first() as VarcharColumn
        assertEquals("name", varcharColumn.name)
        assertEquals(10, varcharColumn.size)
        assertEquals(false, varcharColumn.nullable)
        assertEquals("test string", varcharColumn.default)
    }

    @Test
    fun testDate() {
        val tb = TableBuilder()
        val defaultDate = Date()
        tb.date("name", false, defaultDate)
        val dateColumn = tb.columnList.first() as DateColumn
        assertEquals("name", dateColumn.name)
        assertEquals(false, dateColumn.nullable)
        assertEquals(defaultDate, dateColumn.default)
    }
}