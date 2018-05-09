package com.improve_future.harmonica.core

abstract class AbstractMigration {
    lateinit var connection: Connection

    fun createTable(name: String, block: TableBuilder.() -> Unit): Table {
        val tb = TableBuilder()
        tb.block()
        connection.execute(tb.buildMigrationSql())
        return tb.create()
    }

    fun dropTable(name: String) {
        connection.execute("DROP TABLE $name")
    }

    open fun up() {

    }

    open fun down() {

    }
}