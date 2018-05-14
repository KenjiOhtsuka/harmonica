package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.DbConfig
import org.gradle.api.DefaultTask
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager

abstract class AbstractTask: DefaultTask() {
    private val directoryPath: String
    get() {
        if (project.extensions.extraProperties.has("directoryPath"))
            return project.extensions.extraProperties["directoryPath"] as String
        return "src/main/kotlin/db"
    }

    private val env: String
    get() {
        if (project.extensions.extraProperties.has("env"))
            return project.extensions.extraProperties["env"] as String
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