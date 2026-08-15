package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.UUID
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
        val tableName = "postgres_table_${UUID.randomUUID().toString().replace("-", "")}"
        val connection = Connection.create {
            dbms = Dbms.PostgreSQL
            host = TestDb.postgresHost
            port = TestDb.postgresPort
            dbName = TestDb.postgresDb
            user = TestDb.postgresUser
            password = TestDb.postgresPassword
        }
        try {
            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {
                        createTable(tableName) {
                            varchar("name")
                        }
                    }
                }.apply {
                    this.connection = connection
                }.up()
            }
            assertTrue(connection.doesTableExist(tableName))
        } finally {
            if (connection.doesTableExist(tableName)) {
                connection.transaction {
                    object : AbstractMigration() {
                        override fun down() {
                            dropTable(tableName)
                        }
                    }.apply {
                        this.connection = connection
                    }.down()
                }
            }
            connection.close()
        }
        assertFalse(connection.doesTableExist(tableName))
    }
}
