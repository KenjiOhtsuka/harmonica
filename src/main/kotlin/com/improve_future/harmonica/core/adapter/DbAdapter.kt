package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*

abstract class DbAdapter(val connection: ConnectionInterface) {
    fun createTable(tableName: String, block: TableBuilder.() -> Any) {
        createTable(tableName, TableBuilder().apply { block() })
    }

    abstract fun createTable(tableName: String, tableBuilder: TableBuilder)

    fun dropTable(tableName: String) {
        connection.execute("DROP TABLE $tableName;")
    }

    abstract fun createIndex(tableName: String, columnName: String, unique: Boolean = false)

    internal fun dropIndex(tableName: String, indexName: String) {
        connection.execute("DROP INDEX $indexName ON $tableName;")
    }

    internal abstract fun addColumn(tableName: String, column: AbstractColumn, option: AddingColumnOption)

    fun removeColumn(tableName: String, columnName: String) {
        connection.execute("ALTER TABLE $tableName DROP COLUMN $columnName;")
    }

    internal open class CompanionInterface {
        open fun sqlType(column: AbstractColumn): String {
            return when (column) {
                is IntegerColumn -> "INTEGER"
                is VarcharColumn -> "VARCHAR"
                is DecimalColumn -> "DECIMAL"
                is TextColumn -> "TEXT"
                is BlobColumn -> "BLOB"
                is DateColumn -> "DATE"
                is TimeColumn -> "TIME"
                else -> throw Exception()
            }
        }
    }
}