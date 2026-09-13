package com.improve_future.harmonica.core.table.column

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BigIntegerColumnTest {
    @Test
    fun testInstanceVariable() {
        val bigIntegerColumn = BigIntegerColumn("name")
        assertEquals("name", bigIntegerColumn.name)
        assertEquals(false, bigIntegerColumn.hasDefault)
        bigIntegerColumn.default = 1L
        assertEquals(true, bigIntegerColumn.hasDefault)
        assertEquals("1", bigIntegerColumn.sqlDefault)
    }
}