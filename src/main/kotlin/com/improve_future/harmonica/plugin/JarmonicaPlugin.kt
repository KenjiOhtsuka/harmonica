package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.lang.Closure
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec

internal fun <T> T.groovyClosure(function: () -> Unit) = object : Closure<Unit>(this) {
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

        fun <T : JavaExec> createTaskBase(name: String, task: Class<T>): JavaExec {
            return project.tasks.create(name, task).apply {
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
            }
        }
        createTaskBase("jarmonicaUp", JarmonicaUpTask::class.java).run {
            description = "Compile and migrate up."
            conventionMapping("main", { "com.improve_future.harmonica.task.JarmonicaUpMain" })
        }
        createTaskBase("jarmonicaDown", JarmonicaDownTask::class.java).run {
            description = "Compile and migrate down."
            conventionMapping("main", { "com.improve_future.harmonica.task.JarmonicaDownMain" })
        }
        createTaskBase("jarmonicaCreate", JarmonicaCreateTask::class.java).run {
            description = "Create migrate file."
            conventionMapping("main", { "com.improve_future.harmonica.task.JarmonicaCreateMain" })
        }
        //conventionMapping("main",
        //        MainClassConvention(project, ???({ run.getClasspath() })))
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
