package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.*
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.valueOrNull
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.host.getScriptingClass
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.JvmGetScriptingClass
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.net.URLClassLoader

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
        return resolvePath(directoryPath).resolve("config").resolve("$env.kts")
    }

    fun loadConfigFile(): DbConfig {
        return evaluate(findConfigFile().readText()) as DbConfig
    }

    protected fun createConnection(): Connection {
        return Connection(loadConfigFile())
    }

    private fun evaluate(script: String): Any = withScriptClasspath {
        val result = BasicJvmScriptingHost().eval(
            script.toScriptSource(),
            scriptCompilationConfiguration(),
            null
        )
        result.reports.firstOrNull { it.severity >= ScriptDiagnostic.Severity.ERROR }
            ?.let { report -> throw IllegalStateException(report.message) }
        when (val returnValue = result.valueOrNull()?.returnValue) {
            is ResultValue.Value -> returnValue.value ?: error("Script produced no result")
            else -> error("Script produced no result")
        }
    }

    private var compilationConfiguration: ScriptCompilationConfiguration? = null

    private fun scriptCompilationConfiguration(): ScriptCompilationConfiguration {
        compilationConfiguration?.let { return it }
        return createJvmCompilationConfigurationFromTemplate<MigrationScript>(
            baseHostConfiguration = ScriptingHostConfiguration {
                getScriptingClass(JvmGetScriptingClass())
                jvm {
                    baseClassLoader(MigrationScript::class.java.classLoader)
                }
            }
        ) {
            jvm {
                dependenciesFromCurrentContext(wholeClasspath = true)
            }
        }.also { compilationConfiguration = it }
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

    protected companion object {
        protected fun removePackageStatement(script: String) =
            script.replace(Regex("^\\s*package\\s+.+"), "")
    }
}
