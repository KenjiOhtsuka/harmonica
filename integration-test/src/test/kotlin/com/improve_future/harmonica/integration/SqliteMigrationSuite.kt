package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class SqliteMigrationSuite : AbstractMigrationSuite() {
    @Test
    fun upAndDownAllMigrations() {
        newConnection().use { connection ->
            runAllMigrations(connection)
        }
    }

    override fun newConnection(): Connection {
        val dbPath = Path.of("build", "test-db", "sqlite_migrations")
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
}
