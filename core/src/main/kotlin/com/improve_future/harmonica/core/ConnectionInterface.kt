/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core

import java.sql.Statement

interface ConnectionInterface {
    val config: DbConfig
    fun transaction(block: Connection.() -> Unit)
    fun execute(sql: String): Boolean
    fun doesTableExist(tableName: String): Boolean
    fun createStatement(): Statement
}