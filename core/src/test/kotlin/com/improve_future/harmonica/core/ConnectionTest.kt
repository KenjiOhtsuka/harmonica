/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

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
        assertEquals(
            "jdbc:mysql://127.0.0.1:3306/test?autoReconnect=true",
            method.invoke(Connection.Companion, mySqlConfig) as String
        )
        // SQLite
        val sqliteConfig = object : DbConfig({
            dbms = Dbms.SQLite
            dbName = "test"
        }) {}
        assertEquals(
            "jdbc:sqlite:test.db",
            method.invoke(Connection.Companion, sqliteConfig) as String
        )
    }
}