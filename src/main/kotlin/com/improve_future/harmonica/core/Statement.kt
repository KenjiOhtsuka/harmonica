package com.improve_future.harmonica.core

import java.sql.ResultSet

class Statement(private val javaStatement: java.sql.Statement): AutoCloseable {
    override fun close() {
        javaStatement.close()
    }

    fun execute(sql: String): Boolean {
        return javaStatement.execute(sql)
    }

    fun executeQuery(sql: String): ResultSet {
        return javaStatement.executeQuery(sql)
    }
}