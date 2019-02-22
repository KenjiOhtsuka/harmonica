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
 * along with Harmonica.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.plugin

import org.gradle.api.tasks.JavaExec

abstract class JarmonicaMigrationTask : JavaExec() {
    internal abstract val taskType: JarmonicaTaskType

    protected val migrationPackage: String
        get() {
            if (project.extensions.extraProperties.has("migrationPackage"))
                return project.extensions.extraProperties["migrationPackage"] as String
            if (project.extensions.extraProperties.has("directoryPath"))
                return directoryPath
                    .replace(Regex("^src/main/(kotlin|java)/"), "")
                    .replace("/", ".")
            return "db"
            //project.group.toString() + "." + project.name //+ ".db"
        }

    private val directoryPath: String
        get() {
            if (project.extensions.extraProperties.has("directoryPath"))
                return project.extensions.extraProperties["directoryPath"] as String
            if (project.extensions.extraProperties.has("migrationPackage"))
                return "src/main/kotlin/" + migrationPackage.replace(".", "/")
            return "src/main/kotlin/db"
        }

    private val env: String
        get() {
            if (project.extensions.extraProperties.has("env"))
                return project.extensions.extraProperties["env"] as String
            return getProperty("env") ?: "Default"
        }

    protected fun buildJarmonicaArgument(
        vararg args: String
    ): JarmonicaArgument {
        return JarmonicaArgument().also {
            it.migrationDirectory = directoryPath
            it.migrationPackage = migrationPackage
            it.env = env
            it.taskType = taskType
            args.forEach { arg -> it.add(arg) }
        }
    }

    protected fun getProperty(name: String): String? {
        return if (project.hasProperty(name))
            project.properties[name] as String
        else
            null
    }
}
