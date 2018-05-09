package com.improve_future.harmonica.core

import java.sql.DriverManager

class Connection(private val javaConnection: java.sql.Connection) {
    companion object {
        fun create(block: DbConfig.() -> Unit): Connection {
            return create(DbConfig.create(block))
        }

        private fun create(dbConfig: DbConfig): Connection {
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

    fun executeSelect(sql: String) {
        val statement = javaConnection.createStatement().use {
            val rs = it.executeQuery(sql)
            // ToDo: Retrieve all data, like DataTable in .Net
            while (rs.next()) { }
        }
    }

    fun execute(sql: String): Boolean {
        javaConnection.createStatement().use {
            return it.execute(sql)
        }
    }
}