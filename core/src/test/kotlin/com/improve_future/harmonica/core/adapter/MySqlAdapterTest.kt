/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
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
    fun testAddColumnForBigInteger() {
        val connection = StubConnection()
        val adapter = MySqlAdapter(connection)

        val bigIntegerColumn = BigIntegerColumn("bigInteger")

        adapter.addColumn(
            "table_name", bigIntegerColumn,
            AddingColumnOption()
        )
        assertEquals(
            "ALTER TABLE table_name ADD COLUMN bigInteger BIGINT;",
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

    @Test
    fun testAddForeignKey() {
        val connection = StubConnection()
        val adapter = MySqlAdapter(connection)

        adapter.addForeignKey(
            "table_name", "column_name",
            "referenced_table_name", "referenced_column_name"
        )

        assertEquals(
            "ALTER TABLE table_name" +
                    " ADD CONSTRAINT table_name_column_name_fkey" +
                    " FOREIGN KEY (column_name)" +
                    " REFERENCES referenced_table_name (referenced_column_name);",
            connection.executedSqlList.first()
        )
    }

    @Test
    fun testCreateIndex() {
        // ToDo
    }
}