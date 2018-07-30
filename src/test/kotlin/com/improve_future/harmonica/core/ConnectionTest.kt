package com.improve_future.harmonica.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConnectionTest {
    @Test
    fun testBuildConnectionUriFromDbConfig() {
        val method = Connection.Companion.javaClass
            .getDeclaredMethod(
                "buildConnectionUriFromDbConfig",
                DbConfig::class.java
            ).also { it.isAccessible = true }
        // MySQL
        val mySqlConfig = object : DbConfig({
            dbms = Dbms.MySQL
            dbName = "test"
        }) {}
        var result =
            method.invoke(Connection.Companion, mySqlConfig) as String
        assertEquals(
            "jdbc:mysql://127.0.0.1:3306/test?autoReconnect=true",
            result
        )
        // SQLite
        val sqliteConfig = object : DbConfig({
            dbms = Dbms.SQLite
            dbName = "test"
        }) {}
        result = method.invoke(Connection.Companion, sqliteConfig) as String
        assertEquals("jdbc:sqlite:test.db", result)
    }
}