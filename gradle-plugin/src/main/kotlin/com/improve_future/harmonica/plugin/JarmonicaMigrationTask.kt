/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.plugin

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec

abstract class JarmonicaMigrationTask : JavaExec() {
    @get:Internal
    internal abstract val taskType: JarmonicaTaskType

    protected val migrationPackage: String
        @Internal
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

    private val tableNamePluralization: Boolean
        get() {
            project.extensions.extraProperties.let {
                if (it.has("tableNamePluralization"))
                    return it["tableNamePluralization"] as Boolean
            }
            return false
        }

    /**
     * True when the migration should show SQL queries.
     *
     * True when sql option is "review", blank, "true" or null
     */
    private val dispSql: Boolean
        get() {
            project.extensions.extraProperties.let {
                if (it.has("sql")) {
                    val sqlOption = it["sql"] as? String?
                    if (sqlOption.isNullOrBlank()) return true
                    if (sqlOption == "review") return true
                    return sqlOption.toBoolean()
                }
                return false
            }
        }

    /**
     * True when the migration is in review mode.
     *
     * True only when sql option is "review".
     */
    private val isReview: Boolean
        get() {
            project.extensions.extraProperties.let {
                if (it.has("sql")) {
                    val sqlOption = it["sql"] as? String?
                    if (sqlOption.isNullOrBlank()) return false
                    return sqlOption == "review"
                }
                return false
            }
        }

    protected fun buildJarmonicaArgument(
        vararg args: String
    ): JarmonicaArgument {
//        println(directoryPath)
//        println(project.displayName)
//        println(project.rootProject.name)
//        println(
//            directoryPath
//                .replace(Regex("^src/main/(kotlin|java)/"), "")
//                .replace("/", ".")
//        )
        return JarmonicaArgument().also {
            it.migrationDirectory = directoryPath
            it.migrationPackage = migrationPackage
            it.env = env
            it.taskType = taskType
            it.tableNamePluralization = tableNamePluralization
            it.dispSql = dispSql
            it.isReview = isReview
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
