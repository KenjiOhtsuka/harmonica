package com.improve_future.harmonica.task

object JarmonicaUpMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val migrationPackage = args[0]
//        (classLoader as URLClassLoader).urLs.forEach {
//            println(it)
//        }
        val env = args[3]
        val maxStep = if (args[4] == "") null else args[4].toLong()
        var stepCounter = 1

        val connection = createConnection(migrationPackage, env)
        try {
            connection.transaction {
                versionService.setupHarmonicaMigrationTable(connection)
            }
            for (migrationClass in findMigrationClassList(migrationPackage)) {
                val migrationVersion =
                    versionService.pickUpVersionFromClassName(migrationClass.name)
                if (versionService.isVersionMigrated(connection, migrationVersion)) continue

                println("== [Start] Migrate up $migrationVersion ==")
                connection.transaction {
                    val migration = migrationClass.newInstance()
                    migration.connection = connection
                    migration.up()
                    versionService.saveVersion(connection, migrationVersion)
                }
                println("== [End] Migrate up $migrationVersion ==")
                if (maxStep != null && ++stepCounter > maxStep) break;
            }
            connection.close()
        } catch (e: Exception) {
            connection.close()
            throw e
        }
    }
}