package com.improve_future.harmonica

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.Dbms
import org.jetbrains.exposed.sql.transactions.DEFAULT_ISOLATION_LEVEL
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.reflections.Reflections
import java.net.URLClassLoader
import java.sql.ResultSet
import java.sql.Statement

object Main {
    private const val migrationTableName: String = "harmonica_migration"

    fun transaction(block: () -> Unit) {
        TransactionManager.currentOrNew(DEFAULT_ISOLATION_LEVEL).let {
            try {
                block()
                it.commit()
                it.close()
            } catch (e: Exception) {
                it.rollback()
                it.close()
                throw e
            }
        }
    }

    var dbms: Dbms = Dbms.PostgreSQL

    fun createConnection(packageName: String): Connection {
        val d = loadDbConfig(packageName)
        println(d.dbName)
        println(d.host)
        println(d.user)
        return Connection(loadDbConfig(packageName))
    }

    fun createStatement(connection: Connection): Statement {
        return connection.createStatement()
    }

    fun setupHarmonicaMigrationTable(connection: Connection) {
        if (!connection.doesTableExist(migrationTableName)) {
            connection.execute("""
                CREATE TABLE harmonica_migration (
                    version VARCHAR
                )""".trimMargin())
        }
    }

    fun doesVersionMigrated(connection: Connection, version: String): Boolean {
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

    fun saveVersion(connection: Connection, version: String) {
        println(migrationTableName)
        println(version)
        connection.execute(
                "INSERT INTO $migrationTableName(version) VALUES('$version');")
    }

    fun removeVersion(connection: Connection, version: String) {
        connection.execute(
                "DELETE FROM $migrationTableName WHERE version = '$version'")
    }

    fun findCurrentMigrationVersion(connection: Connection): String {
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

    lateinit var cl: ClassLoader

    @JvmStatic
    fun main(vararg args: String) {
        val migrationPackage = args[0]

        cl = ClassLoader.getSystemClassLoader()
        (cl as URLClassLoader).urLs.forEach {
            println(it)
        }

        val connection = createConnection(migrationPackage)
        try {
            transaction {
                setupHarmonicaMigrationTable(connection)
            }
            for (clazz in findMigrationClassList(migrationPackage)) {
                val migrationVersion: String = clazz.name.substring(clazz.name.lastIndexOf('_') + 1)
                if (doesVersionMigrated(connection, migrationVersion)) continue

                println("== [Start] Migrate up $migrationVersion ==")
                transaction {
                    val migration = clazz.newInstance()
                    migration.connection = connection
                    migration.up()
                    saveVersion(connection, migrationVersion)
                }
                println("== [End] Migrate up $migrationVersion ==")
            }
            connection.close()
        } catch (e: Exception) {
            connection.close()
            throw e
        }
    }

    fun findMigrationClassList(packageName: String): List<Class<out AbstractMigration>> {
        val reflections = Reflections(packageName)
        val classList = reflections.getSubTypesOf(AbstractMigration::class.java)
        return classList.toList().sortedBy { it.name }
    }

    fun loadDbConfig(packageName: String, env: String = "Default"): DbConfig {
        val reflections = Reflections(packageName)
        val classList = reflections.getSubTypesOf(DbConfig::class.java)
        classList.forEach {
            if (it.simpleName == env) return it.newInstance()
        }
        throw Exception("no config was found.")
    }
}