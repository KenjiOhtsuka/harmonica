/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core

import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*

class VersionService(private val migrationTableName: String) {
    fun setupHarmonicaMigrationTable(connection: Connection) {
        if (!connection.doesTableExist(migrationTableName)) {
            connection.execute(
                """
                CREATE TABLE $migrationTableName (
                    version VARCHAR(255)
                )""".trimIndent()
            )
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
                "SELECT COUNT(1) FROM $migrationTableName WHERE version = '$version'"
            )
            resultSet.next()
            result = (resultSet.getLong(1) > 0)
        } catch (e: Exception) {
            System.err.println(e.message)
            statement.close()
            throw e
        }
        statement.close()
        return result
    }

    /**
     * Insert record of the specified version into version control table
     */
    fun saveVersion(connection: Connection, version: String) {
        connection.execute(
            "INSERT INTO $migrationTableName(version) VALUES('$version')"
        )
    }

    /**
     * Remove record of the specified version from version control table
     */
    fun removeVersion(connection: Connection, version: String) {
        connection.execute(
            "DELETE FROM $migrationTableName WHERE version = '$version'"
        )
    }

    fun findCurrentMigrationVersion(connection: Connection): String {
        var result = ""
        if (!connection.doesTableExist(migrationTableName))
            return result

        val statement = connection.createStatement()
        try {
            val resultSet: ResultSet
            when (connection.config.dbms) {
                Dbms.Oracle -> {
                    resultSet = statement.executeQuery("""
                        SELECT version
                          FROM $migrationTableName
                         ORDER BY version DESC
                         FETCH FIRST 1 ROWS ONLY""".trimIndent())
                }
                else -> {
                    resultSet = statement.executeQuery("""
                        SELECT version
                          FROM $migrationTableName
                         ORDER BY version DESC
                         LIMIT 1""".trimIndent())
                }
            }

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
     * class name is like `M20180101001010101010nMigration`.
     */
    fun pickUpVersionFromClassName(name: String): String {
        val endIndex = name.lastIndexOf(migrationSuffixString)
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
    private fun createNewVersionNumber(): String {
        return dateFormat.format(Date())
    }

    /**
     * Create new migration file/class name
     */
    fun composeNewMigrationName(migrationName: String): String {
        val version = createNewVersionNumber()
        return migrationHeadString + version + migrationSuffixString + migrationName
    }

    fun filterClassCandidateWithVersion(
        classList: List<Class<out AbstractMigration>>, version: String
    ): List<Class<out AbstractMigration>> {
        return classList.filter {
            pickUpVersionFromClassName(it.simpleName) == version
        }
    }

    private companion object {
        private const val migrationHeadString = "M"
        private const val migrationSuffixString = "_"
    }
}