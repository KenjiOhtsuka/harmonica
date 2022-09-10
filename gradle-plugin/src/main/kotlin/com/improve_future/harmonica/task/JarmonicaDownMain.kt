/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.task

import com.improve_future.harmonica.plugin.JarmonicaArgument

object JarmonicaDownMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val argument = JarmonicaArgument.parse(args)
        val maxStep = JarmonicaArgument.parseStepString(args[JarmonicaArgument.DEFAULT_ARGUMENT_SIZE])
        var stepCounter = 1

        val classList = findMigrationClassList(argument.migrationPackage)

        val connection =
            createConnection(argument.migrationPackage, argument.env)
        try {
            while (true) {
                val migrationVersion =
                    versionService.findCurrentMigrationVersion(connection)
                if (migrationVersion.isEmpty()) break

                val classCandidateList =
                    versionService.filterClassCandidateWithVersion(
                        classList, migrationVersion
                    )
                if (classCandidateList.isEmpty())
                    throw Error("No migration class is found for the version $migrationVersion.")
                if (1 < classCandidateList.size)
                    throw Error("More then one classes exist for the version $migrationVersion.")
                val migrationClass = classCandidateList.first()

                println("== [Start] Migrate down $migrationVersion ==")
                connection.transaction {
                    val migration =
                        migrationClass.getConstructor().newInstance()
                    argument.apply(migration)
                    migration.connection = connection
                    migration.down()
                    versionService.removeVersion(connection, migrationVersion)
                }
                println("== [End] Migrate down $migrationVersion ==")
                if (maxStep == null || ++stepCounter > maxStep) break
            }
            connection.close()
        } catch (e: Exception) {
            connection.close()
            throw e
        }


//        (classLoader as URLClassLoader).urLs.forEach {
//            println(it)
//        }
    }
}