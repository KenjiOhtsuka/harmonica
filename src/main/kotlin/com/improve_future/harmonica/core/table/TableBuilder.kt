package com.improve_future.harmonica.core.table

import com.improve_future.harmonica.core.table.column.*

typealias Type = Int

class TableBuilder {
    lateinit var tableName: String
    val columnList = mutableListOf<AbstractColumn>()
    var id = true

    protected fun addColumn(column: AbstractColumn): AbstractColumn {
        columnList.add(column)
        return column
    }

    fun decimal(
            columnName: String,
            precision: Int? = null,
            scale: Int? = null,
            nullable: Boolean = true,
            default: Double? = null): AbstractColumn {
        val decimalColumn = DecimalColumn(columnName)
        decimalColumn.nullable = nullable
        return addColumn(decimalColumn)
    }

    fun integer(
            columnName: String,
            nullable: Boolean = true,
            default: Int? = null): AbstractColumn {
        val integerColumn = IntegerColumn(columnName)
        integerColumn.nullable = nullable
        return addColumn(integerColumn)
    }

    /**
     * variable with limit
     */
    fun varchar(
            columnName: String,
            size: Int? = null,
            nullable: Boolean = true,
            default: String? = null): AbstractColumn {
        val varcharColumn = VarcharColumn(columnName)
        varcharColumn.nullable = nullable
        return addColumn(varcharColumn)
    }

    /**
     * Alias for varchar
     */
    fun string(
            columnName: String,
            size: Int? = null,
            nullable: Boolean = true,
            default: String? = null): AbstractColumn {
        return varchar(columnName, size, nullable)
    }

    fun boolean(
            columnName: String,
            nullable: Boolean = false,
            default: Boolean? = null): AbstractColumn {
        val booleanColumn = BooleanColumn(columnName)
        booleanColumn.nullable = nullable
        return addColumn(booleanColumn)
    }

    fun date(
            columnName: String,
            nullable: Boolean = false): AbstractColumn {
        val dateColumn = DateColumn(columnName)
        dateColumn.nullable = nullable
        return addColumn(dateColumn)
    }

    /**
     * unlimited length
     */
    fun text(columnName: String): AbstractColumn {
        return addColumn(TextColumn(columnName))
    }
}