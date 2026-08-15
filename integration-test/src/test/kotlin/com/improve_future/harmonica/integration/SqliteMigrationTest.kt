package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SqliteMigrationTest {
    @Test
    fun createTableAndDropTable() {
        val dbPath = Path.of("build", "test-db", "sqlite_integration")
        Files.createDirectories(dbPath.parent)
        for (suffix in listOf(".db", ".db-wal", ".db-shm")) {
            Files.deleteIfExists(Path.of(dbPath.toString() + suffix))
        }
        Connection.create {
            dbms = Dbms.SQLite
            dbName = dbPath.toString()
            user = ""
            password = ""
        }.use { connection ->
            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {
                        createTable("migration_table") {
                            varchar("name")
                        }
                    }

                    override fun down() {}
                }.apply {
                    this.connection = connection
                }.up()
            }
            assertTrue(connection.doesTableExist("migration_table"))

            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {}

                    override fun down() {
                        dropTable("migration_table")
                    }
                }.apply {
                    this.connection = connection
                }.down()
            }
            assertFalse(connection.doesTableExist("migration_table"))
        }
    }
}
