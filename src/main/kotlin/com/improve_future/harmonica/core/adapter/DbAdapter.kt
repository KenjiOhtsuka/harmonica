package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.table.IndexMethod
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*

internal abstract class DbAdapter(internal val connection: ConnectionInterface) {
    fun createTable(tableName: String, block: TableBuilder.() -> Any) {
        createTable(tableName, TableBuilder().apply { block() })
    }

    abstract fun createTable(tableName: String, tableBuilder: TableBuilder)

    fun dropTable(tableName: String) {
        connection.execute("DROP TABLE $tableName;")
    }

    abstract fun createIndex(
        tableName: String, columnName: String, unique: Boolean = false,
        method: IndexMethod? = null
    )

    abstract fun dropIndex(tableName: String, indexName: String)

    abstract fun addColumn(tableName: String, column: AbstractColumn, option: AddingColumnOption)

    fun removeColumn(tableName: String, columnName: String) {
        connection.execute("ALTER TABLE $tableName DROP COLUMN $columnName;")
    }

    abstract fun renameTable(oldTableName: String, newTableName: String)

    fun renameColumn(
        tableName: String, oldColumnName: String, newColumnName: String
    ) {
        connection.execute(
            "ALTER TABLE $tableName" +
                    " RENAME COLUMN $oldColumnName TO $newColumnName;"
        )
    }

    abstract fun renameIndex(
        tableName: String, oldIndexName: String, newIndexName: String
    )

    internal abstract class CompanionInterface {
        protected open fun sqlType(column: AbstractColumn): String {
            return when (column) {
                is IntegerColumn -> "INTEGER"
                is VarcharColumn -> "VARCHAR"
                is DecimalColumn -> "DECIMAL"
                is BooleanColumn -> "BOOL"
                is TextColumn -> "TEXT"
                is BlobColumn -> "BLOB"
                is DateColumn -> "DATE"
                is TimeColumn -> "TIME"
                is DateTimeColumn -> "DATETIME"
                is TimestampColumn -> "TIMESTAMP"
                else -> throw Exception()
            }
        }

        abstract fun sqlIndexMethod(method: IndexMethod?): String?
    }
}