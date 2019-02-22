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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.task

object JarmonicaDownMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val migrationPackage = args[0]
        val env = args[3]
        val maxStep = if (args[4] == "") null else args[4].toLong()
        var stepCounter = 1

        val classList = findMigrationClassList(migrationPackage)

        val connection = createConnection(migrationPackage, env)
        try {
            while (true) {
                val migrationVersion = versionService.findCurrentMigrationVersion(connection)
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
                    val migration = migrationClass.newInstance()
                    migration.connection = connection
                    migration.down()
                    versionService.removeVersion(connection, migrationVersion!!)
                }
                println("== [End] Migrate down $migrationVersion ==")
                if (maxStep != null && ++stepCounter > maxStep) break
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