/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.table.column.*
import com.improve_future.harmonica.stub.core.StubConnection
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class H2AdapterTest {
    private val buildColumnDeclarationFunctionForTest =
            H2Adapter.Companion::class.java.getDeclaredMethod(
                    "buildColumnDeclarationForCreateTableSql",
                    AbstractColumn::class.java
            ).also { it.isAccessible = true }

    @Test
    fun testBuildColumnDeclarationForText() {
        val textColumn = TextColumn("text")
        fun buildTextDeclaration() =
                buildColumnDeclarationFunctionForTest.invoke(
                        H2Adapter, textColumn
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
                "text TEXT NOT NULL DEFAULT 'text text'",
                buildTextDeclaration()
        )
    }

    @Test
    fun testAddColumnForInteger() {
        val connection = StubConnection()
        val adapter = H2Adapter(connection)

        val integerColumn = IntegerColumn("integer")

        adapter.addColumn(
                "table_name", integerColumn,
                AddingColumnOption()
        )
        assertEquals(
                "ALTER TABLE table_name\n  ADD integer INTEGER;",
                connection.executedSqlList.first()
        )
    }

    @Test
    fun testAddColumnForBigInteger() {
        val connection = StubConnection()
        val adapter = H2Adapter(connection)

        val bigIntegerColumn = BigIntegerColumn("bigInteger")

        adapter.addColumn(
                "table_name", bigIntegerColumn,
                AddingColumnOption()
        )
        assertEquals(
                "ALTER TABLE table_name\n  ADD bigInteger BIGINT;",
                connection.executedSqlList.first()
        )
    }

    @Test
    fun testAddColumnForBlob() {
        val connection = StubConnection()
        val adapter = H2Adapter(connection)

        val blobColumn = BlobColumn("blob")

        adapter.addColumn(
                "table_name", blobColumn,
                AddingColumnOption()
        )
        assertEquals(
                "ALTER TABLE table_name\n  ADD blob BLOB;",
                connection.executedSqlList.first()
        )
    }

    @Test
    fun testAddForeignKey() {
        val connection = StubConnection()
        val adapter = H2Adapter(connection)

        adapter.addForeignKey(
                "table_name", "column_name",
                "referenced_table_name", "referenced_column_name"
        )

        assertEquals(
                "ALTER TABLE table_name\n" +
                        "  ADD CONSTRAINT table_name_column_name_fkey\n" +
                        "    FOREIGN KEY (column_name)" +
                        " REFERENCES referenced_table_name (referenced_column_name);",
                connection.executedSqlList.first()
        )
    }

    @Test
    fun testCreateIndex() {
        val connection = StubConnection()
        val adapter = H2Adapter(connection)

        adapter.createIndex(
                "table_name",
                arrayOf("column1", "column2"),
                true,
                null
        )
        assertEquals(
                "CREATE UNIQUE INDEX table_name_column1_column2_index\n  ON table_name (column1,column2);",
                connection.executedSqlList.first()
        )
    }
}
