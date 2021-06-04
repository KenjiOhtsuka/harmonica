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

                if (applyCount !== null && migrationCount >= applyCount)
                    break

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
     * Down all migrations
     */
    fun allDown(db: Database) {
        val connection = ExposedConnection(db = db)
        try {
            val migrationVersionList = versionService.allListMigrationVersion(connection)

            doMigration(migrationVersionList, connection)

        } catch (e: Exception) {
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Down latest migrations
     */
    fun down(db: Database, applyCount: Int = 1) {
        val connection = ExposedConnection(db = db)
        try {
            val migrationVersionList = versionService.findListMigrationVersion(connection, applyCount)
            
            doMigration(migrationVersionList, connection)

        } catch (e: Exception) {
            throw e
        } finally {
            connection.close()
        }
    }

    private fun doMigration(
        migrationVersionList: List<String>,
        connection: ExposedConnection
    ): Boolean {
        val reflections = Reflections(packageName)
        val allClasses = reflections.getSubTypesOf(AbstractMigration::class.java)

        if (allClasses.isEmpty())
            return true

        for (migrationVersion in migrationVersionList) {
            val fileCandidateList: List<Class<out AbstractMigration>> =
                allClasses.filter { it.simpleName.contains(migrationVersion) }

            if (fileCandidateList.isEmpty())
                throw Error("Not found $migrationVersion")

            if (1 < fileCandidateList.size)
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
        }
        return false
    }
}
