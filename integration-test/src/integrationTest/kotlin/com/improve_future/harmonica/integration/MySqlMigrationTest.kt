package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MySqlMigrationTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun requireMySql() {
            val url = "jdbc:mysql://${TestDb.mysqlHost}:${TestDb.mysqlPort}/${TestDb.mysqlDb}"
            TestDb.requireDb(url, TestDb.mysqlUser, TestDb.mysqlPassword)
        }
    }

    @Test
    fun createTableAndDropTable() {
        val tableName = "mysql_table_${UUID.randomUUID().toString().replace("-", "")}"
        val connection = Connection.create {
            dbms = Dbms.MySQL
            host = TestDb.mysqlHost
            port = TestDb.mysqlPort
            dbName = TestDb.mysqlDb
            user = TestDb.mysqlUser
            password = TestDb.mysqlPassword
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
