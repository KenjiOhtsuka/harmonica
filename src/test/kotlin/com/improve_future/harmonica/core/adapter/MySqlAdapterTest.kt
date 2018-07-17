package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.table.column.AbstractColumn
import com.improve_future.harmonica.core.table.column.TextColumn
import org.junit.Test
import kotlin.test.assertEquals

class MySqlAdapterTest {
    private val buildColumnDeclarationFunctionForTest =
        MySqlAdapter.Companion::class.java.getDeclaredMethod(
            "buildColumnDeclarationForCreateTableSql",
            AbstractColumn::class.java
        ).also { it.isAccessible = true }

    @Test
    fun testBuildColumnDeclarationForText() {
        val textColumn = TextColumn("text")
        fun buildTextDeclaration() =
            buildColumnDeclarationFunctionForTest.invoke(
                MySqlAdapter, textColumn
            ) as String
        assertEquals(
            "text TEXT",
            buildTextDeclaration()
        )
        textColumn.nullable = false
        assertEquals(
            "text TEXT NOT NULL",
            buildTextDeclaration()
        )
        textColumn.default = "text text"
        assertEquals(
            "text TEXT NOT NULL",
            buildTextDeclaration()
        )
    }

    @Test
    fun testAddColumnForInteger() {
        // ToDo
    }
}