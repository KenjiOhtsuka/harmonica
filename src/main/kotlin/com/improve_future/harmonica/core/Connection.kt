package com.improve_future.harmonica.core

import org.jetbrains.kotlin.psi.psiUtil.checkReservedYieldBeforeLambda
import java.io.Closeable
import java.sql.*

open class Connection(private val javaConnection: java.sql.Connection): Closeable {
    override fun close() {
        if (!javaConnection.isClosed) javaConnection.close()
    }

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

    fun executeSelect(sql: String) {
        val statement = javaConnection.createStatement().use {
            val rs = it.executeQuery(sql)
            // ToDo: Retrieve all data, like DataTable in .Net
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
        }
    }

    fun execute(sql: String): Boolean {
        javaConnection.createStatement().use {
            return it.execute(sql)
        }
    }

    fun doesTableExist(tableName: String): Boolean {
        javaConnection.metaData.getTables(null, null, tableName, null).use {
            if (it.next()) return true
        }
        return false
    }

    fun createStatement(): Statement {
        return javaConnection.createStatement()
    }
}