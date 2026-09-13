package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.JavaExec
import org.gradle.work.DisableCachingByDefault
import kotlin.reflect.KClass

class JarmonicaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        fun <T : JavaExec> addTask(
            kTask: KClass<T>, description: String
        ): TaskProvider<T> {
            val taskName = kTask.java.simpleName.removeSuffix("Task").run {
                this[0].lowercaseChar() + this.substring(1)
            }
            val mainClassName = kTask.java.simpleName.removeSuffix("Task") + "Main"

            return project.tasks.register(taskName, kTask.java) { task ->
                task.group = PluginConfig.groupName
                task.description = description
                task.mainClass.set("com.improve_future.harmonica.task.$mainClassName")
                task.classpath(
                    project.extensions
                        .getByType(JavaPluginExtension::class.java)
                        .sourceSets
                        .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                        .runtimeClasspath
                )
                val defaultJvmArgs = if (project.hasProperty("applicationDefaultJvmArgs"))
                    project.property("applicationDefaultJvmArgs") as Collection<*>
                else
                    emptyList<Any>()
                task.jvmArgs(defaultJvmArgs)
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

@DisableCachingByDefault(because = "Migration tasks execute user-provided scripts")
abstract class JarmonicaUpTask : JarmonicaMigrationTask() {
    override val taskType = JarmonicaTaskType.Up

    override fun exec() {
        val step = getProperty("step")?.toLong()

        jvmArgs = listOf<String>()
        args = buildJarmonicaArgument(step.toString()).toList()
        super.exec()
    }
}

@DisableCachingByDefault(because = "Migration tasks execute user-provided scripts")
abstract class JarmonicaDownTask : JarmonicaMigrationTask() {
    override val taskType: JarmonicaTaskType = JarmonicaTaskType.Down

    override fun exec() {
        val step = getProperty("step")?.toLong() ?: 1

        jvmArgs = listOf<String>()
        args = buildJarmonicaArgument(step.toString()).toList()
        super.exec()
    }
}

@DisableCachingByDefault(because = "Migration tasks execute user-provided scripts")
abstract class JarmonicaVersionTask : JarmonicaMigrationTask() {
    override val taskType = JarmonicaTaskType.Version

    override fun exec() {
        jvmArgs = listOf()
        args = buildJarmonicaArgument().toList()
        super.exec()
    }
}
