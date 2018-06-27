package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.TableBuilder

abstract class DbAdapter(val connection: Connection) {
    fun createTable(tableName: String, block: TableBuilder.() -> TableBuilder) {
        createTable(tableName, TableBuilder().block())
    }

    abstract fun createTable(tableName: String, tableBuilder: TableBuilder)

    fun dropTable(tableName: String) {
        dropTableCore(tableName)
    }

    open fun dropTableCore(tableName: String) {
        connection.execute("DROP TABLE $tableName")
    }
}