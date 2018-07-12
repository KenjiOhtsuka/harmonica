package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.table.column.AbstractColumn
import com.improve_future.harmonica.core.table.column.BooleanColumn
import com.improve_future.harmonica.core.table.column.IntegerColumn
import com.improve_future.harmonica.core.table.column.VarcharColumn
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.asserter

class PostgreSqlAdapterTest {
    private val buildColumnDeclarationFunctionForTest =
            PostgreSqlAdapter.Companion::class.java.getDeclaredMethod(
                    "buildColumnDeclarationForCreateTableSql",
                    AbstractColumn::class.java).also { it.isAccessible = true }

    @Test
    fun testBuildColumnDeclarationForInteger() {
        val integerColumn = IntegerColumn("int")

        fun buildIntegerDeclaration() =
                buildColumnDeclarationFunctionForTest.invoke(
                        PostgreSqlAdapter, integerColumn)
        assertEquals(
                "int INTEGER",
                buildIntegerDeclaration())
        integerColumn.nullable = false
        assertEquals(
                "int INTEGER NOT NULL",
                buildIntegerDeclaration())
        integerColumn.default = 1
        assertEquals(
                "int INTEGER NOT NULL DEFAULT 1",
                buildIntegerDeclaration())
        integerColumn.nullable = true
        assertEquals(
                "int INTEGER DEFAULT 1",
                buildIntegerDeclaration())
    }

    @Test
    fun testBuildColumnDeclarationForVarchar() {
        val varcharColumn = VarcharColumn("varchar")
        fun buildVarcharDeclaration() =
                buildColumnDeclarationFunctionForTest.invoke(
                        PostgreSqlAdapter, varcharColumn)
        assertEquals(
                "varchar VARCHAR",
                buildVarcharDeclaration())
        varcharColumn.nullable = false
        assertEquals(
                "varchar VARCHAR NOT NULL",
                buildVarcharDeclaration())
        varcharColumn.default = "varchar"
        assertEquals(
                "varchar VARCHAR NOT NULL DEFAULT 'varchar'",
                buildVarcharDeclaration())
        varcharColumn.nullable = true
        assertEquals(
                "varchar VARCHAR DEFAULT 'varchar'",
                buildVarcharDeclaration())
    }

    @Test
    fun testBuildColumnDeclarationForBoolean() {
        val booleanColumn = BooleanColumn("boolean")
        fun buildBooleanDeclaration() =
                buildColumnDeclarationFunctionForTest.invoke(
                        PostgreSqlAdapter, booleanColumn)
        assertEquals(
                "boolean BOOL",
                buildBooleanDeclaration())
        booleanColumn.nullable = false
        assertEquals(
                "boolean BOOL NOT NULL",
                buildBooleanDeclaration())
        booleanColumn.default = true
        assertEquals(
                "boolean BOOL NOT NULL DEFAULT TRUE",
                buildBooleanDeclaration())
        booleanColumn.default = false
        booleanColumn.nullable = true
        assertEquals(
                "boolean BOOL DEFAULT FALSE",
                buildBooleanDeclaration())
    }
}