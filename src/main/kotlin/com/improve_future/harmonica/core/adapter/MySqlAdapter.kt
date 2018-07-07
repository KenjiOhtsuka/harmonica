package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.AbstractColumn
import com.improve_future.harmonica.core.table.column.IntegerColumn
import com.improve_future.harmonica.core.table.column.VarcharColumn

class MySqlAdapter(connection: Connection) : DbAdapter(connection) {
    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        var sql = "CREATE TABLE $tableName (\n"
        if (tableBuilder.id) {
            sql += "  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY"
            if (tableBuilder.columnList.size > 0) sql += ','
            sql += "\n"
        }
        sql += tableBuilder.columnList.
                joinToString(",\n") {
                    buildColumnDeclarationForCreateTableSql(it)
                }
        sql += "\n);"
        connection.execute(sql)
    }

    companion object {
        private fun buildColumnDeclarationForCreateTableSql(column: AbstractColumn
        ): String {
            var sql = "  "
            sql += column.name + " " + column.sqlType
            when (column) {
                is VarcharColumn -> {
                    sql +=
                            if (column.size == null) "(255)"
                            else "(" + column.size + ")"
                }
            }
            if (!column.nullable) sql += " NOT NULL"
            return sql
        }
    }
}