package com.improve_future.harmonica.service

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import java.nio.file.Paths
import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*

class VersionService(private val migrationTableName: String) {
    fun setupHarmonicaMigrationTable(connection: Connection) {
        if (!connection.doesTableExist(migrationTableName)) {
            connection.execute("""
                CREATE TABLE harmonica_migration (
                    version VARCHAR(255)
                )""".trimIndent())
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
            result = (resultSet.getLong(1) > 0)
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
                 LIMIT 1""".trimIndent())
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

    /**
     * Pick up version string from class name
     *
     * class name is like `M20180101001010101010_Migration`.
     */
    fun pickUpVersionFromClassName(clazz: Class<out Any>): String {
        return pickUpVersionFromClassName(clazz.name)
    }

    /**
     * Pick up version string from class name
     *
     * class name is like `M20180101001010101010_Migration`.
     */
    fun pickUpVersionFromClassName(name: String): String {
        val endIndex = name.lastIndexOf('_')
        val startIndex = name.substring(0, endIndex).lastIndexOf(migrationHeadString)
        return name.substring(startIndex + migrationHeadString.length, endIndex)
    }

    /**
     * Date format to use for Version Number
     */
    private val dateFormat: SimpleDateFormat =
            SimpleDateFormat("yyyyMMddHHmmssSSS")

    /**
     * Create new version number
     */
    fun createNewVersionNumber(): String {
        return dateFormat.format(Date())
    }

    private val migrationHeadString = "M"

    /**
     * Create new migration file/class name
     */
    fun composeNewMigrationName(migrationName: String): String {
        val version = createNewVersionNumber()
        return "$migrationHeadString${version}_$migrationName"
    }

    fun filterClassCandidateWithVersion(
            classList: List<Class<out AbstractMigration>>, version: String
    ): List<Class<out AbstractMigration>> {
        return classList.filter {
            pickUpVersionFromClassName(it.simpleName) == version
        }
    }
}