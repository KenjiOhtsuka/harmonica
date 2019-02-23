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

open class DbConfig() {
    lateinit var dbms: Dbms
    /**
     * The default value is 127.0.0.1
     */
    var host: String = "127.0.0.1"
    var port: Int = -1
    /**
     * Database Name
     *
     * ## SQLite
     *
     * Access to `$dbName.db` file..
     */
    lateinit var dbName: String
    lateinit var user: String
    lateinit var password: String
    var sslmode: Boolean = false

    constructor(block: DbConfig.() -> Unit) : this() {
        this.block()

        if (port == -1) {
            port = when (dbms) {
                Dbms.PostgreSQL -> 5432
                Dbms.MySQL -> 3306
                Dbms.SQLite -> 0
                Dbms.Oracle -> 0
                Dbms.SQLServer -> 0
            }
        }
    }

    companion object {
        fun create(block: DbConfig.() -> Unit): DbConfig {
            return DbConfig(block)
        }
    }
}

operator fun DbConfig.invoke(block: DbConfig.() -> Unit): DbConfig {
    this.block()
    return this
}
