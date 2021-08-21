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

import com.improve_future.harmonica.core.VersionService
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import java.io.File
import java.nio.file.Paths

abstract class AbstractTask : DefaultTask() {
    protected val directoryPath: String
        @Internal
        get() {
            if (project.extensions.extraProperties.has("directoryPath"))
                return project.extensions.extraProperties["directoryPath"] as String
            return "src/main/kotlin/db"
        }

    protected val env: String
        @Internal
        get() {
            if (project.extensions.extraProperties.has("env"))
                return project.extensions.extraProperties["env"] as String
            if (project.hasProperty("env"))
                return project.properties["env"] as String
            return "default"
        }

    fun findMigrationDir(): File {
        return Paths.get(directoryPath, "migration").toFile()
    }

    /** The table name to store executed migration version IDs. */
    private val migrationTableName: String = "harmonica_migration"

    @Internal
    protected val versionService: VersionService

    init {
        versionService = VersionService(migrationTableName)
    }
}