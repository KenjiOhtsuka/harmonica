package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

open class HarmonicaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("harmonicaUp", MigrationUpTask::class.java) { task ->
            task.group = PluginConfig.groupName
            task.description = "Migrate up."
        }

        project.tasks.register("harmonicaCreate", MigrationCreate::class.java) { task ->
            task.group = PluginConfig.groupName
            task.description = "Migrate down."
        }

        project.tasks.register("harmonicaDown", MigrationDownTask::class.java) { task ->
            task.group = PluginConfig.groupName
            task.description = "Create migration file."
        }
    }
}
