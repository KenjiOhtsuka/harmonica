package com.improve_future.harmonica.core.table

import com.improve_future.harmonica.core.table.column.*

typealias Type = Int

class TableBuilder {
    lateinit var name: String
    val columnList = mutableListOf<AbstractColumn>()
    var id = true

    protected fun addColumn(column: AbstractColumn): AbstractColumn {
        columnList.add(column)
        return column
    }

    fun decimal(columnName: String, precision: Int? = null, scale: Int? = null): AbstractColumn {
        return addColumn(DecimalColumn(columnName))
    }

    fun integer(columnName: String): AbstractColumn {
        return addColumn(IntegerColumn(columnName))
    }

    /**
     * variable with limit
     */
    fun varchar(columnName: String, size: Int? = null, notNull: Boolean = false): AbstractColumn {
        return addColumn(VarcharColumn(columnName))
    }

    /**
     * Alias for varchar
     */
    fun string(columnName: String, size: Int? = null, notNull: Boolean = false): AbstractColumn {
        return varchar(columnName, size, notNull)
    }

    fun boolean(columnName: String, notNull: Boolean = false): AbstractColumn {
        return addColumn(BooleanColumn(name))
    }

    fun date(columnName: String): AbstractColumn {
        return addColumn(DateColumn(columnName))
    }

    /**
     * unlimited length
     */
    fun text(columnName: String): AbstractColumn {
        return addColumn(TextColumn(columnName))
    }
}