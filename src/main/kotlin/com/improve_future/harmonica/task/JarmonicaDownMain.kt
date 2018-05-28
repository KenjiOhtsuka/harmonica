package com.improve_future.harmonica.task

object JarmonicaDownMain : JarmonicaTaskMain() {
    @JvmStatic
    fun main(vararg args: String) {
        val migrationPackage = args[0]

        val classList = findMigrationClassList(migrationPackage)

        val connection = createConnection(migrationPackage)
        try {
            val migrationVersion = versionService.findCurrentMigrationVersion(connection)
            val classCandidateList = classList.filter {
                it.name.split('_')[0].substring(1) == migrationVersion }
            if (classCandidateList.isEmpty())
                throw Error("No migration class is found for the version $migrationVersion.")
            if (1 < classCandidateList.size)
                throw Error("More then one classes exist for the version $migrationVersion.")
            val clazz = classCandidateList.first()

            println("== [Start] Migrate down $migrationVersion ==")
            connection.transaction {
                val migration = clazz.newInstance()
                migration.connection = connection
                migration.down()
                versionService.removeVersion(connection, migrationVersion)
            }
            connection.close()
            println("== [End] Migrate down $migrationVersion ==")
        } catch (e: Exception) {
            connection.close()
            throw e
        }

//        (classLoader as URLClassLoader).urLs.forEach {
//            println(it)
//        }
    }
}