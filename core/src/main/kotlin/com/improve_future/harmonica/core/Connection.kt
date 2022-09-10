/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core

import java.io.Closeable
import java.sql.*

open class Connection(
    override val config: DbConfig, protected val hasExposed: Boolean
) : Closeable, ConnectionInterface {
    private lateinit var coreConnection: java.sql.Connection

    private fun connect(config: DbConfig) {
        coreConnection = if (config.dbms != Dbms.SQLite) {
            object : java.sql.Connection by DriverManager.getConnection(
                buildConnectionUriFromDbConfig(config),
                config.user,
                config.password
            ) {
                override fun setTransactionIsolation(level: Int) {}
            }
        } else {
            DriverManager.getConnection(
                buildConnectionUriFromDbConfig(config)
            )
        }
        database = null
        coreConnection.autoCommit = false
    }

    private val javaConnection: java.sql.Connection
        get() {
            if (isClosed) connect(config)

            return if (hasExposed)
                coreConnection
//            TransactionManager.current().connection
            else
                coreConnection
        }

    private var database: Database? = null

    override fun close() {
        if (!isClosed) javaConnection.close()
//        DriverManager.getDrivers().iterator().forEach {
//            it.takeUnless {  }
//        }
//        DriverManager.deregisterDriver(DriverManager.getDriver(
//                buildConnectionUriFromDbConfig(config)))
    }

    private val isClosed: Boolean
        get() {
            return coreConnection.isClosed
        }

    init {
//        val ds = InitialContext().lookup(
//                config.toConnectionUrlString()) as DataSource
//        javaConnection = object : java.sql.Connection by ds.connection {
//            override fun setTransactionIsolation(level: Int) {}
//        }
//        DriverManager.registerDriver(
//                DriverManager.getDriver(buildConnectionUriFromDbConfig(config)))
        //coreConnection =
        connect(config)
        when (config.dbms) {
            Dbms.Oracle ->
                execute("SELECT 1 FROM DUAL")
            else ->
                execute("SELECT 1;")
        }
    }

    companion object {
        fun create(hasExposed: Boolean, block: DbConfig.() -> Unit): Connection {
            return Connection(DbConfig.create(block), hasExposed)
        }

        private fun buildConnectionUriFromDbConfig(dbConfig: DbConfig): String {
            return dbConfig.run {
                when (dbms) {
                    Dbms.PostgreSQL ->
                        "jdbc:postgresql://$host:$port/$dbName?autoReconnect=true"
                    Dbms.MySQL ->
                        "jdbc:mysql://$host:$port/$dbName?autoReconnect=true"
                    Dbms.SQLite ->
                        "jdbc:sqlite:$dbName.db"
                    Dbms.Oracle ->
                        // JDBC URL format for ORacle
                        //   jdbc:oracle:<drivertype>:<username/password>@<database>
                        //
                        //   drivertype: thin, oci or kprb
                        //   database format:
                        //     //<host>:<port>/<service>
                        //     <host>:<port>:<SID>
                        //     <TNSName>
                        "jdbc:oracle:thin:@//$host:$port/$dbName"
                    Dbms.SQLServer ->
                        ""
                    Dbms.H2 ->
                        ""
                }
            }
        }
    }

    override fun transaction(block: Connection.() -> Unit) {
        javaConnection.autoCommit = false
        try {
            block(this)
            javaConnection.commit()
        } catch (e: Exception) {
            javaConnection.rollback()
            javaConnection.close()
            throw e
        }
    }

//    fun executeSelect(sql: String) {
//        val statement = createStatement()
//        val rs = statement.executeQuery(sql)
//        while (rs.next()) {
//            for (i in 0 until rs.metaData.columnCount - 1) {
//                when (rs.metaData.getColumnType(i)) {
//                    Types.DATE -> {}
//                    Types.BIGINT -> {}
//                    Types.BINARY -> {}
//                    Types.BIT -> {}
//                }
//            }
//        }
//        rs.close()
//    }

    /**
     * Execute SQL
     */
    override fun execute(sql: String): Boolean {
        val statement = createStatement()
        val result: Boolean = statement.execute(sql)
        statement.close()
        return result
    }

    /**
     * Check table existence
     *
     * @param tableName The name of the table to be checked.
     */
    override fun doesTableExist(tableName: String): Boolean {
        val resultSet = javaConnection.metaData.getTables(
            null, null,
            if (config.dbms == Dbms.Oracle) tableName.toUpperCase()
            else tableName,
            null
        )
        val result = resultSet.next()
        resultSet.close()
        return result
    }

    override fun createStatement(): Statement {
        return javaConnection.createStatement()
    }
}