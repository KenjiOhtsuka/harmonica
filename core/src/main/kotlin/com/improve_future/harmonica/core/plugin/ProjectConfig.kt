package com.improve_future.harmonica.core.plugin

internal object ProjectConfig {
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