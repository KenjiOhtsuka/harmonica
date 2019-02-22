/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Harmonica.  If not, see <http://www.gnu.org/licenses/>.
 */

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