package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Migration
import org.gradle.api.tasks.TaskAction
import org.jetbrains.exposed.sql.transactions.DEFAULT_ISOLATION_LEVEL

open class MigrationUpTask: AbstractMigrationTask() {
    @TaskAction
    fun migrateUp() {
        val connection = createConnection()
        try {
            transaction {
                setupHarmonicaMigrationTable(connection)
            }
            for (file in findMigrationDir().listFiles().sortedBy { it.name }) {
                val migrationVersion: String = file.name.split('_')[0]
                if (doesVersionMigrated(connection, migrationVersion)) continue

                println("== [Start] Migrate up $migrationVersion ==")
                transaction {
                    val migration: AbstractMigration =
                            readMigration(file.readText())
                    migration.up()
                    saveVersion(connection, migrationVersion)
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