package com.improve_future.harmonica.core

import java.sql.Types

typealias Type = Int

class TableBuilder {
    lateinit var name: String
    val columnList = mutableListOf<Column>()

    protected fun column(columnName: String, type: Type) {
        columnList.add(Column(columnName, type))
    }

    fun integer(columnName: String) {
        column(columnName, Types.INTEGER)
    }

    fun varchar(columnName: String) {
        column(columnName, Types.VARCHAR)
    }

    fun bool(columnName: String) {
        column(columnName, Types.BOOLEAN)
    }

    fun date(columnName: String) {
        column(columnName, Types.DATE)
    }
}