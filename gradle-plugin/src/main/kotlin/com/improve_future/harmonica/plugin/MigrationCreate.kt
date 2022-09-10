/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.plugin

import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths

open class MigrationCreate : AbstractTask() {
    private val migrationName: String
        get() {
            if (project.hasProperty("migrationName"))
                return project.properties["migrationName"] as String
            return "Migration"
        }

    @TaskAction
    fun createMigration() {
        val migrationFile = Paths.get(
            findMigrationDir().absolutePath,
            versionService.composeNewMigrationName(migrationName) + ".kts"
        ).toFile()
        migrationFile.parentFile.mkdirs()
        migrationFile.createNewFile()
        migrationFile.writeText(
            """import com.improve_future.harmonica.core.AbstractMigration

/**
 * $migrationName
 */
object : AbstractMigration() {
    override fun up() {
        createTable("table_name") {
            integer("column_1")
            varchar("column_2")
        }
    }

    override fun down() {
        dropTable("table_name")
    }
}"""
        )
        println("Created ${migrationFile.absolutePath}")
    }
}