package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.DbConfig
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager

abstract class AbstractHarmonicaTask : AbstractTask() {
    companion object {
        val engine: KotlinJsr223JvmLocalScriptEngine by lazy {
            ScriptEngineManager().getEngineByName("kotlin") as
                    KotlinJsr223JvmLocalScriptEngine
        }
    }

    private fun findConfigFile(): File {
        return Paths.get(directoryPath, "config", "$env.kts").toFile()
    }

    fun loadConfigFile(): DbConfig {
        return engine.eval(findConfigFile().readText()) as DbConfig
    }
}