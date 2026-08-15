package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PostgreSqlMigrationTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun requirePostgres() {
            val url = "jdbc:postgresql://${TestDb.postgresHost}:${TestDb.postgresPort}/${TestDb.postgresDb}"
            TestDb.requireDb(url, TestDb.postgresUser, TestDb.postgresPassword)
        }
    }

    @Test
    fun createTableAndDropTable() {
        Connection.create {
            dbms = Dbms.PostgreSQL
            host = TestDb.postgresHost
            port = TestDb.postgresPort
            dbName = TestDb.postgresDb
            user = TestDb.postgresUser
            password = TestDb.postgresPassword
        }.use { connection ->
            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {
                        createTable("postgres_table") {
                            varchar("name")
                        }
                    }

                    override fun down() {}
                }.apply {
                    this.connection = connection
                }.up()
            }
            assertTrue(connection.doesTableExist("postgres_table"))

            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {}

                    override fun down() {
                        dropTable("postgres_table")
                    }
                }.apply {
                    this.connection = connection
                }.down()
            }
            assertFalse(connection.doesTableExist("postgres_table"))
        }
    }
}
