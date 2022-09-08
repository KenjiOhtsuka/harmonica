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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.assertEquals

class PostgreSqlAdapterTest {
    private val buildColumnDeclarationFunctionForTest =
        PostgreSqlAdapter.Companion::class.java.getDeclaredMethod(
            "buildColumnDeclarationForCreateTableSql",
            AbstractColumn::class.java
        ).also { it.isAccessible = true }

    @Test
    fun testBuildColumnDeclarationForInteger() {
        val integerColumn = IntegerColumn("int")

        fun buildIntegerDeclaration() =
            buildColumnDeclarationFunctionForTest.invoke(
                PostgreSqlAdapter, integerColumn
            ) as String
        assertEquals(
            "int INTEGER",
            buildIntegerDeclaration()
        )
        integerColumn.nullable = false
        assertEquals(
            "int INTEGER NOT NULL",
            buildIntegerDeclaration()
        )
        integerColumn.default = 1
        assertEquals(
            "int INTEGER NOT NULL DEFAULT 1",
            buildIntegerDeclaration()
        )
        integerColumn.nullable = true
        assertEquals(
            "int INTEGER DEFAULT 1",
            buildIntegerDeclaration()
        )
    }

    @Test
    fun testBuildColumnDeclarationForDecimal() {
        val decimalColumn = DecimalColumn("decimal")

        fun buildDecimalDeclaration() =
            buildColumnDeclarationFunctionForTest.invoke(
                PostgreSqlAdapter, decimalColumn
            ) as String
        assertEquals(
            "decimal DECIMAL",
            buildDecimalDeclaration()
        )
        decimalColumn.precision = 5
        assertEquals(
            "decimal DECIMAL(5)",
            buildDecimalDeclaration()
        )
        decimalColumn.scale = 3
        assertEquals(
            "decimal DECIMAL(5, 3)",
            buildDecimalDeclaration()
        )
        decimalColumn.nullable = false
        assertEquals(
            "decimal DECIMAL(5, 3) NOT NULL",
            buildDecimalDeclaration()
        )
        decimalColumn.default = 1.0
        assertEquals(
            "decimal DECIMAL(5, 3) NOT NULL DEFAULT 1.0",
            buildDecimalDeclaration()
        )
        decimalColumn.nullable = true
        assertEquals(
            "decimal DECIMAL(5, 3) DEFAULT 1.0",
            buildDecimalDeclaration()
        )
    }

    @Test
    fun testBuildColumnDeclarationForVarchar() {
        val varcharColumn = VarcharColumn("varchar")
        fun buildVarcharDeclaration() =
            buildColumnDeclarationFunctionForTest.invoke(
                PostgreSqlAdapter, varcharColumn
            ) as String
        assertEquals(
            "varchar VARCHAR",
            buildVarcharDeclaration()
        )
        varcharColumn.nullable = false
        assertEquals(
            "varchar VARCHAR NOT NULL",
            buildVarcharDeclaration()
        )
        varcharColumn.default = "varchar"
        assertEquals(
            "varchar VARCHAR NOT NULL DEFAULT 'varchar'",
            buildVarcharDeclaration()
        )
        varcharColumn.nullable = true
        assertEquals(
            "varchar VARCHAR DEFAULT 'varchar'",
            buildVarcharDeclaration()
        )
        varcharColumn.size = 10
        assertEquals(
            "varchar VARCHAR(10) DEFAULT 'varchar'",
            buildVarcharDeclaration()
        )
    }

    @Test
    fun testBuildColumnDeclarationForBoolean() {
        val booleanColumn = BooleanColumn("boolean")
        fun buildBooleanDeclaration() =
            buildColumnDeclarationFunctionForTest.invoke(
                PostgreSqlAdapter, booleanColumn
            ) as String
        assertEquals(
            "boolean BOOL",
            buildBooleanDeclaration()
        )
        booleanColumn.nullable = false
        assertEquals(
            "boolean BOOL NOT NULL",
            buildBooleanDeclaration()
        )
        booleanColumn.default = true
        assertEquals(
            "boolean BOOL NOT NULL DEFAULT TRUE",
            buildBooleanDeclaration()
        )
        booleanColumn.default = false
        booleanColumn.nullable = true
        assertEquals(
            "boolean BOOL DEFAULT FALSE",
            buildBooleanDeclaration()
        )
    }

    @Test
    fun testBuildColumnDeclarationForText() {
        val textColumn = TextColumn("text")
        fun buildTextDeclaration() =
            buildColumnDeclarationFunctionForTest.invoke(
                PostgreSqlAdapter, textColumn
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
        textColumn.nullable = true
        assertEquals(
            "text TEXT DEFAULT 'text text'",
            buildTextDeclaration()
        )
    }

    @Test
    fun testBuildColumnDeclarationForDate() {
        val dateColumn = DateColumn("date")
        fun buildDateDeclaration() =
            buildColumnDeclarationFunctionForTest.invoke(
                PostgreSqlAdapter, dateColumn
            ) as String
        assertEquals(
            "date DATE",
            buildDateDeclaration()
        )
        dateColumn.nullable = false
        assertEquals(
            "date DATE NOT NULL",
            buildDateDeclaration()
        )
        val defaultDate = Date()
        dateColumn.defaultDate = defaultDate
        val dateSql =
            SimpleDateFormat("yyyy-MM-dd").format(defaultDate)
        assertEquals(
            "date DATE NOT NULL DEFAULT '$dateSql'",
            buildDateDeclaration()
        )
        dateColumn.nullable = true
        assertEquals(
            "date DATE DEFAULT '$dateSql'",
            buildDateDeclaration()
        )
    }

    @Test
    fun testBuildColumnDeclarationForTime() {
        val timeColumn = TimeColumn("time")
        fun buildTimeDeclaration() =
            buildColumnDeclarationFunctionForTest.invoke(
                PostgreSqlAdapter, timeColumn
            ) as String
        assertEquals(
            "time TIME",
            buildTimeDeclaration()
        )
        timeColumn.nullable = false
        assertEquals(
            "time TIME NOT NULL",
            buildTimeDeclaration()
        )
        val defaultTime = Date()
        timeColumn.defaultDate = defaultTime
        val timeSql =
            SimpleDateFormat("HH:mm:ss.SSS").format(defaultTime)
        assertEquals(
            "time TIME NOT NULL DEFAULT '$timeSql'",
            buildTimeDeclaration()
        )
        timeColumn.nullable = true
        assertEquals(
            "time TIME DEFAULT '$timeSql'",
            buildTimeDeclaration()
        )
        timeColumn.withTimeZone = true
        assertEquals(
            "time TIME WITH TIME ZONE DEFAULT '$timeSql'",
            buildTimeDeclaration()
        )
    }

    @Test
    fun testAddColumnForInteger() {
        val connection = StubConnection()
        val adapter = PostgreSqlAdapter(connection)

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
    fun testAddBlobForBlob() {
        val connection = StubConnection()
        val adapter = PostgreSqlAdapter(connection)

        val blobColumn = BlobColumn("blob")

        adapter.addColumn(
            "table_name", blobColumn,
            AddingColumnOption()
        )
        assertEquals(
            "ALTER TABLE table_name ADD COLUMN blob BYTEA;",
            connection.executedSqlList.first()
        )
    }

    @Test
    fun testDropIndex() {
        val connection = StubConnection()
        val adapter = PostgreSqlAdapter(connection)

        adapter.dropIndex("table_name", "index_name")
        assertEquals(
            "DROP INDEX index_name;",
            connection.executedSqlList.first()
        )
    }

    @Test
    fun testRenameColumn() {
        val connection = StubConnection()
        val adapter: DbAdapter = PostgreSqlAdapter(connection)

        adapter.renameColumn(
            "table_name",
            "old_column_name",
            "new_column_name"
        )
        assertEquals(
            "ALTER TABLE table_name RENAME COLUMN old_column_name TO new_column_name",
            connection.executedSqlList.first()
        )
    }

    @Test
    fun testAddForeignKey() {
        val connection = StubConnection()
        val adapter: DbAdapter = PostgreSqlAdapter(connection)

        adapter.addForeignKey(
            "table_name",
            "column_name",
            "referenced_table_name",
            "referenced_column_name"
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