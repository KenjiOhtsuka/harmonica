package com.improve_future.harmonica.core

import org.jetbrains.exposed.sql.Database
import java.io.Closeable
import java.sql.Statement

open class ExposedConnection(
    private val db: Database
) : Closeable, ConnectionInterface {
    private var _connection: java.sql.Connection? = null
    private val javaConnection: java.sql.Connection
        get() {
            if (_connection === null) {
                _connection = db.connector()
            }
            return _connection ?: throw AssertionError("Set to null by another thread")
        }

    override fun close() {
        _connection?.close()
    }

    override fun transaction(block: ConnectionInterface.() -> Unit) {
        org.jetbrains.exposed.sql.transactions.transaction(db = db) {
            this@ExposedConnection.block()
        }
    }

    override val config: DbConfig
        get() = DbConfig() {
            dbms = Dbms.PostgreSQL
        }


    /**
     * Execute SQL
     */
    override fun execute(sql: String): Boolean {
        org.jetbrains.exposed.sql.transactions.transaction(db = db) {
            exec(sql)
        }
        // ToDo: return correct status
        return true
    }

    /**
     * Check table existence
     *
     * @param tableName The name of the table to be checked.
     */
    override fun doesTableExist(tableName: String): Boolean {
        return org.jetbrains.exposed.sql.transactions.transaction(db = db) {
            val resultSet = connection.metaData.getTables(
                null, null,
                tableName,
                null
            )
            val result = resultSet.next()
            resultSet.close()
            result
        }
    }

    override fun createStatement(): Statement {

        return javaConnection.createStatement()
    }
}