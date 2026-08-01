package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.config.PluginConfig
import com.improve_future.harmonica.core.*
import org.gradle.api.tasks.Input
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@DisableCachingByDefault(because = "Migration tasks execute user-provided scripts")
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
        val engine: ScriptEngine by lazy {
            ScriptEngineManager().getEngineByName("kotlin")
                ?: error("Kotlin script engine not found on the classpath")
        }

        protected fun removePackageStatement(script: String) =
            script.replace(Regex("^\\s*package\\s+.+"), "")
    }
}
