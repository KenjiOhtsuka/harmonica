package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.Harmonica
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.util.PSQLException

class HarmonicaTest {
    private lateinit var embeddedPostgres: EmbeddedPostgres

    @BeforeEach
    fun setup() {
        embeddedPostgres = EmbeddedPostgres.start()
    }

    @AfterEach
    fun after() {
        embeddedPostgres.close()
    }

    @Test
    fun testUp() {
        val dataBase = Database.connect(embeddedPostgres.postgresDatabase)
        val harmonica = Harmonica(packageName = "com.improve_future.harmonica.plugin.migration")

        harmonica.up(dataBase)

        transaction {
            val sql = """
            SELECT *
            FROM refresh_token
        """.trimIndent()

            val conn = TransactionManager.current().connection
            val statement = conn.createStatement()
            statement.execute(sql)
        }
    }

    @Test
    fun testDown() {
        val dataBase = Database.connect(embeddedPostgres.postgresDatabase)
        val harmonica = Harmonica(packageName = "com.improve_future.harmonica.plugin.migration")

        harmonica.up(dataBase)
        harmonica.down(dataBase)

        val exception: Exception = assertThrows(PSQLException::class.java) {
            transaction {
                TransactionManager
                    .current()
                    .connection
                    .createStatement()
                    .execute("SELECT * FROM refresh_token_d".trimIndent())
            }
        }

        transaction {
            TransactionManager
                .current()
                .connection
                .createStatement()
                .execute("SELECT * FROM refresh_token".trimIndent())
        }

        assertTrue(
            "ERROR: relation \"refresh_token_d\" does not exist\n  Позиция: 15"
                .contains(exception.message ?: "")
        )

    }

    @Test
    fun testDownWithCount() {
        val dataBase = Database.connect(embeddedPostgres.postgresDatabase)
        val harmonica = Harmonica(packageName = "com.improve_future.harmonica.plugin.migration")

        harmonica.up(dataBase)
        harmonica.down(dataBase, 3)

        val exception: Exception = assertThrows(PSQLException::class.java) {
            transaction {
                TransactionManager
                    .current()
                    .connection
                    .createStatement()
                    .execute("SELECT * FROM refresh_token_d".trimIndent())
            }
        }

        val secondException: Exception = assertThrows(PSQLException::class.java) {
            transaction {
                TransactionManager
                    .current()
                    .connection
                    .createStatement()
                    .execute("SELECT * FROM refresh_token".trimIndent())
            }
        }

        assertTrue(
            "ERROR: relation \"refresh_token_d\" does not exist\n  Позиция: 15"
                .contains(exception.message ?: "")
        )
        assertTrue(
            "ERROR: relation \"refresh_token\" does not exist\n  Позиция: 15"
                .contains(secondException.message ?: "")
        )

    }

    @Test
    fun testDownAll() {
        val dataBase = Database.connect(embeddedPostgres.postgresDatabase)
        val harmonica = Harmonica(packageName = "com.improve_future.harmonica.plugin.migration")

        harmonica.up(dataBase)
        harmonica.allDown(dataBase)

        val exception: Exception = assertThrows(PSQLException::class.java) {
            transaction {
                TransactionManager
                    .current()
                    .connection
                    .createStatement()
                    .execute("SELECT * FROM refresh_token_d".trimIndent())
            }
        }

        val secondException: Exception = assertThrows(PSQLException::class.java) {
            transaction {
                TransactionManager
                    .current()
                    .connection
                    .createStatement()
                    .execute("SELECT * FROM refresh_token".trimIndent())
            }
        }

        assertTrue(
            "ERROR: relation \"refresh_token_d\" does not exist\n  Позиция: 15"
                .contains(exception.message ?: "")
        )
        assertTrue(
            "ERROR: relation \"refresh_token\" does not exist\n  Позиция: 15"
                .contains(secondException.message ?: "")
        )

    }
}
