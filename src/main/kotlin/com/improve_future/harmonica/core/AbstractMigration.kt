package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.adapter.DbAdapter
import com.improve_future.harmonica.core.adapter.MySqlAdapter
import com.improve_future.harmonica.core.adapter.PostgreSqlAdapter
import com.improve_future.harmonica.core.table.TableBuilder

abstract class AbstractMigration {
    lateinit var connection: Connection

    private val adapter: DbAdapter by lazy {
        when (connection.config.dbms) {
            Dbms.MySQL -> MySqlAdapter(connection)
            else -> PostgreSqlAdapter(connection)
        }
    }

    fun createTable(name: String, block: TableBuilder.() -> Unit) {
        adapter.createTable(name, block)
    }

    fun dropTable(name: String) {
        println("Drop Table: $name")
        adapter.dropTable(name)
    }

    fun removeColumn(tableName: String) {
        // ToDo
    }

    fun addColumn(tableName: String) {
        // ToDo
    }

    fun createIndex(tableName: String, columnName: String) {
        adapter.createIndex(tableName, columnName)
        println("Add Index: $tableName $columnName")
    }

    fun createIndex(tableName: String, columnNameArray: Array<String>) {
        // ToDo
        // println("Add Index:")
    }

    fun removeIndex(tableName: String) {
        // ToDo
    }

    fun executeSql(sql: String) {
        println("Execute SQL")
        connection.execute(sql)
    }

    open fun up() {

    }

    open fun down() {

    }
}