package com.improve_future.harmonica.plugin

import org.gradle.api.tasks.JavaExec

abstract class JarmonicaMigrationTask : JavaExec() {
    internal abstract val taskType: JarmonicaTaskType

    protected val migrationPackage: String
        get() {
            if (project.extensions.extraProperties.has("migrationPackage"))
                return project.extensions.extraProperties["migrationPackage"] as String
            if (project.extensions.extraProperties.has("directoryPath"))
                return directoryPath
                    .replace(Regex("^src/main/(kotlin|java)/"), "")
                    .replace("/", ".")
            return "db"
        }

    private val directoryPath: String
        get() {
            if (project.extensions.extraProperties.has("directoryPath"))
                return project.extensions.extraProperties["directoryPath"] as String
            if (project.extensions.extraProperties.has("migrationPackage"))
                return "src/main/kotlin/" + migrationPackage.replace(".", "/")
            return "src/main/kotlin/db"
        }

    private val env: String
        get() {
            if (project.extensions.extraProperties.has("env"))
                return project.extensions.extraProperties["env"] as String
            return getProperty("env") ?: "Default"
        }

    protected fun buildJarmonicaArgument(
        vararg args: String
    ): JarmonicaArgument {
        return JarmonicaArgument().also {
            it.migrationDirectory = directoryPath
            it.migrationPackage = migrationPackage
            it.env = env
            it.taskType = taskType
            args.forEach { arg -> it.add(arg) }
        }
    }

    protected fun getProperty(name: String): String? {
        return if (project.hasProperty(name))
            project.properties[name] as String
        else
            null
    }
}
