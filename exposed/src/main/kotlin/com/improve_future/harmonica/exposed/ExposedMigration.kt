package com.improve_future.harmonica.exposed

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.util.WeakHashMap

private val databaseCache = WeakHashMap<Connection, Database>()

fun AbstractMigration.exposedTransaction(block: Transaction.() -> Unit) {
    val connection = connection as? Connection
        ?: error("exposedTransaction requires a com.improve_future.harmonica.core.Connection")
    transaction(connection.exposedDatabase()) { block() }
}

private fun Connection.exposedDatabase(): Database =
    databaseCache.getOrPut(this) {
        val weakConnection = WeakReference(this)
        Database.connect(
            getNewConnection = { weakConnection.exposedConnection() },
            databaseConfig = DatabaseConfig { defaultMaxAttempts = 1 }
        )
    }

private fun WeakReference<Connection>.exposedConnection(): java.sql.Connection {
    val connection = get()
        ?: error("harmonica Connection was garbage-collected while Exposed was in use")
    return Proxy.newProxyInstance(
        java.sql.Connection::class.java.classLoader,
        arrayOf(java.sql.Connection::class.java)
    ) { _, method, args ->
        when (method.name) {
            "commit", "rollback", "close" -> null
            else -> try {
                method.invoke(connection.jdbcConnection, *(args ?: emptyArray()))
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        }
    } as java.sql.Connection
}
