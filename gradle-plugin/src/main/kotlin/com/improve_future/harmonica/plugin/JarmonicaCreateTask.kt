/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.plugin

open class JarmonicaCreateTask : JarmonicaMigrationTask() {
    override val taskType = JarmonicaTaskType.Create

    override fun exec() {
        val migrationName =
                if (project.hasProperty("migrationName"))
                    project.properties["migrationName"] as String
                else "Migration"

        jvmArgs = listOf<String>()
        args = buildJarmonicaArgument(migrationName).toList()
        super.exec()
    }
}