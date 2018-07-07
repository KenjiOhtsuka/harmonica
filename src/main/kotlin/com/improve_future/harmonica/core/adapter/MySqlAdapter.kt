package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.table.TableBuilder

class MySqlAdapter(connection: Connection) : DbAdapter(connection) {
    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        var sql = "CREATE TABLE $tableName (\n"
        sql += tableBuilder.columnList.
                joinToString(",\n") { "  ${it.name} ${it.sqlType}" }
        sql += "\n);"
        connection.execute(sql)
    }
}