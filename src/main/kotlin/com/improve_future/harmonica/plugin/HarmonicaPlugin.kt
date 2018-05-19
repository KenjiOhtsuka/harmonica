package com.improve_future.harmonica.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

open class HarmonicaPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.run {
            create("harmonicaUp", MigrationUpTask::class.java) {

            }

            create("harmonicaCreate", MigrationCreate::class.java) {

            }

            create("harmonicaDown", MigrationDownTask::class.java) {

            }
        }
    }
}