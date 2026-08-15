package com.improve_future.harmonica.integration

import org.junit.jupiter.api.Assumptions
import java.sql.DriverManager
import java.sql.SQLException

object TestDb {
    val postgresHost = env("HARMONICA_TEST_POSTGRES_HOST", "127.0.0.1")
    val postgresPort = env("HARMONICA_TEST_POSTGRES_PORT", "5432").toInt()
    val postgresDb = env("HARMONICA_TEST_POSTGRES_DB", "harmonica_test")
    val postgresUser = env("HARMONICA_TEST_POSTGRES_USER", "developer")
    val postgresPassword = env("HARMONICA_TEST_POSTGRES_PASSWORD", "developer")

    val mysqlHost = env("HARMONICA_TEST_MYSQL_HOST", "127.0.0.1")
    val mysqlPort = env("HARMONICA_TEST_MYSQL_PORT", "3306").toInt()
    val mysqlDb = env("HARMONICA_TEST_MYSQL_DB", "harmonica_test")
    val mysqlUser = env("HARMONICA_TEST_MYSQL_USER", "developer")
    val mysqlPassword = env("HARMONICA_TEST_MYSQL_PASSWORD", "developer")

    private fun env(name: String, default: String): String =
        System.getenv(name) ?: default

    fun requireDb(url: String, user: String, password: String) {
        val previousTimeout = DriverManager.getLoginTimeout()
        try {
            DriverManager.setLoginTimeout(2)
            DriverManager.getConnection(url, user, password).close()
        } catch (e: SQLException) {
            Assumptions.abort("Database unavailable at $url: ${e.message}")
        } finally {
            DriverManager.setLoginTimeout(previousTimeout)
        }
    }
}
