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

    fun decimal(columnName: String): Column {
        return column(columnName, Types.DECIMAL)
    }

    fun integer(columnName: String): Column {
        return column(columnName, Types.INTEGER)
    }

    /**
     * variable with limit
     */
    fun varchar(columnName: String, size: Int? = null): Column {
        return column(columnName, Types.VARCHAR)
    }

    /**
     * Alias for varchar
     */
    fun string(columnName: String): Column {
        return varchar(columnName)
    }

    fun bool(columnName: String): Column {
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