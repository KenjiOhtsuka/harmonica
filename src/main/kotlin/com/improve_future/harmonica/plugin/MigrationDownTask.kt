package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import org.gradle.api.tasks.TaskAction
import java.io.File

open class MigrationDownTask: AbstractMigrationTask() {
    @TaskAction
    fun migrateDown() {
        try {
            createConnection().use { connection ->
                val migrationVersion = findCurrentMigrationVersion(connection)
                val fileCandidateArray: Array<out File> = findMigrationDir().
                        listFiles { dir, name -> name.startsWith(migrationVersion) }
                if (fileCandidateArray.isEmpty() || 1 < fileCandidateArray.size)
                    throw Error("More then one files exist for migration $migrationVersion")
                val file = fileCandidateArray.first()

                println("== [Start] Migrate down $migrationVersion ==")
                connection.transaction {
                    val migration: AbstractMigration =
                            execKotlin(file.readText())
                    migration.connection = connection
                    migration.down()
                    removeVersion(this, migrationVersion)
                }
                println("== [End] Migrate down $migrationVersion ==")
            }
        } catch (e: Exception) {
            println(e.message)
            e.stackTrace.forEach {
                println(
                        "         " +
                                it.fileName + " : " +
                                it.lineNumber + " : " +
                                it.methodName

                )
            }
            throw e
        }

    }
}