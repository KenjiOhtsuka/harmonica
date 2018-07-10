package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.adapter.DbAdapter
import com.improve_future.harmonica.core.adapter.MySqlAdapter
import com.improve_future.harmonica.core.adapter.PostgreSqlAdapter
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.AbstractColumn
import com.improve_future.harmonica.core.table.column.AddingColumnOption
import com.improve_future.harmonica.core.table.column.IntegerColumn

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

    private fun addColumn(tableName: String, column: AbstractColumn, option: AddingColumnOption) {
        // ToDo
    }

    /**
     * Add new integer column to existing table
     *
     * @param first You add column at first of the columns
     * valid only for MySQL
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addIntegerColumn(
            tableName: String, columnName: String,
            nullable: Boolean = true, default: Long? = null,
            first: Boolean = false,
            justBeforeColumnName: String? = null) {
        val integerColumn = IntegerColumn(columnName)
        integerColumn.also {
            it.nullable = nullable
            it.default = default
        }
        val option = AddingColumnOption().also {
            it.first = first
            it.justBeforeColumn = justBeforeColumnName
        }
        addColumn(tableName, integerColumn, option)
    }

    fun addVarcharColumn(
            tableName: String, columnName: String,
            nullable: Boolean = true, default: String? = null) {
    }

    fun addBooleanColumn(
            tableName: String, columnName: String,
            nullable: Boolean = true, default: Boolean? = null) {

    }

    fun addTextColumn(
            tableName: String, columnName: String,
            nullable: Boolean = true, default: String? = null) {

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