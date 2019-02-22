/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Harmonica.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.plugin

class JarmonicaArgument() {
    lateinit var migrationPackage: String
    internal lateinit var taskType: JarmonicaTaskType
    lateinit var env: String
    lateinit var migrationDirectory: String
    private val additionalArgList = mutableListOf<String>()

    fun toArray(): Array<String> {
        return arrayOf(
            migrationPackage,
            taskType.name,
            migrationDirectory,
            env
        ) + additionalArgList.toTypedArray()
    }

    fun toList(): List<String> {
        return listOf(
            migrationPackage,
            taskType.name,
            migrationDirectory,
            env
        ) + additionalArgList
    }

    fun add(arg: String): JarmonicaArgument {
        additionalArgList.add(arg)
        return this
    }
}