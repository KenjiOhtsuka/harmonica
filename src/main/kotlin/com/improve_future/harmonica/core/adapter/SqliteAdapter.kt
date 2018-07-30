package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.table.IndexMethod
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*

internal class SqliteAdapter(connection: ConnectionInterface) :
    DbAdapter(connection) {
    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        var sql = "CREATE TABLE $tableName (\n"
        if (tableBuilder.id) {
            sql += "  id INTEGER PRIMARY KEY AUTOINCREMENT"
            if (tableBuilder.columnList.size > 0) sql += ','
            sql += "\n"
        }
        sql += tableBuilder.columnList.joinToString(",\n") {
            "  " + buildColumnDeclarationForCreateTableSql(it)
        }
        sql += "\n);"
    }

    override fun createIndex(tableName: String, columnName: String, unique: Boolean, method: IndexMethod?) {
        var sql = "CREATE INDEX ${tableName}_${columnName}_idx"
        sql += " ON $tableName ($columnName);"
        connection.execute(sql)
    }

    override fun dropIndex(tableName: String, indexName: String) {
        val sql = "DROP INDEX $indexName;"
        connection.execute(sql)
    }

    override fun addColumn(tableName: String, column: AbstractColumn, option: AddingColumnOption) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renameTable(oldTableName: String, newTableName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renameIndex(tableName: String, oldIndexName: String, newIndexName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addForeignKey(
        tableName: String,
        columnName: String,
        referencedTableName: String,
        referencedColumnName: String
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    internal companion object : DbAdapter.CompanionInterface() {
        private fun buildColumnDeclarationForCreateTableSql(
            column: AbstractColumn
        ): String {
            var sql = column.name + " " + sqlType(column)
            if (!column.nullable) sql += " NOT NULL"
            if (column.hasDefault) {
                sql += " DEFAULT " + column.sqlDefault
            }
            return sql
        }

        override fun sqlIndexMethod(method: IndexMethod?): String? {
            return null
        }
    }
}