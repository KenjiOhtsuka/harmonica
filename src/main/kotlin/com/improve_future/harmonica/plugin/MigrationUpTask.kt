package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import org.gradle.api.tasks.TaskAction

open class MigrationUpTask: AbstractMigrationTask() {
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