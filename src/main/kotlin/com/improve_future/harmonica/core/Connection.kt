package com.improve_future.harmonica.core

import java.io.Closeable
import java.sql.*

open class Connection(private val javaConnection: java.sql.Connection): Closeable {
    override fun close() {
        if (!isClosed) javaConnection.close()
    }

    val isClosed: Boolean
    get() { return javaConnection.isClosed }

    init {
        javaConnection.autoCommit = false
    }

    companion object {
        fun create(block: DbConfig.() -> Unit): Connection {
            return create(DbConfig.create(block))
        }

        fun create(dbConfig: DbConfig): Connection {
            return Connection(
                    DriverManager.getConnection(
                            buildConnectionUriFromDbConfig(dbConfig),
                            dbConfig.user,
                            dbConfig.password
                    ))
        }

        private fun buildConnectionUriFromDbConfig(dbConfig: DbConfig): String {
            return dbConfig.run {
                when (dbms) {
                    Dbms.PostgreSQL ->
                        "jdbc:postgresql://$host:$port/$dbName"
                    Dbms.MySQL ->
                        "jdbc:mysql://$host:$port/$dbName"
                    Dbms.SQLite ->
                        ""
                }
            }
        }
    }

    open fun transaction(block: Connection.() -> Unit) {
        try {
            block()
            javaConnection.commit()
        } catch(e: Exception) {
            javaConnection.rollback()
            throw e
        }
    }

    fun execAndClose(block: (Connection) -> Unit) {
        block(this)
        this.close()
    }

    fun executeSelect(sql: String) {
        val statement = createStatement()
        val rs = statement.executeQuery(sql)
        while (rs.next()) {
            for (i in 0 until rs.metaData.columnCount - 1) {
                when (rs.metaData.getColumnType(i)) {
                    Types.DATE -> {}
                    Types.BIGINT -> {}
                    Types.BINARY -> {}
                    Types.BIT -> {}
                }
            }
        }
        rs.close()
    }

    /**
     * Execute SQL
     */
    fun execute(sql: String): Boolean {
        val statement = createStatement()
        val result: Boolean
        result = statement.execute(sql)
        statement.close()
        return result
    }

    fun doesTableExist(tableName: String): Boolean {
        val resultSet = javaConnection.metaData.getTables(null, null, tableName, null)
        val result = resultSet.next()
        resultSet.close()
        return result
    }

    fun createStatement(): Statement {
        return javaConnection.createStatement()
    }
}