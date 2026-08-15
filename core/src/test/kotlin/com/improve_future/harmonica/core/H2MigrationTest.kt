package com.improve_future.harmonica.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class H2MigrationTest {
    @Test
    fun createTableAndAddColumn() {
        Connection.create {
            dbms = Dbms.H2
            dbName = "mem:harmonica_test"
            user = "sa"
            password = ""
        }.use { connection ->
            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {
                        createTable("h2_table") {
                            varchar("name")
                        }
                    }
                }.apply {
                    this.connection = connection
                }.up()
            }
            assertTrue(connection.doesTableExist("h2_table"))

            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {
                        addVarcharColumn("h2_table", "note")
                    }
                }.apply {
                    this.connection = connection
                }.up()
            }
            assertTrue(connection.jdbcConnection.metaData.getColumns(null, null, "H2_TABLE", "NOTE").next())

            connection.transaction {
                object : AbstractMigration() {
                    override fun down() {
                        dropTable("h2_table")
                    }
                }.apply {
                    this.connection = connection
                }.down()
            }
            assertFalse(connection.doesTableExist("h2_table"))
        }
    }

    @Test
    fun committedDataSurvivesFailedTransaction() {
        Connection.create {
            dbms = Dbms.H2
            dbName = "mem:harmonica_failed_transaction"
            user = "sa"
            password = ""
        }.use { connection ->
            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {
                        createTable("committed_table") {
                            varchar("name")
                        }
                    }
                }.apply {
                    this.connection = connection
                }.up()
                execute("INSERT INTO committed_table (name) VALUES ('before')")
            }

            assertFailsWith<RuntimeException> {
                connection.transaction {
                    execute("INSERT INTO committed_table (name) VALUES ('rolled back')")
                    throw RuntimeException("boom")
                }
            }

            val count = connection.createStatement().use {
                it.executeQuery("SELECT COUNT(*) FROM committed_table").use { resultSet ->
                    resultSet.next()
                    resultSet.getInt(1)
                }
            }
            assertEquals(1, count)
        }
    }

    @Test
    fun doesTableExistWithLowercaseIdentifiers() {
        Connection.create {
            dbms = Dbms.H2
            dbName = "mem:harmonica_lower;DATABASE_TO_LOWER=TRUE"
            user = "sa"
            password = ""
        }.use { connection ->
            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {
                        createTable("lower_table") {
                            varchar("name")
                        }
                    }
                }.apply {
                    this.connection = connection
                }.up()
            }
            assertTrue(connection.doesTableExist("lower_table"))
        }
    }
}
