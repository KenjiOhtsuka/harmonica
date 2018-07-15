package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.table.column.TextColumn
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
            first = true
        )
        val textAddingColumn =
            migration.adapter.addingColumnList.first()
        assertEquals(
            "table_name",
            textAddingColumn.tableName
        )
        val textColumn = textAddingColumn.column as TextColumn
        assertEquals(
            "column_name",
            textColumn.name
        )
        assertEquals(false, textColumn.nullable)
        val addingOption = textAddingColumn.option
        assertEquals(true, addingOption.first)
    }
}