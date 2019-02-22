package com.improve_future.harmonica.core.table.column

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TextColumnTest {
    @Test
    fun testInstanceVariable() {
        val textColumn = TextColumn("name")
        assertEquals("name", textColumn.name)
        assertEquals(false, textColumn.hasDefault)
        textColumn.default = "text"
        assertEquals(true, textColumn.hasDefault)
        assertEquals("'text'", textColumn.sqlDefault)
    }
}