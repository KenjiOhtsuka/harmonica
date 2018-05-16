package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import org.gradle.api.tasks.TaskAction
import java.io.File

open class MigrationDownTask: AbstractMigrationTask() {
    @TaskAction
    fun migrateDown() {
        val connection = createConnection()
        try {
            val migrationVersion = findCurrentMigrationVersion(connection)
            val fileCandidateArray: Array<out File> = findMigrationDir().
                    listFiles { _, name -> name.startsWith(migrationVersion) }
            if (fileCandidateArray.isEmpty() || 1 < fileCandidateArray.size)
                throw Error("More then one files exist for migration $migrationVersion")
            val file = fileCandidateArray.first()

            println("== [Start] Migrate down $migrationVersion ==")
            transaction {
                val migration: AbstractMigration =
                        readMigration(file.readText())
                migration.down()
                removeVersion(connection, migrationVersion)
            }
            connection.close()
                println("== [End] Migrate down $migrationVersion ==")
        } catch (e: Exception) {
            connection.close()
            throw e
        }
    }
}