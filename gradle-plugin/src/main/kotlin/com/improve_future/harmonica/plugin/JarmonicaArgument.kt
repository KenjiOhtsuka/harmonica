/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration

class JarmonicaArgument() {
    lateinit var migrationPackage: String
    internal lateinit var taskType: JarmonicaTaskType
    lateinit var env: String
    lateinit var migrationDirectory: String
    internal var tableNamePluralization: Boolean = false
    internal var dispSql = false
    internal var isReview = false
    private val additionalArgList = mutableListOf<String>()

    fun toArray(): Array<String> {
        return arrayOf(
            migrationPackage,
            taskType.name,
            migrationDirectory,
            env,
            tableNamePluralization.toString(),
            dispSql.toString(),
            isReview.toString()
        ) + additionalArgList.toTypedArray()
    }

    fun toList(): List<String> {
        return toArray().toList()
    }

    fun add(arg: String): JarmonicaArgument {
        additionalArgList.add(arg)
        return this
    }

    fun apply(migration: AbstractMigration): AbstractMigration {
        migration.isReview = isReview
        migration.dispSql = dispSql
        return migration
    }

    companion object {
        const val DEFAULT_ARGUMENT_SIZE = 7

        fun parse(args: Array<out String>): JarmonicaArgument {
            return JarmonicaArgument().also {
                it.migrationPackage = args[0]
                it.migrationDirectory = args[2]
                it.env = args[3]
                it.tableNamePluralization = args[4] == "true"
                it.dispSql = args[5] == "true"
                it.isReview = args[6] == "true"
            }
        }

        fun parseStepString(stepString: String): Long? {
            return if (stepString == "null") null else stepString.toLong()
        }
    }
}