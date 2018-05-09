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
        column(columnName, java.sql.Types.INTEGER)
    }

    fun varchar(columnName: String) {
        column(columnName, java.sql.Types.VARCHAR)
    }

    fun bool(columnName: String) {
        column(columnName, java.sql.Types.BOOLEAN)
    }

    fun create(): Table {
        return Table(name)
    }

    fun buildMigrationSql(): String {
        return "CREATE TABLE $name (" +
                columnList.
                        map{ "${it.type} ${it.name}" } .
                        joinToString(", ") +
                ");"
    }
}

class Column(val name: String, val type: Type) {
    val sqlType: String
    get() {
        return when (type) {
            Types.VARCHAR -> "VARCHAR"
            Types.INTEGER -> "INTEGER"
            Types.BOOLEAN -> "BOOL"
            Types.BLOB -> "BLOB"
            Types.DATE -> "DATE"
            else -> throw Exception()
        }
    }
}