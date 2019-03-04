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

    object Exposed {
        object Database {
            private val databaseClass by lazy {
                Class.forName("org.jetbrains.exposed.sql.Database")
            }

            private val database by lazy {
                databaseClass.getDeclaredField("INSTANCE").get(databaseClass) as Any
            }

            // manager: (Database) -> TransactionManager = { ThreadLocalTransactionManager(it, DEFAULT_ISOLATION_LEVEL) }
            fun connect(
                getNewConnection: () -> java.sql.Connection,
                manager: Any? = null
            ): Database {
                lateinit var a: () -> java.sql.Connection
                lateinit var b: (Database) -> TransactionManager
                val method = database::class.java.getMethod(
                    "connect",
                    a::class.java,
                    b::class.java
                )
                if (manager == null)
                    method.invoke(database, getNewConnection)
                else
                    method.invoke(database, getNewConnection, manager)
                return Database
            }
        }

        object TransactionManager {
            object Manager {
                private val manager by lazy {
                    val method = transactionManager::class.java.getMethod(
                        "getManager"
                    )
                    method.invoke(transactionManager) as Any
                }

                val defaultIsolationLevel: Int
                    get () {
                        val method = manager::class.java.getMethod(
                            "getTransactionIsolationLevel"
                        )
                        return method.invoke(manager) as Int
                    }
            }

            private val transactionManagerClass by lazy {
                Class.forName("org.jetbrains.exposed.sql.transactions.TransactionManager")!!
            }

            private val transactionManager by lazy {
                transactionManagerClass.getDeclaredField("INSTANCE").get(
                    transactionManagerClass
                ) as Any
            }

            val manager: Manager
                get() {
                    val method = transactionManager::class.java.getMethod(
                        "getManager"
                    )
                    return method.invoke(transactionManager) as Manager
                }

            val defaultIsolationLevel by lazy {

            }
        }
//        fun getTransactionManager(): Class<*> {
//            return Class.forName("org.jetbrains.exposed.sql.transactions.TransactionManager")
//        }

    }
}