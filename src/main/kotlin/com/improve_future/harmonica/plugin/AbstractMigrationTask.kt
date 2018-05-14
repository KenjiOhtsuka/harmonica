package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.sql.ResultSet
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
        val statement = createStatement(connection)
        val result: Boolean
        val resultSet: ResultSet
        try {
            resultSet = statement.executeQuery(
                    "SELECT COUNT(1) FROM $migrationTableName WHERE version = '$version';")
            resultSet.next()
            result = resultSet.getLong(1) > 0
        } catch (e: Exception) {
            println(e.message)
            statement.close()
            throw e
        }
        statement.close()
        return result
    }

    protected fun saveVersion(connection: Connection, version: String) {
        connection.execute(
                "INSERT INTO $migrationTableName(version) VALUES('$version');")
    }

    protected fun removeVersion(connection: Connection, version: String) {
        connection.execute(
                "DELETE FROM $migrationTableName WHERE version = '$version'")
    }

    protected fun findCurrentMigrationVersion(connection: Connection): String {
        var result: String = ""
        if (!connection.doesTableExist(migrationTableName))
            return result

        val statement = connection.createStatement()
        try {
            val resultSet = statement.executeQuery("""
                SELECT version
                  FROM $migrationTableName
                 ORDER BY version DESC
                 LIMIT 1""".trimMargin())
            if (resultSet.next()) result = resultSet.getString(1)
            resultSet.close()
            statement.close()
        } catch (e: Exception) {
            statement.close()
            throw e
        }
        statement.close()
        return result
    }
}