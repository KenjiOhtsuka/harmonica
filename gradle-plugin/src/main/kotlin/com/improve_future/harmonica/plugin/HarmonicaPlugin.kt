package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

open class HarmonicaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val scriptClasspath = project.configurations.create("harmonica") {
            it.isCanBeConsumed = false
            it.isCanBeResolved = true
        }

        fun registerMigrationTask(
            name: String, description: String, type: Class<out AbstractMigrationTask>
        ) {
            project.tasks.register(name, type) { task ->
                task.group = PluginConfig.groupName
                task.description = description
                task.scriptClasspath.from(scriptClasspath)
            }
        }

        registerMigrationTask(
            "harmonicaUp", "Migrate up.", MigrationUpTask::class.java
        )
        registerMigrationTask(
            "harmonicaDown", "Migrate down.", MigrationDownTask::class.java
        )

        project.tasks.register("harmonicaCreate", MigrationCreate::class.java) { task ->
            task.group = PluginConfig.groupName
            task.description = "Create migration file."
        }
    }
}
