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

import com.improve_future.harmonica.service.VersionService
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

open class MigrationCreate : AbstractTask() {
    private val migrationName: String
        get() {
            if (project.hasProperty("migrationName"))
                return project.properties["migrationName"] as String
            return "Migration"
        }

    private val versionService = VersionService("harmonica_migration")

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