package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PostgreSqlMigrationSuite : AbstractMigrationSuite() {
    companion object {
        @BeforeAll
        @JvmStatic
        fun requirePostgres() {
            val url = "jdbc:postgresql://${TestDb.postgresHost}:${TestDb.postgresPort}/${TestDb.postgresDb}"
            TestDb.requireDb(url, TestDb.postgresUser, TestDb.postgresPassword)
        }
    }

    @Test
    fun upAndDownAllMigrations() {
        newConnection().use { connection ->
            runAllMigrations(connection)
        }
    }

    override fun newConnection(): Connection = Connection.create {
        dbms = Dbms.PostgreSQL
        host = TestDb.postgresHost
        port = TestDb.postgresPort
        dbName = TestDb.postgresDb
        user = TestDb.postgresUser
        password = TestDb.postgresPassword
    }
}
