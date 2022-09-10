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

class BooleanColumnTest {
    @Test
    fun testInstanceVariable() {
        val booleanColumn = BooleanColumn("name")
        assertEquals("name", booleanColumn.name)
        assertEquals(false, booleanColumn.hasDefault)
        booleanColumn.default = true
        assertEquals("TRUE", booleanColumn.sqlDefault)
        assertEquals(true, booleanColumn.hasDefault)
        booleanColumn.default = false
        assertEquals("FALSE", booleanColumn.sqlDefault)
        assertEquals(true, booleanColumn.hasDefault)
        booleanColumn.default = null
        assertEquals(false, booleanColumn.hasDefault)
    }
}