package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

open class HarmonicaPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.run {
            create("harmonicaUp", MigrationUpTask::class.java) {
                it.group = PluginConfig.groupName
            }

            create("harmonicaCreate", MigrationCreate::class.java) {
                it.group = PluginConfig.groupName
            }

            create("harmonicaDown", MigrationDownTask::class.java) {
                it.group = PluginConfig.groupName
            }
        }
    }
}