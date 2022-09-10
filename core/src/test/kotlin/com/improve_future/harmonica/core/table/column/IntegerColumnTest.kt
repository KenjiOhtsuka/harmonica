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

class IntegerColumnTest {
    @Test
    fun testInstanceVariable() {
        val integerColumn = IntegerColumn("name")
        assertEquals("name", integerColumn.name)
        assertEquals(false, integerColumn.hasDefault)
        integerColumn.default = 1
        assertEquals(true, integerColumn.hasDefault)
        assertEquals("1", integerColumn.sqlDefault)
    }
}