package com.imp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class HarmonicaPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("harmonica", HarmonicaTask::class.java).apply {

        }
    }
}

open class HarmonicaTask: DefaultTask() {
    @TaskAction
    fun migrate() {
        println("YEAH!")
    }
}
