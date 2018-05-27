package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import org.gradle.api.tasks.TaskAction
import org.jetbrains.exposed.sql.transactions.DEFAULT_ISOLATION_LEVEL
import java.sql.DriverManager

open class MigrationUpTask: AbstractMigrationTask() {
    @TaskAction
    fun migrateUp() {
        com.improve_future.harmonica.Main
        println("jarmonica up")
        val connection = createConnection()
        try {
            connection.transaction {
                setupHarmonicaMigrationTable(connection)
            }
            for (file in findMigrationDir().listFiles().sortedBy { it.name }) {
                val migrationVersion: String = file.name.split('_')[0]
                if (doesVersionMigrated(connection, migrationVersion)) continue

                println("== [Start] Migrate up $migrationVersion ==")
                connection.transaction {
                    val migration: AbstractMigration =
                            readMigration(file.readText())
                    migration.connection = connection
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