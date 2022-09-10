/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

open class HarmonicaPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.run {
            create("harmonicaUp", MigrationUpTask::class.java) {
                it.group = PluginConfig.groupName
                it.description = "Migrate up."
            }

            create("harmonicaCreate", MigrationCreate::class.java) {
                it.group = PluginConfig.groupName
                it.description = "Migrate down."
            }

            create("harmonicaDown", MigrationDownTask::class.java) {
                it.group = PluginConfig.groupName
                it.description = "Create migration file."
            }
        }
    }
}