/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import com.improve_future.harmonica.core.*
import org.gradle.api.tasks.Input
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager

abstract class AbstractMigrationTask : AbstractTask() {
    @Input
    var dbms: Dbms = Dbms.PostgreSQL

    protected fun readMigration(script: String): AbstractMigration {
        return engine.eval(removePackageStatement(script)) as AbstractMigration
    }

    private fun findConfigFile(): File {
        return Paths.get(directoryPath, "config", "$env.kts").toFile()
    }

    fun loadConfigFile(): DbConfig {
        return engine.eval(findConfigFile().readText()) as DbConfig
    }

    protected fun createConnection(): Connection {
        return Connection(loadConfigFile(), PluginConfig.hasExposed())
    }

    protected companion object {
        val engine: KotlinJsr223JvmLocalScriptEngine by lazy {
            ScriptEngineManager().getEngineByName("kotlin") as
                KotlinJsr223JvmLocalScriptEngine
        }

        protected fun removePackageStatement(script: String) =
            script.replace(Regex("^\\s*package\\s+.+"), "")
    }
}
