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

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals

class DateColumnTest {
    @Test
    fun testInstanceVariable() {
        val dateColumn = DateColumn("name")
        assertEquals("name", dateColumn.name)

        // null default value check
        fun checkAllNull() {
            assertEquals(null, dateColumn.default)
            assertEquals(null, dateColumn.defaultDate)
            assertEquals(null, dateColumn.defaultLocalDate)
            assertEquals(false, dateColumn.hasDefault)
        }
        checkAllNull()
        dateColumn.defaultDate = null
        checkAllNull()
        dateColumn.defaultLocalDate = null
        checkAllNull()

        dateColumn.default = "2345-08-19"
        assertEquals(true, dateColumn.hasDefault)
        assertEquals("2345-08-19", dateColumn.default)
        val actualDate = Calendar.getInstance()
        actualDate.time = dateColumn.defaultDate
        assertEquals(2345, actualDate.get(Calendar.YEAR))
        assertEquals(8, actualDate.get(Calendar.MONTH) + 1)
        assertEquals(19, actualDate.get(Calendar.DAY_OF_MONTH))
        assertEquals(
            LocalDate.of(2345, 8, 19), dateColumn.defaultLocalDate
        )

        val calendar = Calendar.getInstance()
        val date = calendar.let {
            it.set(2543, 11, 23)
            it.time
        }
        dateColumn.defaultDate = date
        assertEquals(true, dateColumn.hasDefault)
        assertEquals("2543-12-23", dateColumn.default)
        assertEquals(date, dateColumn.defaultDate)
        assertEquals(LocalDate.of(2543, 12, 23), dateColumn.defaultLocalDate)

        val localDate = LocalDate.of(2435, 10, 6)
        dateColumn.defaultLocalDate = localDate
        assertEquals(true, dateColumn.hasDefault)
        assertEquals("2435-10-06", dateColumn.default)
        actualDate.time = dateColumn.defaultDate
        assertEquals(2435, actualDate.get(Calendar.YEAR))
        assertEquals(10, actualDate.get(Calendar.MONTH) + 1)
        assertEquals(6, actualDate.get(Calendar.DAY_OF_MONTH))
        assertEquals(localDate, dateColumn.defaultLocalDate)

        dateColumn.default = null
        checkAllNull()
    }
}