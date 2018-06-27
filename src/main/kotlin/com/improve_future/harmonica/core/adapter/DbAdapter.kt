package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.TableBuilder

abstract class DbAdapter(val connection: Connection) {
    fun createTable(tableName: String, block: TableBuilder.() -> TableBuilder) {
        println("Create Table: $tableName")
        createTableCore(tableName, TableBuilder().block())
    }

    protected abstract fun createTableCore(
            tableName: String, tableBuilder: TableBuilder)

    fun dropTable(tableName: String) {
        println("Drop Table: $tableName")
        dropTableCore(tableName)
    }

    open fun dropTableCore(tableName: String) {
        connection.execute("DROP TABLE $tableName")
    }
}