package com.improve_future.harmonica.core.table.column

import org.jetbrains.kotlin.daemon.common.toHexString
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