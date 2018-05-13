package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class MigrationUpTask: AbstractMigrationTask() {
    @TaskAction
    fun migrateUp() {
        try {
            createConnection().use { connection ->
                setupHarmonicaMigrationTable(connection)

                for (file in findMigrationDir().listFiles().sortedBy { it.name }) {
                    val migrationVersion: String = file.name.split('_')[0]
                    if (doesVersionMigrated(connection, migrationVersion)) continue

                    println("== [Start] Migrate up $migrationVersion ==")
                    connection.transaction {
                        val migration: AbstractMigration =
                                execKotlin(file.readText())
                        migration.connection = connection
                        migration.up()
                        saveVersion(this, migrationVersion)
                    }
                    println("== [End] Migrate up $migrationVersion ==")
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }
}