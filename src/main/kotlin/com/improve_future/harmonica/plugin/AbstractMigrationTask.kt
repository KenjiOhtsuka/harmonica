/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.plugin

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
        return Connection(loadConfigFile())
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