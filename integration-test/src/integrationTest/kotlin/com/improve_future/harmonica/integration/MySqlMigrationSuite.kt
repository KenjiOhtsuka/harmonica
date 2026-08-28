package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class MySqlMigrationSuite : AbstractMigrationSuite() {
    companion object {
        @BeforeAll
        @JvmStatic
        fun requireMySql() {
            val url = "jdbc:mysql://${TestDb.mysqlHost}:${TestDb.mysqlPort}/${TestDb.mysqlDb}"
            TestDb.requireDb(url, TestDb.mysqlUser, TestDb.mysqlPassword)
        }
    }

    @Test
    fun upAndDownAllMigrations() {
        newConnection().use { connection ->
            runAllMigrations(connection)
        }
    }

    override fun newConnection(): Connection = Connection.create {
        dbms = Dbms.MySQL
        host = TestDb.mysqlHost
        port = TestDb.mysqlPort
        dbName = TestDb.mysqlDb
        user = TestDb.mysqlUser
        password = TestDb.mysqlPassword
    }
}
