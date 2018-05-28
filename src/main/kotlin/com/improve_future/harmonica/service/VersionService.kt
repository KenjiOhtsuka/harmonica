package com.improve_future.harmonica.service

import com.improve_future.harmonica.core.Connection
import java.sql.ResultSet
import java.sql.Statement

class VersionService(val migrationTableName: String) {
    fun setupHarmonicaMigrationTable(connection: Connection) {
        if (!connection.doesTableExist(migrationTableName)) {
            connection.execute("""
                CREATE TABLE harmonica_migration (
                    version VARCHAR
                )""".trimMargin())
        }
    }

    private fun createStatement(connection: Connection): Statement {
        return connection.createStatement()
    }

    fun isVersionMigrated(connection: Connection, version: String): Boolean {
        val statement = createStatement(connection)
        val result: Boolean
        val resultSet: ResultSet
        try {
            resultSet = statement.executeQuery(
                    "SELECT COUNT(1) FROM $migrationTableName WHERE version = '$version';")
            resultSet.next()
            result = resultSet.getLong(1) > 0
        } catch (e: Exception) {
            println(e.message)
            statement.close()
            throw e
        }
        statement.close()
        return result
    }

    fun saveVersion(connection: Connection, version: String) {
        connection.execute(
                "INSERT INTO $migrationTableName(version) VALUES('$version');")
    }

    fun removeVersion(connection: Connection, version: String) {
        connection.execute(
                "DELETE FROM $migrationTableName WHERE version = '$version'")
    }

    fun findCurrentMigrationVersion(connection: Connection): String {
        var result: String = ""
        if (!connection.doesTableExist(migrationTableName))
            return result

        val statement = connection.createStatement()
        try {
            val resultSet = statement.executeQuery("""
                SELECT version
                  FROM $migrationTableName
                 ORDER BY version DESC
                 LIMIT 1""".trimMargin())
            if (resultSet.next()) result = resultSet.getString(1)
            resultSet.close()
            statement.close()
        } catch (e: Exception) {
            statement.close()
            throw e
        }
        statement.close()
        return result
    }
}