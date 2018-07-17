package com.improve_future.harmonica.core.table.column

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.asserter

class BooleanColumnTest {
    @Test
    fun testInstanceVariable() {
        val booleanColumn = BooleanColumn("name")
        assertEquals("name", booleanColumn.name)
        assertEquals(false, booleanColumn.hasDefault)
        booleanColumn.default = true
        assertEquals("TRUE", booleanColumn.sqlDefault)
        assertEquals(true, booleanColumn.hasDefault)
        booleanColumn.default = true
        assertEquals("FALSE", booleanColumn.sqlDefault)
        assertEquals(true, booleanColumn.hasDefault)
        booleanColumn.default = false
        assertEquals(false, booleanColumn.hasDefault)
    }
}