package com.improve_future.harmonica

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.plugin.AbstractMigrationTask
import com.improve_future.harmonica.service.VersionService
import org.reflections.Reflections
import java.net.URLClassLoader
import java.sql.ResultSet

abstract class A {
    val a = 1
}

abstract class B {
    val b = 1
}

class Main {
    companion object: AbstractMigrationTask() {
        private const val migrationTableName: String = "harmonica_migration"
        private val versionService: VersionService
        private var classLoader: ClassLoader

        init {
            versionService = VersionService(migrationTableName)
            classLoader = ClassLoader.getSystemClassLoader()
        }

        private fun createConnection(packageName: String): Connection {
            return Connection(loadDbConfig(packageName))
        }

        @JvmStatic
        fun main(vararg args: String) {
            println("start main method")
            val migrationPackage = args[0]

//        (classLoader as URLClassLoader).urLs.forEach {
//            println(it)
//        }

            val connection = createConnection(migrationPackage)
            try {
                connection.transaction {
                    setupHarmonicaMigrationTable(connection)
                }
                for (clazz in findMigrationClassList(migrationPackage)) {
                    val migrationVersion: String = clazz.name.substring(clazz.name.lastIndexOf('_') + 1)
                    if (doesVersionMigrated(connection, migrationVersion)) continue

                    println("== [Start] Migrate up $migrationVersion ==")
                    connection.transaction {
                        val migration = clazz.newInstance()
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

        fun findMigrationClassList(packageName: String): List<Class<out AbstractMigration>> {
            val reflections = Reflections(packageName)
            val classList = reflections.getSubTypesOf(AbstractMigration::class.java)
            return classList.toList().sortedBy { it.name }
        }

        fun loadDbConfig(packageName: String, env: String = "Default"): DbConfig {
            val reflections = Reflections(packageName)
            val classList = reflections.getSubTypesOf(DbConfig::class.java)
            classList.forEach {
                if (it.simpleName == env) return it.newInstance()
            }
            throw Exception("no config was found.")
        }
    }
}