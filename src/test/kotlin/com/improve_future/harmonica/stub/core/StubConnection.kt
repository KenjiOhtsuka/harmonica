package com.improve_future.harmonica.stub.core

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.DbConfig
import java.sql.Statement

class StubConnection : ConnectionInterface {
    override val config = DbConfig()

    val executedSqlList = mutableListOf<String>()

    override fun transaction(block: Connection.() -> Unit) {

    }

    override fun execute(sql: String): Boolean {
        executedSqlList.add(sql)
        return true
    }

    override fun doesTableExist(tableName: String): Boolean {
        return true
    }

    @Deprecated(
            "Cause Error",
            ReplaceWith("Can't be replaced. It's created only to meet interface."))
    override fun createStatement(): Statement {
        return Statement::class.java.newInstance()
    }
}