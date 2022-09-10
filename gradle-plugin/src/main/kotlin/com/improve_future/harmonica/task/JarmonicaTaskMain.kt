/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.task

import com.improve_future.harmonica.config.PluginConfig
import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.VersionService
import org.reflections.Reflections

abstract class JarmonicaTaskMain {
    private val migrationTableName: String = "harmonica_migration"
    protected val versionService: VersionService
    private var classLoader: ClassLoader

    init {
        versionService = VersionService(migrationTableName)
        classLoader = ClassLoader.getSystemClassLoader()
    }

    protected fun createConnection(
        packageName: String, env: String
    ): Connection {
        return Connection(loadDbConfig(packageName, env), PluginConfig.hasExposed())
    }

    protected fun findMigrationClassList(packageName: String): List<Class<out AbstractMigration>> {
        val reflections = Reflections(packageName)
        val classList = reflections.getSubTypesOf(AbstractMigration::class.java)
        return classList.toList().sortedBy { it.name }
    }

    private fun loadDbConfig(
        packageName: String, env: String = "Default"
    ): DbConfig {
        val reflections = Reflections(packageName)
        val classList = reflections.getSubTypesOf(DbConfig::class.java)
        classList.forEach {
            if (it.simpleName == env) {
                return try {
                    // for Class inherits DbConfig
                    it.getConstructor().newInstance()
                } catch (e: Exception) {
                    // for Object inherits DbConfig
                    it.getDeclaredField("INSTANCE").get(it) as DbConfig
                }
            }
        }
        throw Exception("no config was found.")
    }
}