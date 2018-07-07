package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.table.column.AbstractColumn
import com.improve_future.harmonica.core.table.column.IntegerColumn
import org.junit.Test
import kotlin.test.assertEquals

class PostgreSqlAdapterTest {
    @Test
    fun testBuildColumnDeclarationForCreateTableSql() {
        val buildColumnDeclarationForCreateTableSql =
                PostgreSqlAdapter.Companion::class.java.getDeclaredMethod(
                        "buildColumnDeclarationForCreateTableSql",
                        AbstractColumn::class.java)
        buildColumnDeclarationForCreateTableSql.isAccessible = true

        val integerColumn = IntegerColumn("int")
        integerColumn.nullable = false

        val expression =
                buildColumnDeclarationForCreateTableSql.invoke(
                        PostgreSqlAdapter, integerColumn)

        assertEquals(
                "  int INTEGER NOT NULL",
                expression)
    }
}