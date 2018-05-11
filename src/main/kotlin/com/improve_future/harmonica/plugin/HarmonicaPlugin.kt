package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.Paths
import javax.script.ScriptEngineManager
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase
import java.io.File
import kotlin.reflect.KClass

open class HarmonicaPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("harmonica", HarmonicaTask::class.java).apply {

        }
    }
}

open class HarmonicaTask: DefaultTask() {
    @Input
    var migrationDirPath: String = ""
    @Input
    var host: String = ""
    @Input
    var dbName: String = ""
    @Input
    var username: String = ""
    @Input
    var password: String = ""

    companion object {
        val engine: KotlinJsr223JvmLocalScriptEngine =
                ScriptEngineManager().getEngineByName("kotlin")
                        as KotlinJsr223JvmLocalScriptEngine
    }

    private fun exec(script: String): AbstractMigration {
        return engine.eval(script) as AbstractMigration
    }

    @TaskAction
    fun migrate() {
        // read db config file
        // /db/Config.kts
        // create connection
        val connection = Connection.create {
            dbms = Dbms.PostgreSQL
            host = this@HarmonicaTask.host
            dbName = this@HarmonicaTask.dbName
            user = username
            password = this@HarmonicaTask.password
        }
        // fetch files
        // migration/*.kts

//        val walker = Files.walk(Paths.get(
//                project.projectDir.path + "/" + migrationDirPath))
        val dirPath = Paths.get(project.projectDir.path, migrationDirPath)
        try {
            for (it in dirPath.toFile().listFiles()) {
                println(it.absolutePath)
                // start transaction
                connection.transaction {
                    // execute migration
                    val loadedObj: AbstractMigration =
                            exec(it.readText())
                    println(loadedObj.up())
                }
            }
        } catch (e: Exception) {
            println(e.message)
            e.stackTrace.forEach {
                println(
                        "         " +
                        it.fileName + ":" +
                        it.lineNumber + ":" +
                                it.methodName

                )
            }
        } finally {
        }
        // update harmonica_migrations
    }
}
