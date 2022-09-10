/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.table.column

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TimestampColumnTest {
    @Test
    fun testInstanceVariable() {
        val timestampColumn = TimestampColumn("name")
        assertEquals("name", timestampColumn.name)
        assertEquals(false, timestampColumn.hasDefault)
        timestampColumn.default = "2018-02-13 11:12:13"
        assertEquals(true, timestampColumn.hasDefault)
        assertEquals("'2018-02-13T11:12:13'", timestampColumn.sqlDefault)
    }
}