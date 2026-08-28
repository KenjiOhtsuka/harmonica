package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.demo.jarmonica.migration.M20180714192339748_NormalMigration
import com.improve_future.harmonica.demo.jarmonica.migration.M20180714194338790_NotNullMigration
import com.improve_future.harmonica.demo.jarmonica.migration.M20180714194840311_DefaultMigration
import com.improve_future.harmonica.demo.jarmonica.migration.M20180714203511949_OtherMigration

abstract class AbstractMigrationSuite {
    protected abstract fun newConnection(): Connection

    protected fun runAllMigrations(connection: Connection) {
        dropLeftoverTables(connection)
        verifyNormalMigration(connection)
        verifyNotNullMigration(connection)
        verifyDefaultMigration(connection)
        verifyOtherMigration(connection)
        DemoMigrations.downAll(connection)
        assertDropped(connection)
    }

    private fun dropLeftoverTables(connection: Connection) {
        val migration = object : AbstractMigration() {
            override fun up() {}
            override fun down() {}
        }
        migration.connection = connection
        listOf(
            "other_table", "other_table_for_add",
            "default_table", "default_table_for_add",
            "not_null_table", "not_null_table_for_add",
            "normal_table", "normal_table_for_add"
        ).forEach {
            if (connection.doesTableExist(it)) migration.dropTable(it)
        }
    }

    private fun runUp(connection: Connection, migration: AbstractMigration) {
        connection.transaction {
            migration.connection = this
            migration.up()
        }
    }

    private fun verifyNormalMigration(connection: Connection) {
        runUp(connection, M20180714192339748_NormalMigration())
        MigrationAssertions.assertTablesExist(connection, "normal_table", "normal_table_for_add")
        MigrationAssertions.assertColumns(
            connection, "normal_table",
            "integer_column", "varchar_column", "decimal_column", "boolean_column",
            "blob_column", "date_column", "time_column", "date_time_column",
            "timestamp_column", "text_column"
        )
        MigrationAssertions.assertColumns(
            connection, "normal_table_for_add",
            "integer_column", "varchar_column", "decimal_column", "boolean_column",
            "blob_column", "date_column", "time_column", "date_time_column",
            "timestamp_column", "text_column"
        )
        MigrationAssertions.assertIndex(
            connection, "normal_table", "normal_table_integer_column_idx", "integer_column"
        )
        MigrationAssertions.assertTableRowCount(connection, "normal_table", 0)
        MigrationAssertions.assertTableRowCount(connection, "normal_table_for_add", 0)
    }

    private fun verifyNotNullMigration(connection: Connection) {
        runUp(connection, M20180714194338790_NotNullMigration())
        MigrationAssertions.assertTablesExist(connection, "not_null_table", "not_null_table_for_add")
        MigrationAssertions.assertColumns(
            connection, "not_null_table",
            "integer_column", "varchar_column", "decimal_column", "boolean_column",
            "blob_column", "date_column", "time_column", "date_time_column",
            "timestamp_column", "text_column"
        )
        MigrationAssertions.assertColumnNullable(
            connection, "not_null_table", "integer_column", nullable = false
        )
        MigrationAssertions.assertColumns(
            connection, "not_null_table_for_add",
            "integer_column", "varchar_column", "decimal_column", "boolean_column",
            "blob_column", "date_column", "time_column", "date_time_column",
            "timestamp_column", "text_column"
        )
    }

    private fun verifyDefaultMigration(connection: Connection) {
        runUp(connection, M20180714194840311_DefaultMigration())
        MigrationAssertions.assertTablesExist(connection, "default_table", "default_table_for_add")
        MigrationAssertions.assertColumns(
            connection, "default_table",
            "integer_column", "varchar_column", "decimal_column", "boolean_column",
            "blob_column", "date_column_1", "date_column_2", "date_column_3",
            "time_column_1", "time_column_2", "time_column_3",
            "date_time_column_1", "date_time_column_2", "date_time_column_3",
            "timestamp_column_1", "timestamp_column_2", "timestamp_column_3",
            "text_column"
        )
    }

    private fun verifyOtherMigration(connection: Connection) {
        runUp(connection, M20180714203511949_OtherMigration())
        MigrationAssertions.assertTablesExist(connection, "other_table", "other_table_for_add")
        MigrationAssertions.assertColumns(
            connection, "other_table",
            "integer_column", "varchar_column", "decimal_column", "boolean_column",
            "blob_column", "date_column", "time_column", "date_time_column",
            "timestamp_column", "text_column"
        )
        MigrationAssertions.assertColumns(
            connection, "other_table_for_add",
            "integer_column", "varchar_column", "decimal_column", "boolean_column",
            "blob_column", "date_column", "time_column", "date_time_column",
            "timestamp_column", "text_column"
        )
    }

    private fun assertDropped(connection: Connection) {
        MigrationAssertions.assertTablesAbsent(
            connection,
            "normal_table", "normal_table_for_add",
            "not_null_table", "not_null_table_for_add",
            "default_table", "default_table_for_add",
            "other_table", "other_table_for_add"
        )
    }
}
