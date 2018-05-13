package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.DbConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager

abstract class AbstractTask: DefaultTask() {
    @Input
    open var directoryPath: String = "src/main/kotlin/db"

    val env: String
    get() {
        if (project.hasProperty("env"))
            return project.properties["env"] as String
        return "default"
    }


    companion object {
        val engine: KotlinJsr223JvmLocalScriptEngine =
                ScriptEngineManager().getEngineByName("kotlin")
                        as KotlinJsr223JvmLocalScriptEngine
    }

    private fun findConfigFile(): File {
        return Paths.get(directoryPath, "config", "$env.kts").toFile()
    }

    fun loadConfigFile(): DbConfig {
        return engine.eval(findConfigFile().readText()) as DbConfig
    }

    fun findMigrationDir(): File {
        return Paths.get(directoryPath, "migration").toFile()
    }
}