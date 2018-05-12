package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.Dbms
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.sql.Statement
import javax.script.ScriptEngineManager

abstract class AbstractMigrationTask: AbstractTask() {
    private val migrationTableName: String = "harmonica_migration"

    @Input
    var dbms: Dbms = Dbms.PostgreSQL

    protected fun execKotlin(script: String): AbstractMigration {
        return engine.eval(script) as AbstractMigration
    }

    protected fun createConnection(): Connection {
        return Connection.create(loadConfigFile())
    }

    protected fun createStatement(connection: Connection): Statement {
        return connection.createStatement()
    }

    protected fun setupHarmonicaMigrationTable(connection: Connection) {
        if (!connection.doesTableExist(migrationTableName)) {
            connection.execute("""
                CREATE TABLE harmonica_migration (
                    version VARCHAR
                )""".trimMargin())
        }
    }

    protected fun doesVersionMigrated(connection: Connection, version: String): Boolean {
        createStatement(connection).use {
            it.executeQuery(
                    "SELECT COUNT(1) FROM $migrationTableName WHERE version = '$version").use {
                return it.getLong(0) == 1L
            }
        }
    }

    protected fun saveVersion(connection: Connection, version: String) {
        connection.execute(
                "INSERT INTO $migrationTableName(version) VALUES('$version');")
    }

    protected fun removeVersion(connection: Connection, version: String) {
        connection.execute(
                "DELETE FROM $migrationTableName WHERE version = $version")
    }

    protected fun findCurrentMigrationVersion(connection: Connection): String {
        if (!connection.doesTableExist(migrationTableName))
            return ""
        connection.createStatement().use {
            it.executeQuery("""
                SELECT version
                  FROM $migrationTableName
                 ORDER BY version DESC
                 LIMIT 1""".trimMargin()).use {
                if (it.next()) return it.getString(0)
            }
        }
        return ""
    }
}