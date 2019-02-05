package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.adapter.StubDbAdapter
import com.improve_future.harmonica.core.table.column.*
import org.jetbrains.kotlin.daemon.common.toHexString
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.test.assertEquals

class AbstractMigrationTest {
    @Test
    fun testAddTextColumn() {
        val migration = StubMigration()
        migration.addTextColumn(
            "table_name",
            "column_name",
            nullable = false,
            default = "default value",
            first = true,
            justBeforeColumnName = "previous_column"
        )
        var textAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals(
            "table_name",
            textAddingColumn.tableName
        )
        var textColumn = textAddingColumn.column as TextColumn
        assertEquals("column_name", textColumn.name)
        assertEquals(false, textColumn.nullable)
        assertEquals("default value", textColumn.default)
        val addingOption = textAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)

        val rawSql = RawSql("'A' || 'B'")
        migration.addTextColumn(
            "table_name",
            "column_name",
            default = rawSql
        )
        textAddingColumn =
            migration.adapter.addingColumnList.last()
        textColumn = textAddingColumn.column as TextColumn
        assertEquals(rawSql.sql, textColumn.sqlDefault)
    }

    @Test
    fun testAddDecimalColumn() {
        val migration = StubMigration()
        migration.addDecimalColumn(
            "table_name",
            "column_name",
            precision = 5,
            scale = 2,
            nullable = false,
            default = 5.3,
            first = true,
            justBeforeColumnName = "previous_column"
        )
        var decimalAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals("table_name", decimalAddingColumn.tableName)
        var decimalColumn = decimalAddingColumn.column as DecimalColumn
        assertEquals("column_name", decimalColumn.name)
        assertEquals(5, decimalColumn.precision)
        assertEquals(2, decimalColumn.scale)
        assertEquals(false, decimalColumn.nullable)
        assertEquals(5.3, decimalColumn.default)
        val addingOption = decimalAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)

        val rawSql = RawSql("1 + 2.0")
        migration.addDecimalColumn(
            "table_name",
            "column_name",
            default = rawSql
        )
        decimalAddingColumn =
            migration.adapter.addingColumnList.last()
        decimalColumn = decimalAddingColumn.column as DecimalColumn
        assertEquals(rawSql.sql, decimalColumn.sqlDefault)
    }

    @Test
    fun testAddVarcharColumn() {
        val migration = StubMigration()
        migration.addVarcharColumn(
            "table_name",
            "column_name",
            size = 10,
            nullable = false,
            default = "default value",
            first = true,
            justBeforeColumnName = "previous_column"
        )
        var varcharAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals("table_name", varcharAddingColumn.tableName)
        var varcharColumn = varcharAddingColumn.column as VarcharColumn
        assertEquals("column_name", varcharColumn.name)
        assertEquals(10, varcharColumn.size)
        assertEquals(false, varcharColumn.nullable)
        assertEquals("default value", varcharColumn.default)
        val addingOption = varcharAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)

        val rawSql = RawSql("'A' || 'B'")
        migration.addVarcharColumn(
            "table_name",
            "column_name",
            default = rawSql
        )
        varcharAddingColumn =
            migration.adapter.addingColumnList.last()
        varcharColumn = varcharAddingColumn.column as VarcharColumn
        assertEquals(rawSql.sql, varcharColumn.sqlDefault)
    }

    @Test
    fun testAddDateColumn() {
        val migration = StubMigration()
        val defaultDate = Date()
        migration.addDateColumn(
            "table_name",
            "column_name",
            nullable = false,
            default = defaultDate,
            first = true,
            justBeforeColumnName = "previous_column"
        )
        var dateAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals("table_name", dateAddingColumn.tableName)
        var dateColumn = dateAddingColumn.column as DateColumn
        assertEquals("column_name", dateColumn.name)
        assertEquals(false, dateColumn.nullable)
        assertEquals(defaultDate, dateColumn.defaultDate)
        var addingOption = dateAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)

        val rawSql = RawSql("CURRENT_TIMESTAMP")
        migration.addDateColumn(
            "table_name",
            "column_name",
            nullable = true,
            default = rawSql,
            first = false
        )
        dateAddingColumn =
            migration.adapter.addingColumnList.last()
        assertEquals("table_name", dateAddingColumn.tableName)
        dateColumn = dateAddingColumn.column as DateColumn
        assertEquals("column_name", dateColumn.name)
        assertEquals(true, dateColumn.nullable)
        assertEquals(rawSql.sql, dateColumn.sqlDefault)
        addingOption = dateAddingColumn.option
        assertEquals(false, addingOption.first)
        assertEquals(null, addingOption.justBeforeColumn)

    }

    @Test
    fun testAddBooleanColumn() {
        val migration = StubMigration()
        val defaultBoolean = false
        migration.addBooleanColumn(
            "table_name",
            "column_name",
            nullable = false,
            default = defaultBoolean,
            first = true,
            justBeforeColumnName = "previous_column"
        )
        var booleanAddingColumn = migration.adapter.addingColumnList.first()
        assertEquals("table_name", booleanAddingColumn.tableName)
        var booleanColumn = booleanAddingColumn.column as BooleanColumn
        assertEquals("column_name", booleanColumn.name)
        assertEquals(false, booleanColumn.nullable)
        assertEquals(defaultBoolean, booleanColumn.default)
        val addingOption = booleanAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)

        val rawSql = RawSql("TRUE AND FALSE")
        migration.addBooleanColumn(
            "table_name",
            "column_name",
            default = rawSql
        )
        booleanAddingColumn = migration.adapter.addingColumnList.last()
        booleanColumn = booleanAddingColumn.column as BooleanColumn
        assertEquals(rawSql.sql, booleanColumn.sqlDefault)
    }

    @Test
    fun testAddTimeColumn() {
        val migration = StubMigration()
        val defaultLocalTime = LocalTime.of(
            1, 2, 3, 100000004
        )
        migration.addTimeColumn(
            "table_name",
            "column_name",
            nullable = false,
            default = defaultLocalTime,
            withTimeZone = true,
            first = true,
            justBeforeColumnName = "previous_column"
        )
        var timeAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals("table_name", timeAddingColumn.tableName)
        var timeColumn = timeAddingColumn.column as TimeColumn
        assertEquals("column_name", timeColumn.name)
        assertEquals(false, timeColumn.nullable)
        assertEquals(defaultLocalTime, timeColumn.defaultLocalTime)
        assertEquals(true, timeColumn.withTimeZone)
        val addingOption = timeAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)

        val rawSql = RawSql("CURRENT_TIMESTAMP")
        migration.addTimeColumn(
            "table_name",
            "column_name",
            default = rawSql
        )
        timeAddingColumn = migration.adapter.addingColumnList.last()
        timeColumn = timeAddingColumn.column as TimeColumn
        assertEquals(rawSql.sql, timeColumn.sqlDefault)
    }

    @Test
    fun testAddDateTimeColumn() {
        val migration = StubMigration()
        val defaultLocalDateTime = LocalDateTime.now()
        migration.addDateTimeColumn(
            "table_name",
            "column_name",
            nullable = false,
            default = defaultLocalDateTime,
            first = true,
            justBeforeColumnName = "previous_column"
        )
        val dateTimeAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals(
            "table_name",
            dateTimeAddingColumn.tableName
        )
        val dateTimeColumn = dateTimeAddingColumn.column as DateTimeColumn
        assertEquals("column_name", dateTimeColumn.name)
        assertEquals(false, dateTimeColumn.nullable)
        assertEquals(
            defaultLocalDateTime.toString(),
            dateTimeColumn.default
        )
        val addingOption = dateTimeAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals(
            "previous_column",
            addingOption.justBeforeColumn
        )    // ToDo
    }

    @Test
    fun testAddTimestampColumn() {
        val migration = StubMigration()
        val defaultLocalDateTime = LocalDateTime.now()
        migration.addTimestampColumn(
            "table_name",
            "column_name",
            nullable = false,
            default = defaultLocalDateTime,
            withTimeZone = true,
            first = true,
            justBeforeColumnName = "previous_column"
        )
        val timestampAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals(
            "table_name",
            timestampAddingColumn.tableName
        )
        val timestampColumn = timestampAddingColumn.column as TimestampColumn
        assertEquals("column_name", timestampColumn.name)
        assertEquals(false, timestampColumn.nullable)
        assertEquals(true, timestampColumn.withTimeZone)
        assertEquals(
            defaultLocalDateTime.toString(),
            timestampColumn.default
        )
        val addingOption = timestampAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)
    }

    @Test
    fun testAddBlobColumn() {
        val migration = StubMigration()
        migration.addBlobColumn(
            "table_name",
            "column_name",
            nullable = false,
            default = "abcdefg".toByteArray(),
            first = true,
            justBeforeColumnName = "previous_column"
        )
        val blobAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals(
            "table_name",
            blobAddingColumn.tableName
        )
        val blobColumn = blobAddingColumn.column as BlobColumn
        assertEquals("column_name", blobColumn.name)
        assertEquals(false, blobColumn.nullable)
        assertEquals(
            "abcdefg".toByteArray().toHexString(),
            blobColumn.default?.toHexString()
        )
        val addingOption = blobAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)
    }

    @Test
    fun testAddForeignKey() {
        val migration = StubMigration()
        migration.addForeignKey(
            "table_name",
            "column_name",
            "referenced_table_name"
        )

        var addingForeignKey = migration.adapter.addingForeignKeyList.last()
        assertEquals("table_name", addingForeignKey.tableName)
        assertEquals("column_name", addingForeignKey.columnName)
        assertEquals(
            "referenced_table_name",
            addingForeignKey.referencedTableName
        )
        assertEquals("id", addingForeignKey.referencedColumnName)

        migration.addForeignKey(
            "table_name",
            "column_name",
            "referenced_table_name",
            "referenced_column_name"
        )

        addingForeignKey = migration.adapter.addingForeignKeyList.last()
        assertEquals("table_name", addingForeignKey.tableName)
        assertEquals("column_name", addingForeignKey.columnName)
        assertEquals(
            "referenced_table_name",
            addingForeignKey.referencedTableName
        )
        assertEquals(
            "referenced_column_name",
            addingForeignKey.referencedColumnName
        )
    }
}