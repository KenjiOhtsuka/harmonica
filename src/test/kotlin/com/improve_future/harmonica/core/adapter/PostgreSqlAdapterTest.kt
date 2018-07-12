package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.table.column.*
import org.junit.Test
import kotlin.test.assertEquals

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
    fun testBuildColumnDeclarationForDecimal() {
        val decimalColumn = DecimalColumn("decimal")

        fun buildIntegerDeclaration() =
                buildColumnDeclarationFunctionForTest.invoke(
                        PostgreSqlAdapter, decimalColumn)
        assertEquals(
                "decimal DECIMAL",
                buildIntegerDeclaration())
        decimalColumn.nullable = false
        assertEquals(
                "decimal DECIMAL NOT NULL",
                buildIntegerDeclaration())
        decimalColumn.default = 1.0
        assertEquals(
                "decimal DECIMAL NOT NULL DEFAULT 1.0",
                buildIntegerDeclaration())
        decimalColumn.nullable = true
        assertEquals(
                "decimal DECIMAL DEFAULT 1.0",
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

    @Test
    fun testBuildColumnDeclarationForText() {
        val textColumn = TextColumn("text")
        fun buildTextDeclaration() =
                buildColumnDeclarationFunctionForTest.invoke(
                        PostgreSqlAdapter, textColumn)
        assertEquals(
                "text TEXT",
                buildTextDeclaration())
        textColumn.nullable = false
        assertEquals(
                "text TEXT NOT NULL",
                buildTextDeclaration())
        textColumn.default = "text text"
        assertEquals(
                "text TEXT NOT NULL DEFAULT 'text text'",
                buildTextDeclaration())
        textColumn.nullable = true
        assertEquals(
                "text TEXT DEFAULT 'text text'",
                buildTextDeclaration())
    }
}