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

    abstract fun addColumn(
        tableName: String,
        column: AbstractColumn,
        option: AddingColumnOption
    )

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

    abstract fun addForeignKey(
        tableName: String, columnName: String,
        referencedTableName: String, referencedColumnName: String
    )

    fun dropForeignKey(
        tableName: String, columnName: String
    ) {
        dropForeignKey(
            tableName, columnName,
            buildForeignKeyName(tableName, columnName)
        )
    }

    abstract fun dropForeignKey(
        tableName: String, columnName: String,
        keyName: String
    )

    protected open fun buildForeignKeyName(
        tableName: String, columnName: String
    ) = "${tableName}_${columnName}_fkey"

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

        protected abstract fun sqlIndexMethod(method: IndexMethod?): String?
    }
}