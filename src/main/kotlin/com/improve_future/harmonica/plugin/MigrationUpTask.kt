package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class MigrationUpTask: AbstractMigrationTask() {
    @TaskAction
    fun migrateUp() {
        val connection = createConnection()
        try {
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
            connection.close()
        } catch (e: Exception) {
            connection.close()
            throw e
        }
    }
}