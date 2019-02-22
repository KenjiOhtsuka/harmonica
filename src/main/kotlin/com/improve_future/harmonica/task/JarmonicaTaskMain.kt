/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.task

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.service.VersionService
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
        return Connection(loadDbConfig(packageName, env))
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
                    // for Class inherit DbConfig
                    it.getConstructor().newInstance()
                } catch (e: Exception) {
                    // for Object inherit DbConfig
                    it.getDeclaredField("INSTANCE").get(it) as DbConfig
                }
            }
        }
        throw Exception("no config was found.")
    }
}