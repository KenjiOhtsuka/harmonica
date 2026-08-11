package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.*
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Paths
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@DisableCachingByDefault(because = "Migration tasks execute user-provided scripts")
abstract class AbstractMigrationTask : AbstractTask() {
    @Input
    var dbms: Dbms = Dbms.PostgreSQL

    @get:Classpath
    abstract val scriptClasspath: ConfigurableFileCollection

    protected fun readMigration(script: String): AbstractMigration {
        return evaluate(removePackageStatement(script)) as AbstractMigration
    }

    private fun findConfigFile(): File {
        return Paths.get(directoryPath, "config", "$env.kts").toFile()
    }

    fun loadConfigFile(): DbConfig {
        return evaluate(findConfigFile().readText()) as DbConfig
    }

    protected fun createConnection(): Connection {
        return Connection(loadConfigFile())
    }

    private fun evaluate(script: String): Any = withScriptClasspath {
        scriptEngine().eval(script)
    }

    private fun <T> withScriptClasspath(block: () -> T): T {
        val previous = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = scriptClassLoader()
        return try {
            block()
        } finally {
            Thread.currentThread().contextClassLoader = previous
        }
    }

    private var scriptClassLoader: URLClassLoader? = null

    private fun scriptClassLoader(): URLClassLoader {
        scriptClassLoader?.let { return it }
        val parent = AbstractMigrationTask::class.java.classLoader
        val urls = scriptClasspath.files.map { it.toURI().toURL() }.toTypedArray()
        return URLClassLoader(urls, parent).also { scriptClassLoader = it }
    }

    private var scriptEngine: ScriptEngine? = null

    private fun scriptEngine(): ScriptEngine {
        scriptEngine?.let { return it }
        val engine = ScriptEngineManager().getEngineByName("kotlin")
            ?: error("Kotlin script engine not found on the classpath")
        scriptEngine = engine
        return engine
    }

    protected companion object {
        protected fun removePackageStatement(script: String) =
            script.replace(Regex("^\\s*package\\s+.+"), "")
    }
}
