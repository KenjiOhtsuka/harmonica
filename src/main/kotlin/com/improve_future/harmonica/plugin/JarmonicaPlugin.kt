package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.DbConfig
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.classloader.ClasspathUtil.getClasspath
import com.sun.javafx.scene.CameraHelper.project
import groovy.lang.Closure
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.codehaus.groovy.vmplugin.VMPluginFactory.getPlugin
import org.gradle.api.tasks.JavaExec
import com.sun.javafx.scene.CameraHelper.project
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager

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
        val task: JarmonicaUpTask = project.tasks.create("jarmonicaUp", JarmonicaUpTask::class.java)
            task.description = "Runs this project as a Spring Boot application."
            task.group = ApplicationPlugin.APPLICATION_GROUP
            task.classpath(javaConvention.sourceSets
                    .findByName(SourceSet.MAIN_SOURCE_SET_NAME)!!.runtimeClasspath)
            task.conventionMapping.map(
                    "jvmArgs",
                    groovyClosure {
                        if (project.hasProperty("applicationDefaultJvmArgs"))
                            project.property("applicationDefaultJvmArgs")
                        else java.util.Collections.emptyList<Any>()
                    }
            )
            task.conventionMapping("main", { "com.improve_future.harmonica.Main" })
            //conventionMapping("main",
            //        MainClassConvention(project, ???({ run.getClasspath() })))
        }
    }



open class JarmonicaUpTask : JavaExec() {
    private val migrationPackage: String
        get() {
            if (project.extensions.extraProperties.has("migrationPackage"))
                return project.extensions.extraProperties["migrationPackage"] as String
            return "db"
        }

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
        args = listOf(migrationPackage)
        println("-- print main --")
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