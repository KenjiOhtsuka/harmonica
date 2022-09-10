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