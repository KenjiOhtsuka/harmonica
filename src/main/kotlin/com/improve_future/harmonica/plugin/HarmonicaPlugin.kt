package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths
import javax.script.ScriptEngineManager
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine

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