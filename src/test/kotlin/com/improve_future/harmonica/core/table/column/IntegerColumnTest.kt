package com.improve_future.harmonica.core.table.column

import org.junit.Test
import kotlin.test.assertEquals

class IntegerColumnTest {
    @Test
    fun testInstanceVariable() {
        val integerColumn = IntegerColumn("name")
        assertEquals("name", integerColumn.name)
        assertEquals(false, integerColumn.hasDefault)
        integerColumn.default = 1
        assertEquals(true, integerColumn.hasDefault)
    }
}