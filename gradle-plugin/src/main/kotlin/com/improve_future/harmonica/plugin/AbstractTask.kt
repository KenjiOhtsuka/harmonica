/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
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