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

    fun integer(columnName: String): Column {
        return column(columnName, Types.INTEGER)
    }

    fun varchar(columnName: String): Column {
        return column(columnName, Types.VARCHAR)
    }

    fun bool(columnName: String): Column {
        return column(columnName, Types.BOOLEAN)
    }

    fun date(columnName: String): Column {
        return column(columnName, Types.DATE)
    }
}