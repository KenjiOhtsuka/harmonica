package com.improve_future.harmonica.core

class ConnectionAop(javaConnection: java.sql.Connection):
        Connection(javaConnection) {
    override fun transaction(block: Connection.() -> Unit) {
        try {
            super.transaction(block)
        } catch(e: Exception) {

        }
    }
}