package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Migration
import org.gradle.api.tasks.TaskAction
import org.jetbrains.exposed.sql.transactions.DEFAULT_ISOLATION_LEVEL
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

open class MigrationUpTask: AbstractMigrationTask() {
    @TaskAction
    fun migrateUp() {
        val connection = createConnection()
        try {
            transaction {
                setupHarmonicaMigrationTable(connection)
                commit()
            }
            for (file in findMigrationDir().listFiles().sortedBy { it.name }) {
                val migrationVersion: String = file.name.split('_')[0]
                if (doesVersionMigrated(connection, migrationVersion)) continue

                println("== [Start] Migrate up $migrationVersion ==")
                val t = TransactionManager.currentOrNew(DEFAULT_ISOLATION_LEVEL)
                t.run {
                    val migration: Migration =
                            readMigration(file.readText())
                    migration.upFun()
                    saveVersion(connection, migrationVersion)
                    commit()
                }
                println("== [End] Migrate up $migrationVersion ==")
            }
            connection.close()
        } catch (e: Exception) {
            println(e.message)
            e.stackTrace.forEach {
                println(it)
            }
            connection.close()
            throw e
        }
    }
}