package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.*
import com.improve_future.harmonica.service.VersionService
import org.gradle.api.tasks.Input

abstract class AbstractMigrationTask : AbstractHarmonicaTask() {
    /** The table name to store executed migration version IDs. */
    private val migrationTableName: String = "harmonica_migration"
    protected val versionService: VersionService

    init {
        versionService = VersionService(migrationTableName)
    }

    @Input
    var dbms: Dbms = Dbms.PostgreSQL

    protected fun readMigration(script: String): AbstractMigration {
        return engine.eval(removePackageStatement(script)) as AbstractMigration
    }

    protected fun createConnection(): Connection {
        return Connection(loadConfigFile())
    }

    protected companion object {
        protected fun removePackageStatement(script: String) =
            script.replace(Regex("^\\s*package\\s+.+"), "")
    }
}