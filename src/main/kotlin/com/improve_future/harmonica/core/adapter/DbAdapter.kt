package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.table.TableBuilder

abstract class DbAdapter(val connection: Connection) {
    fun createTable(tableName: String, block: TableBuilder.() -> Any) {
        createTable(tableName, TableBuilder().apply { block() })
    }

    abstract fun createTable(tableName: String, tableBuilder: TableBuilder)

    abstract fun createIndex(tableName: String, columnName: String, unique: Boolean = false)

    fun dropTable(tableName: String) {
        dropTableCore(tableName)
    }

    open fun dropTableCore(tableName: String) {
        connection.execute("DROP TABLE $tableName")
    }
}