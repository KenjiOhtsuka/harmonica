package com.improve_future.harmonica.core

import org.junit.jupiter.api.Test
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

                    override fun down() {}
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

                    override fun down() {}
                }.apply {
                    this.connection = connection
                }.up()
            }
            assertTrue(connection.jdbcConnection.metaData.getColumns(null, null, "H2_TABLE", "NOTE").next())

            connection.transaction {
                object : AbstractMigration() {
                    override fun up() {}

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
}
