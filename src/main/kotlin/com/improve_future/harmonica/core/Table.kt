package com.improve_future.harmonica.core

import java.sql.Types

class Table(name: String) {
    // ToDo: Refactor. It might be useless.
    companion object {
        fun create(block: TableBuilder.() -> Unit): Table {
            val tb = TableBuilder()
            tb.block()
            tb.buildMigrationSql()
            return tb.create()
        }

        fun drop() {

        }
    }
}

typealias Type = Int

class TableBuilder {
    lateinit var name: String
    val columnList = mutableListOf<Column>()

    fun column(columnName: String, type: Type) {
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

    fun create(): Table {
        return Table(name)
    }

    fun buildMigrationSql(): String {
        return "CREATE TABLE $name (" +
                columnList.joinToString(", ") { "${it.name} ${it.sqlType}" } +
                ");"
    }
}