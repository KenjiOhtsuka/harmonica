/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2020  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
