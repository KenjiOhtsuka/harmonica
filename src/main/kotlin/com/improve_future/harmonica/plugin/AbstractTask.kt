package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.DbConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager

abstract class AbstractTask: DefaultTask() {
    companion object {
        val engine: KotlinJsr223JvmLocalScriptEngine =
                ScriptEngineManager().getEngineByName("kotlin")
                        as KotlinJsr223JvmLocalScriptEngine
    }

    @Input
    var directoryPath: String = "src/main/kotlin/db"
    @Input
    var env: String = "default"

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