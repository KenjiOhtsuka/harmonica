package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.Connection
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

object MigrationAssertions {
    fun assertTablesExist(connection: Connection, vararg tables: String) {
        for (table in tables) {
            assertTrue(connection.doesTableExist(table), "table $table exists")
        }
    }

    fun assertTablesAbsent(connection: Connection, vararg tables: String) {
        for (table in tables) {
            assertFalse(connection.doesTableExist(table), "table $table was dropped")
        }
    }

    fun assertColumns(
        connection: Connection,
        table: String,
        vararg columnNames: String
    ) {
        val metadata = connection.jdbcConnection.metaData
        for (name in columnNames) {
            metadata.getColumns(null, null, table, name).use { resultSet ->
                assertTrue(resultSet.next(), "column $table.$name exists")
            }
        }
    }

    fun assertColumnNullable(
        connection: Connection,
        table: String,
        name: String,
        nullable: Boolean
    ) {
        connection.jdbcConnection.metaData.getColumns(null, null, table, name).use { resultSet ->
            assertTrue(resultSet.next(), "column $table.$name exists")
            assertEquals(nullable, resultSet.getBoolean("NULLABLE"), "$table.$name nullable")
        }
    }

    fun assertTableRowCount(connection: Connection, table: String, expected: Int) {
        connection.jdbcConnection.createStatement().use { statement ->
            statement.executeQuery("SELECT COUNT(*) FROM $table").use { resultSet ->
                assertTrue(resultSet.next(), "table $table queryable")
                assertEquals(expected, resultSet.getInt(1), "$table row count")
            }
        }
    }

    fun assertIndex(
        connection: Connection,
        table: String,
        indexName: String,
        columnName: String
    ) {
        connection.jdbcConnection.metaData.getIndexInfo(null, null, table, false, false).use { resultSet ->
            var found = false
            while (resultSet.next()) {
                if (resultSet.getString("INDEX_NAME") == indexName &&
                    resultSet.getString("COLUMN_NAME") == columnName
                ) {
                    found = true
                    break
                }
            }
            assertTrue(found, "index $indexName on $table.$columnName exists")
        }
    }
}
