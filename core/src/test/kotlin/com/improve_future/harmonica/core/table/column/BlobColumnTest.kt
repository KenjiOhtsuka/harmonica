/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.toHexString
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BlobColumnTest {
    @Test
    fun testInstanceVariable() {
        val blobColumn = BlobColumn("name")
        assertEquals("name", blobColumn.name)
        assertEquals(null, blobColumn.default)
        assertEquals(false, blobColumn.hasDefault)
        blobColumn.default = byteArrayOf(1)
        assertEquals(
            byteArrayOf(1).toHexString(), blobColumn.default?.toHexString()
        )
        assertEquals(true, blobColumn.hasDefault)
        assertEquals("E'\\\\x01'", blobColumn.sqlDefault)
        blobColumn.default = null
        assertEquals(null, blobColumn.default)
        assertEquals(false, blobColumn.hasDefault)
    }
}
