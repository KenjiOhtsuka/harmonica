package com.improve_future.harmonica.plugin

import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.nio.file.Paths

@DisableCachingByDefault(because = "Migration tasks execute user-provided scripts")
open class MigrationCreate : AbstractTask() {
    private val migrationName: String
        get() {
            if (project.hasProperty("migrationName"))
                return project.findProperty("migrationName") as String
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