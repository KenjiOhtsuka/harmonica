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

package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import org.gradle.api.tasks.TaskAction
import java.io.File

open class MigrationDownTask : AbstractMigrationTask() {
    @TaskAction
    fun migrateDown() {
        val connection = createConnection()
        try {
            val migrationVersion = versionService.findCurrentMigrationVersion(connection)
            val fileCandidateArray: Array<out File> =
                findMigrationDir().listFiles { _, name -> name.startsWith(migrationVersion) }
            if (fileCandidateArray.isEmpty())
                return
            if (1 < fileCandidateArray.size)
                throw Error("More then one files exist for migration $migrationVersion")
            val file = fileCandidateArray.first()

            println("== [Start] Migrate down $migrationVersion ==")
            connection.transaction {
                val migration: AbstractMigration =
                    readMigration(file.readText())
                migration.connection = connection
                migration.down()
                versionService.removeVersion(connection, migrationVersion)
            }
            println("== [End] Migrate down $migrationVersion ==")
            connection.close()
        } catch (e: Exception) {
            connection.close()
            throw e
        }
    }
}