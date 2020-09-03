package com.improve_future.harmonica.core

import com.improve_future.harmonica.service.VersionService
import org.jetbrains.exposed.sql.Database
import org.reflections.Reflections
import kotlin.reflect.full.createInstance

open class Harmonica(private val packageName: String, private val migrationTableName: String = "harmonica_migration") {
    private val versionService: VersionService = VersionService(migrationTableName)

    /**
     * Up all migrations
     */
    fun up(db: Database, applyCount: Int? = null) {
        val connection = ExposedConnection(db = db)
        try {
            connection.transaction {
                versionService.setupHarmonicaMigrationTable(connection)
            }
            val reflections = Reflections(packageName)
            val allClasses = reflections.getSubTypesOf(AbstractMigration::class.java)

            var migrationCount = 0;
            for (clazz in allClasses.sortedBy { it.simpleName }) {
                val migrationVersion: String = versionService.pickUpVersionFromClassName(clazz.simpleName)

                if (applyCount !== null && migrationCount >= applyCount) {
                    break;
                }
                if (versionService.isVersionMigrated(connection, migrationVersion)) continue
                migrationCount++

                println("== [Start] Migrate up $migrationVersion ==")
                connection.transaction {
                    val kClass = clazz.kotlin
                    val migration: AbstractMigration = kClass.createInstance()
                    migration.connection = connection
                    migration.up()
                    versionService.saveVersion(connection, migrationVersion)
                }
                println("== [End] Migrate up $migrationVersion ==")
            }
        } catch (e: Exception) {
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Down latest migrations
     */
    fun down(db: Database) {
        val connection = ExposedConnection(db = db)
        try {
            val migrationVersion = versionService.findCurrentMigrationVersion(connection)

            val reflections = Reflections(packageName)
            val allClasses = reflections.getSubTypesOf(AbstractMigration::class.java)

            if (allClasses.isEmpty())
                return

            val fileCandidateList: List<Class<out AbstractMigration>> =
                allClasses.filter { it.simpleName.startsWith(migrationVersion) }

            if (1 < allClasses.size)
                throw Error("More then one files exist for migration $migrationVersion")

            val clazz = fileCandidateList.first()

            println("== [Start] Migrate down $migrationVersion ==")
            connection.transaction {
                val kClass = clazz.kotlin
                val migration: AbstractMigration = kClass.createInstance()

                migration.connection = connection
                migration.down()
                versionService.removeVersion(connection, migrationVersion)
            }

            println("== [End] Migrate down $migrationVersion ==")

        } catch (e: Exception) {
            throw e
        } finally {
            connection.close()
        }
    }
}