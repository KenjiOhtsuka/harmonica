package com.improve_future.harmonica.core.table.column

import org.junit.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals

class DateColumnTest {
    @Test
    fun testDateConversion() {
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
        val calendar = Calendar.getInstance()
        calendar.set(2345, 7, 19)
        val actualDate = Calendar.getInstance()
        actualDate.time = dateColumn.defaultDate
        arrayOf(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH).forEach {
            assertEquals(calendar.get(it), actualDate.get(it))
        }
        assertEquals(
            LocalDate.of(2345, 8, 19), dateColumn.defaultLocalDate
        )

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