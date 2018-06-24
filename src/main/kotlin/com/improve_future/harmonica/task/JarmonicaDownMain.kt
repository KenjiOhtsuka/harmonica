package com.improve_future.harmonica.task

object JarmonicaDownMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val migrationPackage = args[0]

        val classList = findMigrationClassList(migrationPackage)

        val connection = createConnection(migrationPackage)
        try {
            val migrationVersion = versionService.findCurrentMigrationVersion(connection)
            if (migrationVersion.isNotEmpty()) {
                val classCandidateList =
                        versionService.filterClassCandidateWithVersion(
                                classList, migrationVersion!!)
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