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