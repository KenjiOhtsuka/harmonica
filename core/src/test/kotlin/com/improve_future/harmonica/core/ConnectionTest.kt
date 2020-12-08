/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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