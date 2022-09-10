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
import kotlin.test.assertNull

class DateTimeColumnTest {
    @Test
    fun testInstanceVariable() {
        val dateTimeColumn = DateTimeColumn("name")
        assertEquals("name", dateTimeColumn.name)
        assertEquals(false, dateTimeColumn.hasDefault)
        dateTimeColumn.default = "2018-01-02 21:24:23.234"
        assertEquals(true, dateTimeColumn.hasDefault)
        assertEquals(2018, dateTimeColumn.defaultLocalDateTime?.year)
        assertEquals(1, dateTimeColumn.defaultLocalDateTime?.monthValue)
        assertEquals(2, dateTimeColumn.defaultLocalDateTime?.dayOfMonth)
        assertEquals(21, dateTimeColumn.defaultLocalDateTime?.hour)
        assertEquals(24, dateTimeColumn.defaultLocalDateTime?.minute)
        assertEquals(23, dateTimeColumn.defaultLocalDateTime?.second)
        assertEquals(234000000, dateTimeColumn.defaultLocalDateTime?.nano)
        dateTimeColumn.defaultLocalDateTime = null
        assertNull(dateTimeColumn.sqlDefault)
    }
}