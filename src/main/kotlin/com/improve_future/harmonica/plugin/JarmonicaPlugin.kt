package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.lang.Closure
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec

fun <T> T.groovyClosure(function: () -> Unit) = object : Closure<Unit>(this) {
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
        fun <T:JavaExec> createTaskBase(name: String, task: Class<T>): JavaExec {
            return project.tasks.create(name, task).apply {
                group = PluginConfig.groupName
                classpath(javaConvention.sourceSets
                        .findByName(SourceSet.MAIN_SOURCE_SET_NAME)!!.runtimeClasspath)
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

abstract class JarmonicaMigrationTask : JavaExec() {
    abstract val taskType: JarmonicaTaskType

    protected val migrationPackage: String
        get() {
            if (project.extensions.extraProperties.has("migrationPackage"))
                return project.extensions.extraProperties["migrationPackage"] as String
            if (project.extensions.extraProperties.has("directoryPath"))
                return directoryPath.removePrefix("src/main/kotlin/").
                        replace("/", ".")
            return "db"
        }

    protected val directoryPath: String
        get() {
            if (project.extensions.extraProperties.has("directoryPath"))
                return project.extensions.extraProperties["directoryPath"] as String
            if (project.extensions.extraProperties.has("migrationPackage"))
                "src/main/kotlin/" + migrationPackage.replace(".", "/")
            return "src/main/kotlin/db"
        }

    protected val env: String
        get() {
            if (project.extensions.extraProperties.has("env"))
                return project.extensions.extraProperties["env"] as String
            if (project.hasProperty("env"))
                return project.properties["env"] as String
            return "Default"
        }

    protected fun buildJarmonicaArgument(
            vararg args: String): JarmonicaArgument {
        return JarmonicaArgument().also {
            it.migrationDirectory = directoryPath
            it.migrationPackage = migrationPackage
            it.env = env
            it.taskType = taskType
            args.forEach { arg -> it.add(arg) }
        }
    }
}

open class JarmonicaUpTask : JarmonicaMigrationTask() {
    override val taskType = JarmonicaTaskType.Up
//    private val env: String
//        get() {
//            if (project.extensions.extraProperties.has("env"))
//                return project.extensions.extraProperties["env"] as String
//            if (project.hasProperty("env"))
//                return project.properties["env"] as String
//            return "default"
//        }

//    companion object {
//        val engine: KotlinJsr223JvmLocalScriptEngine =
//                ScriptEngineManager().getEngineByName("kotlin")
//                        as KotlinJsr223JvmLocalScriptEngine
//    }
//
//    private fun findConfigFile(): File {
//        return File("test") //Paths.get(migrationPackage, "config", "$env").toFile()
//    }
//
//    fun loadConfigFile(): DbConfig {
//        return engine.eval(findConfigFile().readText()) as DbConfig
//    }
//
//    fun findMigrationDir(): File {
//        return Paths.get(migrationPackage, "migration").toFile()
//    }

    override fun exec() {
        jvmArgs = listOf<String>()
        args = buildJarmonicaArgument().toList()
        super.exec()
    }
}

open class JarmonicaDownTask : JarmonicaMigrationTask() {
    override val taskType: JarmonicaTaskType = JarmonicaTaskType.Down

    override fun exec() {
        jvmArgs = listOf<String>()
        args = buildJarmonicaArgument().toList()
        super.exec()
    }
}

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

enum class JarmonicaTaskType {
    Up,
    Down,
    Create
}