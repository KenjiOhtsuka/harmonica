/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.task

import com.improve_future.harmonica.plugin.JarmonicaArgument

object JarmonicaVersionMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val argument = JarmonicaArgument.parse(args)

        val connection = this.createConnection(
            argument.migrationPackage, argument.env
        )

        val version = versionService.findCurrentMigrationVersion(connection)
        if (version.isEmpty()) {
            println("(no version)")
        } else {
            println("Version: $version")
        }

        connection.close()
    }
}