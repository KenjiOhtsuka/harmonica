/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.table.TableBuilder
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.test.assertEquals

class TableBuilderTest {
    @Test
    fun testInteger() {
        var tb = TableBuilder()
        tb.integer("name")
        var integerColumn = tb.columnList.first() as IntegerColumn
        assertEquals("name", integerColumn.name)
        assertEquals(true, integerColumn.nullable)
        assertEquals(null, integerColumn.default)

        tb = TableBuilder()
        tb.integer("name", false, 1)
        integerColumn = tb.columnList.first() as IntegerColumn
        assertEquals("name", integerColumn.name)
        assertEquals(false, integerColumn.nullable)
        assertEquals(1L, integerColumn.default)
    }

    @Test
    fun testBigInteger() {
        var tb = TableBuilder()
        tb.bigInteger("name")
        var bigInteger = tb.columnList.first() as BigIntegerColumn
        assertEquals("name", bigInteger.name)
        assertEquals(true, bigInteger.nullable)
        assertEquals(null, bigInteger.default)

        tb = TableBuilder()
        tb.bigInteger("name", false, 1L)
        bigInteger = tb.columnList.first() as BigIntegerColumn
        assertEquals("name", bigInteger.name)
        assertEquals(false, bigInteger.nullable)
        assertEquals(1L, bigInteger.default)
    }

    @Test
    fun testDecimal() {
        var tb = TableBuilder()
        tb.decimal("name")
        var decimalColumn = tb.columnList.first() as DecimalColumn
        assertEquals("name", decimalColumn.name)
        assertEquals(null, decimalColumn.precision)
        assertEquals(null, decimalColumn.scale)
        assertEquals(true, decimalColumn.nullable)
        assertEquals(null, decimalColumn.default)

        tb = TableBuilder()
        tb.decimal(
            "name", 10, 5,
            false, 1.1
        )
        decimalColumn = tb.columnList.first() as DecimalColumn
        assertEquals("name", decimalColumn.name)
        assertEquals(10, decimalColumn.precision)
        assertEquals(5, decimalColumn.scale)
        assertEquals(false, decimalColumn.nullable)
        assertEquals(1.1, decimalColumn.default)
    }

    @Test
    fun testVarchar() {
        var tb = TableBuilder()
        tb.varchar("name")
        var varcharColumn = tb.columnList.first() as VarcharColumn
        assertEquals("name", varcharColumn.name)
        assertEquals(null, varcharColumn.size)
        assertEquals(true, varcharColumn.nullable)
        assertEquals(null, varcharColumn.default)

        tb = TableBuilder()
        tb.varchar("name", 10, false, "test string")
        varcharColumn = tb.columnList.first() as VarcharColumn
        assertEquals("name", varcharColumn.name)
        assertEquals(10, varcharColumn.size)
        assertEquals(false, varcharColumn.nullable)
        assertEquals("test string", varcharColumn.default)
    }

    @Test
    fun testDate() {
        var tb = TableBuilder()
        tb.date("name")
        var dateColumn = tb.columnList.first() as DateColumn
        assertEquals("name", dateColumn.name)
        assertEquals(true, dateColumn.nullable)
        assertEquals(null, dateColumn.default)

        tb = TableBuilder()
        val defaultDate = Date()
        tb.date("name", false, defaultDate)
        dateColumn = tb.columnList.first() as DateColumn
        assertEquals("name", dateColumn.name)
        assertEquals(false, dateColumn.nullable)
        assertEquals(defaultDate, dateColumn.defaultDate)
    }

    @Test
    fun testBoolean() {
        var tb = TableBuilder()
        tb.boolean("name")
        var booleanColumn = tb.columnList.first() as BooleanColumn
        assertEquals("name", booleanColumn.name)
        assertEquals(true, booleanColumn.nullable)
        assertEquals(null, booleanColumn.default)

        tb = TableBuilder()
        val defaultBoolean = true
        tb.boolean("name", false, defaultBoolean)
        booleanColumn = tb.columnList.first() as BooleanColumn
        assertEquals("name", booleanColumn.name)
        assertEquals(false, booleanColumn.nullable)
        assertEquals(defaultBoolean, booleanColumn.default)
        assertEquals(true, booleanColumn.hasDefault)
    }

    @Test
    fun testTimestamp() {
        var tb = TableBuilder()
        tb.timestamp("name")
        var timestampColumn = tb.columnList.first() as TimestampColumn
        assertEquals("name", timestampColumn.name)
        assertEquals(true, timestampColumn.nullable)
        assertEquals(null, timestampColumn.default)
        assertEquals(false, timestampColumn.withTimeZone)

        tb = TableBuilder()
        val defaultDate = Date()
        tb.timestamp("name", default = defaultDate)
        timestampColumn = tb.columnList.first() as TimestampColumn
        assertEquals("name", timestampColumn.name)
        assertEquals(true, timestampColumn.nullable)
        assertEquals(defaultDate, timestampColumn.defaultDate)
        assertEquals(false, timestampColumn.withTimeZone)

        tb = TableBuilder()
        var defaultLocalDateTime = LocalDateTime.now()
        tb.timestamp("name", default = defaultLocalDateTime)
        timestampColumn = tb.columnList.first() as TimestampColumn
        assertEquals("name", timestampColumn.name)
        assertEquals(true, timestampColumn.nullable)
        assertEquals(defaultLocalDateTime, timestampColumn.defaultLocalDateTime)
        assertEquals(false, timestampColumn.withTimeZone)

        tb = TableBuilder()
        defaultLocalDateTime = LocalDateTime.now()
        tb.timestamp(
            "name", false, defaultLocalDateTime,
            true
        )
        timestampColumn = tb.columnList.first() as TimestampColumn
        assertEquals("name", timestampColumn.name)
        assertEquals(false, timestampColumn.nullable)
        assertEquals(defaultLocalDateTime, timestampColumn.defaultLocalDateTime)
        assertEquals(true, timestampColumn.withTimeZone)
    }

    @Test
    fun testDateTime() {
        var tb = TableBuilder()
        tb.dateTime("name")
        var dateTimeColumn = tb.columnList.first() as DateTimeColumn
        assertEquals("name", dateTimeColumn.name)
        assertEquals(true, dateTimeColumn.nullable)
        assertEquals(null, dateTimeColumn.default)

        tb = TableBuilder()
        val defaultDate = Date()
        tb.dateTime("name", default = defaultDate)
        dateTimeColumn = tb.columnList.first() as DateTimeColumn
        assertEquals("name", dateTimeColumn.name)
        assertEquals(true, dateTimeColumn.nullable)
        assertEquals(defaultDate, dateTimeColumn.defaultDate)

        tb = TableBuilder()
        var defaultLocalDateTime = LocalDateTime.now()
        tb.dateTime("name", default = defaultLocalDateTime)
        dateTimeColumn = tb.columnList.first() as DateTimeColumn
        assertEquals("name", dateTimeColumn.name)
        assertEquals(true, dateTimeColumn.nullable)
        assertEquals(defaultLocalDateTime, dateTimeColumn.defaultLocalDateTime)

        tb = TableBuilder()
        defaultLocalDateTime = LocalDateTime.now()
        tb.dateTime("name", false, defaultLocalDateTime)
        dateTimeColumn = tb.columnList.first() as DateTimeColumn
        assertEquals("name", dateTimeColumn.name)
        assertEquals(false, dateTimeColumn.nullable)
        assertEquals(defaultLocalDateTime, dateTimeColumn.defaultLocalDateTime)
    }

    @Test
    fun testTime() {
        var tb = TableBuilder()
        tb.time("name")
        var timeColumn = tb.columnList.first() as TimeColumn
        assertEquals("name", timeColumn.name)
        assertEquals(true, timeColumn.nullable)
        assertEquals(null, timeColumn.default)
        assertEquals(false, timeColumn.withTimeZone)

        tb = TableBuilder()
        val defaultTime = LocalTime.now()
        tb.time("name", false, defaultTime, true)
        timeColumn = tb.columnList.first() as TimeColumn
        assertEquals("name", timeColumn.name)
        assertEquals(false, timeColumn.nullable)
        assertEquals(defaultTime, timeColumn.defaultLocalTime)
        assertEquals(true, timeColumn.withTimeZone)
    }

    @Test
    fun testBlob() {
        var tb = TableBuilder()
        tb.blob("name")
        var blobColumn = tb.columnList.first() as BlobColumn
        assertEquals("name", blobColumn.name)
        assertEquals(true, blobColumn.nullable)
        assertEquals(null, blobColumn.default)

        tb = TableBuilder()
        val defaultBlob = byteArrayOf(1, 2, 3, 4, 5)
        tb.blob("name", false, defaultBlob)
        blobColumn = tb.columnList.first() as BlobColumn
        assertEquals("name", blobColumn.name)
        assertEquals(false, blobColumn.nullable)
        assertEquals(defaultBlob, blobColumn.default)
    }

    @Test
    fun testRefer() {
        var tb = TableBuilder()
        tb.refer("users")
        var column = tb.columnList.first()
        assertEquals("users_id", column.name)

        tb = TableBuilder()
        tb.tableNameIsInPluralForm = true
        tb.refer("users")
        column = tb.columnList.first()
        assertEquals("user_id", column.name)
    }
}