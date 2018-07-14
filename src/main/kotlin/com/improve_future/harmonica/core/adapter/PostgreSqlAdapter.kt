package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*

class PostgreSqlAdapter(connection: ConnectionInterface) : DbAdapter(connection) {
    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        var sql = "CREATE TABLE $tableName (\n"
        if (tableBuilder.id) {
            sql += "  id SERIAL PRIMARY KEY"
            if (tableBuilder.columnList.size > 0) sql += ','
            sql += "\n"
        }
        sql += tableBuilder.columnList.joinToString(",\n") {
            "  " + buildColumnDeclarationForCreateTableSql(it)
        }
        sql += "\n);"
        connection.execute(sql)
    }

    companion object {
        private fun buildColumnDeclarationForCreateTableSql(
            column: AbstractColumn
        ): String {
            var sql = column.name + " " + column.sqlType
            when (column) {
                is VarcharColumn -> {
                    if (column.size != null)
                        sql += "(" + column.size.toString() + ")"
                }
                is DecimalColumn -> {
                    if (column.precision != null) {
                        sql += "(" + column.precision.toString()
                        if (column.scale != null) {
                            sql += ", " + column.scale.toString()
                        }
                        sql += ")"
                    }
                }
            }
            if (!column.nullable) sql += " NOT NULL"
            if (column.hasDefault) {
                sql += " DEFAULT " + column.sqlDefault
            }
            return sql
        }
    }

    override fun createIndex(tableName: String, columnName: String, unique: Boolean) {
        var sql = "CREATE"
        if (unique) sql += " UNIQUE"
        //sql += " INDEX ${tableName}_$columnName ON $tableName($columnName);"
        sql += " INDEX ON $tableName($columnName);"
        connection.execute(sql)
    }

    override fun addColumn(tableName: String, column: AbstractColumn, option: AddingColumnOption) {
        var sql = "ALTER TABLE $tableName ADD COLUMN "
        sql += buildColumnDeclarationForCreateTableSql(column)
        sql += ";"
        connection.execute(sql)
    }
}