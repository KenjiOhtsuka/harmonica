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

package com.improve_future.harmonica.task

import java.nio.file.Paths

object JarmonicaCreateMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val migrationPackage = args[0]
        val migrationDirectory = args[2]
        val env = args[3]
        val migrationName = args[4]
        val migrationClassName = versionService.composeNewMigrationName(migrationName)

        val migrationFile = Paths.get(
            Paths.get(migrationDirectory, "migration").toFile().absolutePath,
            "$migrationClassName.kt"
        ).toFile()
        migrationFile.parentFile.mkdirs()
        migrationFile.createNewFile()
        migrationFile.writeText(
            """package ${args[0]}.migration

import AbstractMigration

/**
 * $migrationName
 */
class $migrationClassName : AbstractMigration() {
    override fun up() {
        createTable("table_name") {
            integer("integer_column", default = 8)
            varchar("varchar_column", size = 10, nullable = false)
            decimal("decimal_column", precision = 5, scale = 2)
            text("text_column", default = "default value")
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