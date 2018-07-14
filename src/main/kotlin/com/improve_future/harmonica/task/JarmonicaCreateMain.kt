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

import com.improve_future.harmonica.core.AbstractMigration

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