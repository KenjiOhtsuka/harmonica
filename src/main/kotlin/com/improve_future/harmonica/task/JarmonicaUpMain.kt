package com.improve_future.harmonica.task

object JarmonicaUpMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val migrationPackage = args[0]

//        (classLoader as URLClassLoader).urLs.forEach {
//            println(it)
//        }

        val connection = createConnection(migrationPackage)
        try {
            connection.transaction {
                versionService.setupHarmonicaMigrationTable(connection)
            }
            for (clazz in findMigrationClassList(migrationPackage)) {
                val migrationVersion = versionService.pickUpVersionFromClassName(clazz.name)
                if (versionService.isVersionMigrated(connection, migrationVersion)) continue

                println("== [Start] Migrate up $migrationVersion ==")
                connection.transaction {
                    val migration = clazz.newInstance()
                    migration.connection = connection
                    migration.up()
                    versionService.saveVersion(connection, migrationVersion)
                }
                println("== [End] Migrate up $migrationVersion ==")
            }
            connection.close()
        } catch (e: Exception) {
            connection.close()
            throw e
        }
    }
}