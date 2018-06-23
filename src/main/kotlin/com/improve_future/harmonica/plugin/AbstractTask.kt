package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.DbConfig
import org.gradle.api.DefaultTask
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager

abstract class AbstractTask: DefaultTask() {
    protected val directoryPath: String
    get() {
        if (project.extensions.extraProperties.has("directoryPath"))
            return project.extensions.extraProperties["directoryPath"] as String
        return "src/main/kotlin/db"
    }

    protected val env: String
    get() {
        if (project.extensions.extraProperties.has("env"))
            return project.extensions.extraProperties["env"] as String
        if (project.hasProperty("env"))
            return project.properties["env"] as String
        return "default"
    }

    fun findMigrationDir(): File {
        return Paths.get(directoryPath, "migration").toFile()
    }

}