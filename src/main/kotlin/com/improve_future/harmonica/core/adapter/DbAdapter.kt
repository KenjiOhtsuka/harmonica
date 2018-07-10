package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.table.TableBuilder

abstract class DbAdapter(val connection: Connection) {
    fun createTable(tableName: String, block: TableBuilder.() -> Any) {
        createTable(tableName, TableBuilder().apply { block() })
    }

    abstract fun createTable(tableName: String, tableBuilder: TableBuilder)

    abstract fun createIndex(tableName: String, columnName: String, unique: Boolean = false)

    fun dropIndex(tableName: String, indexName: String) {
        connection.execute("DROP INDEX $indexName ON $tableName;")
    }

    fun dropTable(tableName: String) {
        connection.execute("DROP TABLE $tableName;")
    }

    fun removeColumn(tableName: String, columnName: String) {
        connection.execute("ALTER TABLE $tableName DROP COLUMN $columnName;")
    }
}