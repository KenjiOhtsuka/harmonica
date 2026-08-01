package com.improve_future.harmonica.core.table.column

import org.junit.jupiter.api.Test
import java.time.LocalTime
import java.util.*
import kotlin.test.assertEquals

class TimeColumnTest {
    @Test
    fun testInstanceVariable() {
        val timeColumn = TimeColumn("name")
        assertEquals("name", timeColumn.name)

        // null default value check
        fun checkAllNull() {
            assertEquals(null, timeColumn.default)
            assertEquals(null, timeColumn.defaultDate)
            assertEquals(null, timeColumn.defaultLocalTime)
            assertEquals(false, timeColumn.hasDefault)
        }
        checkAllNull()
        timeColumn.defaultDate = null
        checkAllNull()
        timeColumn.defaultLocalTime = null
        checkAllNull()

        timeColumn.default = "21:43:51.123"
        assertEquals(true, timeColumn.hasDefault)
        assertEquals("21:43:51.123", timeColumn.default)
        val actualDate = Calendar.getInstance()
        actualDate.time = timeColumn.defaultDate
        assertEquals(21, actualDate.get(Calendar.HOUR_OF_DAY))
        assertEquals(43, actualDate.get(Calendar.MINUTE))
        assertEquals(51, actualDate.get(Calendar.SECOND))
        assertEquals(123, actualDate.get(Calendar.MILLISECOND))
        assertEquals(
            LocalTime.of(21, 43, 51, 123000000),
            timeColumn.defaultLocalTime
        )

        val calendar = Calendar.getInstance()
        val date = calendar.let {
            it.set(Calendar.HOUR_OF_DAY, 12)
            it.set(Calendar.MINUTE, 34)
            it.set(Calendar.SECOND, 56)
            it.set(Calendar.MILLISECOND, 789)
            it.time
        }
        timeColumn.defaultDate = date
        assertEquals(true, timeColumn.hasDefault)
        assertEquals("12:34:56.789", timeColumn.default)
        assertEquals(date, timeColumn.defaultDate)
        assertEquals(
            LocalTime.of(12, 34, 56, 789000000),
            timeColumn.defaultLocalTime
        )

        val localTime = LocalTime.of(1, 2, 3, 100000004)
        timeColumn.defaultLocalTime = localTime
        assertEquals(true, timeColumn.hasDefault)
        assertEquals("01:02:03.100000004", timeColumn.default)
        actualDate.time = timeColumn.defaultDate
        assertEquals(1, actualDate.get(Calendar.HOUR_OF_DAY))
        assertEquals(2, actualDate.get(Calendar.MINUTE))
        assertEquals(3, actualDate.get(Calendar.SECOND))
        assertEquals(100, actualDate.get(Calendar.MILLISECOND))
        assertEquals(localTime, timeColumn.defaultLocalTime)

        timeColumn.default = null
        checkAllNull()
    }
}