package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.table.column.*
import com.improve_future.harmonica.stub.core.StubConnection
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
        val connection = StubConnection()
        val adapter = MySqlAdapter(connection)

        val integerColumn = IntegerColumn("integer")

        adapter.addColumn(
            "table_name", integerColumn,
            AddingColumnOption()
        )
        assertEquals(
            "ALTER TABLE table_name ADD COLUMN integer INTEGER;",
            connection.executedSqlList.first()
        )
    }

    @Test
    fun testAddColumnForBlob() {
        val connection = StubConnection()
        val adapter = MySqlAdapter(connection)

        val blobColumn = BlobColumn("blob")

        adapter.addColumn(
            "table_name", blobColumn,
            AddingColumnOption()
        )
        assertEquals(
            "ALTER TABLE table_name ADD COLUMN blob BLOB;",
            connection.executedSqlList.first()
        )
    }
}