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
        println("Create Table: $name")
        adapter.createTable(name, block)
    }

    fun dropTable(name: String) {
        println("Drop Table: $name")
        adapter.dropTable(name)
    }

    fun removeColumn(tableName: String, columnName: String) {
        println("Remove Column: $tableName $columnName")
        adapter.removeColumn(tableName, columnName)
    }

    private fun addColumn(tableName: String) {
        // ToDo
    }

    fun addIntegerColumn() {

    }

    fun addVarcharColumn() {

    }

    fun addBooleanColumn() {

    }

    fun addTextColumn() {

    }

    fun createIndex(tableName: String, columnName: String) {
        println("Add Index: $tableName $columnName")
        adapter.createIndex(tableName, columnName)
    }

//    fun createIndex(tableName: String, columnNameArray: Array<String>) {
//        // ToDo
//        // println("Add Index:")
//    }

    fun dropIndex(tableName: String, indexName: String) {
        adapter.dropIndex(tableName, indexName)
        println("Drop Index: $tableName $indexName")
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