package com.improve_future.harmonica.plugin

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.lang.Closure
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec

fun <T> T.groovyClosure(function: () -> Unit) = object : Closure<Unit>(this) {
    @Suppress("unused")
    fun doCall() {
        function()
    }
}

class JarmonicaPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // register plugin action
        registerPluginAction(target)
    }

    private fun registerPluginAction(target: Project) {
        val actionList = listOf(JarmonicaPluginAction())
        actionList.forEach { it.execute(target) }
    }
}

class JarmonicaPluginAction : Action<Project> {
    override fun execute(project: Project) {
        val javaConvention = project.convention
                .getPlugin(JavaPluginConvention::class.java)
        fun <T:JavaExec> createTaskBase(name: String, task: Class<T>): JavaExec {
            return project.tasks.create(name, task).apply {
                // ToDo: change to Harmonica
                group = ApplicationPlugin.APPLICATION_GROUP
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
        createTaskBase("jarmoncaUp", JarmonicaUpTask::class.java).run {
            description = "Compile and migrate up."
            conventionMapping("main", { "com.improve_future.harmonica.Main" })
        }
        createTaskBase("jarmonicaDown", JarmonicaDownTask::class.java).run {
            description = "Compile and migrate down."
            conventionMapping("main", { "com.improve_future.harmonica.Main" })
        }
        createTaskBase("jarmonicaCreate", JarmonicaCreateTask::class.java).run {
            description = "Create migrate file."
            conventionMapping("main", { "com.improve_future.harmonica.Main" })
        }
            //conventionMapping("main",
            //        MainClassConvention(project, ???({ run.getClasspath() })))
    }
}

abstract class JarmonicaMigrationTask : JavaExec() {
    protected val migrationPackage: String
        get() {
            if (project.extensions.extraProperties.has("migrationPackage"))
                return project.extensions.extraProperties["migrationPackage"] as String
            return "db"
        }
}

open class JarmonicaUpTask : JarmonicaMigrationTask() {
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
        jvmArgs = listOf<String>()//migrationPackage)
        args = listOf(migrationPackage, JarmonicaTaskType.Up.toString())
        println(main)
        super.exec()
    }
}

open class JarmonicaDownTask : JarmonicaMigrationTask() {
    override fun exec() {
        jvmArgs = listOf<String>()//migrationPackage)
        args = listOf(migrationPackage, JarmonicaTaskType.Down.toString())
        println(main)
        super.exec()
    }
}

open class JarmonicaCreateTask : JarmonicaMigrationTask() {
    override fun exec() {
        jvmArgs = listOf<String>()//migrationPackage)
        args = listOf(migrationPackage, JarmonicaTaskType.Create.toString())
        println(main)
        super.exec()
    }
}

class JarmonicaArgument() {
    lateinit var migrationPackage: String

    fun toArray() {
        arrayOf(migrationPackage)
    }
}

enum class JarmonicaTaskType {
    Up,
    Down,
    Create
}