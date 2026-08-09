package com.improve_future.harmonica.exposed

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.sql.SQLException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private object Users : Table("users_exposed") {
    val name = varchar("name", 100)
}

private class CreateAndInsertMigration : AbstractMigration() {
    override fun up() {
        exposedTransaction {
            SchemaUtils.create(Users)
            Users.insert { it[name] = "test-user" }
        }
    }
}

private class FailingMigration : AbstractMigration() {
    override fun up() {
        createTable("harmonica_rollback_probe") { integer("probe") }
        exposedTransaction {
            SchemaUtils.create(Users)
            error("boom")
        }
    }
}

private class InvalidSqlMigration : AbstractMigration() {
    override fun up() {
        exposedTransaction {
            exec("THIS IS NOT SQL")
        }
    }
}

class ExposedMigrationTest {
    private fun createConnection(name: String): Connection {
        val dbPath = Path.of("build", "test-db", name)
        Files.createDirectories(dbPath.parent)
        for (suffix in listOf(".db", ".db-wal", ".db-shm")) {
            Files.deleteIfExists(Path.of(dbPath.toString() + suffix))
        }
        return Connection.create {
            dbms = Dbms.SQLite
            dbName = dbPath.toString()
            user = ""
            password = ""
        }
    }

    private fun Connection.rowCount(tableName: String): Int {
        createStatement().use { statement ->
            statement.executeQuery("SELECT COUNT(*) FROM $tableName").use { rs ->
                rs.next()
                return rs.getInt(1)
            }
        }
    }

    @Test
    fun testExposedDslRunsInsideHarmonicaTransaction() {
        val connection = createConnection("success")
        val migration = CreateAndInsertMigration()
        migration.connection = connection
        connection.transaction {
            migration.up()
        }
        assertTrue(connection.doesTableExist("users_exposed"))
        assertEquals(1, connection.rowCount("users_exposed"))
        assertTrue(!connection.jdbcConnection.isClosed)
        assertTrue(!connection.jdbcConnection.autoCommit)
        connection.close()
    }

    @Test
    fun testExposedDslRollsBackWithHarmonicaOnFailure() {
        val connection = createConnection("rollback")
        val migration = FailingMigration()
        migration.connection = connection
        assertFailsWith<IllegalStateException> {
            connection.transaction {
                migration.up()
            }
        }
        assertTrue(!connection.doesTableExist("users_exposed"))
        assertTrue(!connection.doesTableExist("harmonica_rollback_probe"))
        connection.close()
    }

    @Test
    fun testExposedDslSurvivesHarmonicaReconnect() {
        val connection = createConnection("reconnect")
        val failing = FailingMigration()
        failing.connection = connection
        assertFailsWith<IllegalStateException> {
            connection.transaction {
                failing.up()
            }
        }
        val ok = CreateAndInsertMigration()
        ok.connection = connection
        connection.transaction {
            ok.up()
        }
        assertTrue(connection.doesTableExist("users_exposed"))
        assertEquals(1, connection.rowCount("users_exposed"))
        connection.close()
    }

    @Test
    fun testSqlExceptionPropagatesThroughProxyAsSqlException() {
        val connection = createConnection("sqlerror")
        val migration = InvalidSqlMigration()
        migration.connection = connection
        assertFailsWith<SQLException> {
            connection.transaction {
                migration.up()
            }
        }
        connection.close()
    }
}
