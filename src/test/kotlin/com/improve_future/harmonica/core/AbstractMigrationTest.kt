package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.table.column.DecimalColumn
import com.improve_future.harmonica.core.table.column.TextColumn
import com.improve_future.harmonica.core.table.column.VarcharColumn
import org.junit.Test
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
        val textAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals(
            "table_name",
            textAddingColumn.tableName
        )
        val textColumn = textAddingColumn.column as TextColumn
        assertEquals("column_name", textColumn.name)
        assertEquals(false, textColumn.nullable)
        assertEquals("default value", textColumn.default)
        val addingOption = textAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)
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
        val decimalAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals("table_name", decimalAddingColumn.tableName)
        val decimalColumn = decimalAddingColumn.column as DecimalColumn
        assertEquals("column_name", decimalColumn.name)
        assertEquals(5, decimalColumn.precision)
        assertEquals(2, decimalColumn.scale)
        assertEquals(false, decimalColumn.nullable)
        assertEquals(5.3, decimalColumn.default)
        val addingOption = decimalAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)
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
        val varcharAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals("table_name", varcharAddingColumn.tableName)
        val varcharColumn = varcharAddingColumn.column as VarcharColumn
        assertEquals("column_name", varcharColumn.name)
        assertEquals(10, varcharColumn.size)
        assertEquals(false, varcharColumn.nullable)
        assertEquals("default value", varcharColumn.default)
        val addingOption = varcharAddingColumn.option
        assertEquals(true, addingOption.first)
        assertEquals("previous_column", addingOption.justBeforeColumn)
    }
}