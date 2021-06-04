/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.service

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.Dbms
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.Date

class VersionService(private val migrationTableName: String) {
    internal fun setupHarmonicaMigrationTable(connection: ConnectionInterface) {
        if (!connection.doesTableExist(migrationTableName)) {
            connection.execute(
                """
                CREATE TABLE $migrationTableName (
                    version VARCHAR(255)
                )""".trimIndent()
            )
        }
    }

    private fun createStatement(connection: ConnectionInterface): Statement {
        return connection.createStatement()
    }

    internal fun isVersionMigrated(connection: ConnectionInterface, version: String): Boolean {
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
    internal fun saveVersion(connection: ConnectionInterface, version: String) {
        connection.execute(
            "INSERT INTO $migrationTableName(version) VALUES('$version')"
        )
    }

    /**
     * Remove record of the specified version from version control table
     */
    internal fun removeVersion(connection: ConnectionInterface, version: String) {
        connection.execute(
            "DELETE FROM $migrationTableName WHERE version = '$version'"
        )
    }

    internal fun allListMigrationVersion(connection: ConnectionInterface): List<String> {
        if (!connection.doesTableExist(migrationTableName)) return listOf()

        val resultList = mutableListOf<String>()
        val statement = connection.createStatement()
        try {
            val resultSet = when (connection.config.dbms) {
                Dbms.Oracle -> {
                    statement.executeQuery(
                        """
                        SELECT version
                          FROM $migrationTableName
                         ORDER BY version DESC
                         """.trimIndent()
                    )
                }
                else -> {
                    statement.executeQuery(
                        """
                        SELECT version
                          FROM $migrationTableName
                         ORDER BY version DESC
                         """.trimIndent()
                    )
                }
            }
            while (resultSet.next()) {
                resultList.add(resultSet.getString(1));
            }
            resultSet.close()
        } catch (e: Exception) {
            throw e
        } finally {
            statement.close()
        }
        return resultList
    }

    internal fun findListMigrationVersion(connection: ConnectionInterface, count: Int): List<String> {
        if (!connection.doesTableExist(migrationTableName)) return listOf()

        val resultList = mutableListOf<String>()
        val statement = connection.createStatement()
        try {
            val resultSet = when (connection.config.dbms) {
                Dbms.Oracle -> {
                    statement.executeQuery(
                        """
                        SELECT version
                          FROM $migrationTableName
                         ORDER BY version DESC
                         FETCH FIRST $count ROWS ONLY
                         """.trimIndent()
                    )
                }
                else -> {
                    statement.executeQuery(
                        """
                        SELECT version
                          FROM $migrationTableName
                         ORDER BY version DESC
                         LIMIT $count
                         """.trimIndent()
                    )
                }
            }
            while (resultSet.next()) {
                resultList.add(resultSet.getString(1));
            }
            resultSet.close()
        } catch (e: Exception) {
            throw e
        } finally {
            statement.close()
        }
        return resultList
    }

    internal fun findCurrentMigrationVersion(connection: ConnectionInterface): String {
        return findListMigrationVersion(connection, 1).firstOrNull() ?: ""
    }

    /**
     * Pick up version string from class name
     *
     * class name is like `M20180101001010101010_Migration`.
     */
    internal fun pickUpVersionFromClassName(clazz: Class<out Any>): String {
        return pickUpVersionFromClassName(clazz.name)
    }

    /**
     * Pick up version string from class name
     *
     * class name is like `M20180101001010101010nMigration`.
     */
    internal fun pickUpVersionFromClassName(name: String): String {
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
    internal fun composeNewMigrationName(migrationName: String): String {
        val version = createNewVersionNumber()
        return migrationHeadString + version + migrationSuffixString + migrationName
    }

    internal fun filterClassCandidateWithVersion(
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
