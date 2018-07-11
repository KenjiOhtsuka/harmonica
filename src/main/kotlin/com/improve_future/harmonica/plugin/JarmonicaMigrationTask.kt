package com.improve_future.harmonica.plugin

import org.gradle.api.tasks.JavaExec

abstract class JarmonicaMigrationTask : JavaExec() {
    abstract val taskType: JarmonicaTaskType

    protected val migrationPackage: String
        get() {
            if (project.extensions.extraProperties.has("migrationPackage"))
                return project.extensions.extraProperties["migrationPackage"] as String
            if (project.extensions.extraProperties.has("directoryPath"))
                return directoryPath.removePrefix("src/main/kotlin/").
                        replace("/", ".")
            return "db"
        }

    protected val directoryPath: String
        get() {
            if (project.extensions.extraProperties.has("directoryPath"))
                return project.extensions.extraProperties["directoryPath"] as String
            if (project.extensions.extraProperties.has("migrationPackage"))
                "src/main/kotlin/" + migrationPackage.replace(".", "/")
            return "src/main/kotlin/db"
        }

    protected val env: String
        get() {
            if (project.extensions.extraProperties.has("env"))
                return project.extensions.extraProperties["env"] as String
            if (project.hasProperty("env"))
                return project.properties["env"] as String
            return "Default"
        }

    protected fun buildJarmonicaArgument(
            vararg args: String): JarmonicaArgument {
        return JarmonicaArgument().also {
            it.migrationDirectory = directoryPath
            it.migrationPackage = migrationPackage
            it.env = env
            it.taskType = taskType
            args.forEach { arg -> it.add(arg) }
        }
    }
}
