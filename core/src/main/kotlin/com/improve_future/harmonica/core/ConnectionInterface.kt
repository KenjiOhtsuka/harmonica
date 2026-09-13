package com.improve_future.harmonica.core

import java.sql.Statement

interface ConnectionInterface {
    val config: DbConfig
    val jdbcConnection: java.sql.Connection
    fun transaction(block: Connection.() -> Unit)
    fun execute(sql: String): Boolean
    fun doesTableExist(tableName: String): Boolean
    fun createStatement(): Statement
}