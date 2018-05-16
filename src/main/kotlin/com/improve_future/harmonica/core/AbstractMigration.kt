package com.improve_future.harmonica.core

import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager

abstract class AbstractMigration {
    lateinit var connection: Connection

    fun createTable(name: String, block: TableBuilder.() -> Unit): Table {
        println("Create Table: $name")
        val tb = TableBuilder()
        tb.name = name
        tb.block()
        connection.execute(tb.buildMigrationSql())
        return tb.create()
    }

    fun dropTable(name: String) {
        println("Drop Table: $name")
        connection.execute("DROP TABLE $name;")
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