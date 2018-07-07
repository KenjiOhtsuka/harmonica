package com.improve_future.harmonica.core

import java.sql.Types

typealias Type = Int

class TableBuilder {
    lateinit var name: String
    val columnList = mutableListOf<Column>()

    protected fun column(columnName: String, type: Type): Column {
        val column = Column(columnName, type)
        columnList.add(column)
        return column
    }

    fun decimal(columnName: String, precision: Int? = null, scale: Int? = null): Column {
        return column(columnName, Types.DECIMAL)
    }

    fun integer(columnName: String): Column {
        return column(columnName, Types.INTEGER)
    }

    /**
     * variable with limit
     */
    fun varchar(columnName: String, size: Int? = null, notNull: Boolean = false): Column {
        return column(columnName, Types.VARCHAR)
    }

    /**
     * Alias for varchar
     */
    fun string(columnName: String, size: Int? = null, notNull: Boolean = false): Column {
        return varchar(columnName, size, notNull)
    }

    fun bool(columnName: String, notNull: Boolean = false): Column {
        return column(columnName, Types.BOOLEAN)
    }

    fun date(columnName: String): Column {
        return column(columnName, Types.DATE)
    }

    /**
     * unlimited length
     */
    fun text(columnName: String): Column {
        return column(columnName, Types.LONGNVARCHAR)
    }
}