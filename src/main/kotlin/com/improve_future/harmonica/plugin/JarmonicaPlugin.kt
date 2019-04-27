/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.lang.Closure
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec
import kotlin.reflect.KClass

internal fun <T> T.groovyClosure(function: () -> Unit) =
    object : Closure<Unit>(this) {
        @Suppress("unused")
        fun doCall() {
            function()
        }
    }

class JarmonicaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // register plugin action
        val javaConvention = project.convention
            .getPlugin(JavaPluginConvention::class.java)

        fun <T : JavaExec> addTask(
            kTask: KClass<T>, description: String
        ): JavaExec {
            val task = kTask.java
            val taskName = task.simpleName.removeSuffix("Task").run {
                this[0].toLowerCase() + this.substring(1)
            }
            val mainClassName = task.simpleName.removeSuffix("Task") + "Main"

            return project.tasks.create(taskName, task).apply {
                group = PluginConfig.groupName
                classpath(
                    javaConvention.sourceSets
                        .findByName(SourceSet.MAIN_SOURCE_SET_NAME)!!.runtimeClasspath
                )
                conventionMapping.map(
                    "jvmArgs",
                    groovyClosure {
                        if (project.hasProperty("applicationDefaultJvmArgs"))
                            project.property("applicationDefaultJvmArgs")
                        else java.util.Collections.emptyList<Any>()
                    }
                )
                this.description = description
                conventionMapping("main") { "com.improve_future.harmonica.task.$mainClassName" }
            }
        }

        addTask(
            JarmonicaUpTask::class, "Compile and migrate up."
        )
        addTask(
            JarmonicaDownTask::class, "Compile and migrate down."
        )
        addTask(
            JarmonicaCreateTask::class, "Create a migration file."
        )
        addTask(
            JarmonicaVersionTask::class, "Show current migration version."
        )

    }
}

open class JarmonicaUpTask : JarmonicaMigrationTask() {
    override val taskType = JarmonicaTaskType.Up

    override fun exec() {
        val step = getProperty("step")?.toLong()

        jvmArgs = listOf<String>()
        args = buildJarmonicaArgument(
            step.toString()
        ).toList()
        super.exec()
    }
}

open class JarmonicaDownTask : JarmonicaMigrationTask() {
    override val taskType: JarmonicaTaskType = JarmonicaTaskType.Down

    override fun exec() {
        val step = getProperty("step")?.toLong() ?: 1

        jvmArgs = listOf<String>()
        args = buildJarmonicaArgument(
            step.toString()
        ).toList()
        super.exec()
    }
}

open class JarmonicaVersionTask : JarmonicaMigrationTask() {
    override val taskType = JarmonicaTaskType.Version

    override fun exec() {
        jvmArgs = listOf()
        args = buildJarmonicaArgument().toList()
        super.exec()
    }
}
