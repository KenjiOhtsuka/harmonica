/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.plugin

class JarmonicaArgument() {
    lateinit var migrationPackage: String
    internal lateinit var taskType: JarmonicaTaskType
    lateinit var env: String
    lateinit var migrationDirectory: String
    internal var tableNamePluralization: Boolean = false
    internal var dispSql = false
    private val additionalArgList = mutableListOf<String>()

    fun toArray(): Array<String> {
        return arrayOf(
            migrationPackage,
            taskType.name,
            migrationDirectory,
            env,
            tableNamePluralization.toString(),
            dispSql.toString()
        ) + additionalArgList.toTypedArray()
    }

    fun toList(): List<String> {
        return toArray().toList()
    }

    fun add(arg: String): JarmonicaArgument {
        additionalArgList.add(arg)
        return this
    }

    companion object {
        fun parse(args: Array<out String>): JarmonicaArgument {
            return JarmonicaArgument().also {
                it.migrationPackage = args[0]
                it.migrationDirectory = args[2]
                it.env = args[3]
                it.tableNamePluralization = args[4] == "true"
                it.dispSql = args[5] == "true"
            }
        }

        fun parseStepString(stepString: String): Long? {
            return if (stepString == "null") null else stepString.toLong()
        }
    }
}