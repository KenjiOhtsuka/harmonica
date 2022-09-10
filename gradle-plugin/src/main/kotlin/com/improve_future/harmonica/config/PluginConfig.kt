/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.config

internal object PluginConfig {
    const val groupName = "migration"

    fun hasExposed(): Boolean {
        return try {
            null != Class.forName("org.jetbrains.exposed.sql.Database")
        } catch (e: Exception) {
            false
        }
    }

//    object Exposed {
//        fun getTransactionManager(): Class<*> {
//            return Class.forName("org.jetbrains.exposed.sql.transactions.TransactionManager")
//        }
//    }
}
