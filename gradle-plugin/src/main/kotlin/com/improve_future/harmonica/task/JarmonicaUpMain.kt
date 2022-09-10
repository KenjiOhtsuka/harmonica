/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.task

import com.improve_future.harmonica.plugin.JarmonicaArgument

object JarmonicaUpMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val argument = JarmonicaArgument.parse(args)
//        (classLoader as URLClassLoader).urLs.forEach {
//            println(it)
//        }

        val maxStep = JarmonicaArgument.parseStepString(args[JarmonicaArgument.DEFAULT_ARGUMENT_SIZE])
        var stepCounter = 1

        val connection = createConnection(argument.migrationPackage, argument.env)
        try {
            connection.transaction {
                versionService.setupHarmonicaMigrationTable(connection)
            }
            for (migrationClass in findMigrationClassList(argument.migrationPackage)) {
                val migrationVersion =
                    versionService.pickUpVersionFromClassName(migrationClass.name)
                if (versionService.isVersionMigrated(connection, migrationVersion)) continue

                println("== [Start] Migrate up $migrationVersion ==")
                connection.transaction {
                    val migration = migrationClass.getConstructor().newInstance()
                    argument.apply(migration)
                    migration.connection = connection
                    migration.up()
                    if (!argument.isReview)
                        versionService.saveVersion(connection, migrationVersion)
                }
                println("== [End] Migrate up $migrationVersion ==")
                if (maxStep != null && ++stepCounter > maxStep) break
            }
            connection.close()
        } catch (e: Exception) {
            connection.close()
            throw e
        }
    }
}