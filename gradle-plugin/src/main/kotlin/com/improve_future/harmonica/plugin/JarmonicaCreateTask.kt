package com.improve_future.harmonica.plugin

import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Migration tasks execute user-provided scripts")
abstract class JarmonicaCreateTask : JarmonicaMigrationTask() {
    override val taskType = JarmonicaTaskType.Create

    override fun exec() {
        val migrationName =
                if (project.hasProperty("migrationName"))
                    project.findProperty("migrationName") as String
                else "Migration"

        jvmArgs = listOf<String>()
        args = buildJarmonicaArgument(migrationName).toList()
        super.exec()
    }
}