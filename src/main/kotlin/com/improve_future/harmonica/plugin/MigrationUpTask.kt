/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import org.gradle.api.tasks.TaskAction

open class MigrationUpTask : AbstractMigrationTask() {
    @TaskAction
    fun migrateUp() {
        val connection = createConnection()
        try {
            connection.transaction {
                versionService.setupHarmonicaMigrationTable(connection)
            }
            for (file in findMigrationDir().listFiles().sortedBy { it.name }) {
                val migrationVersion: String = file.name.split('_')[0]
                if (versionService.isVersionMigrated(connection, migrationVersion)) continue

                println("== [Start] Migrate up $migrationVersion ==")
                connection.transaction {
                    val migration: AbstractMigration =
                        readMigration(file.readText())
                    migration.connection = connection
                    migration.up()
                    versionService.saveVersion(connection, migrationVersion)
                }
                println("== [End] Migrate up $migrationVersion ==")
            }
            connection.close()
        } catch (e: Exception) {
            connection.close()
            throw e
        }
    }
}