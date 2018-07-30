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
            }
        }
    }

    companion object {
        fun create(block: DbConfig.() -> Unit): DbConfig {
            return DbConfig(block)
        }
    }

    fun toConnectionUrlString(): String {
        return when (dbms) {
            Dbms.PostgreSQL ->
                "postgresql://$user:$password@$host:$port/$dbName"
            Dbms.MySQL ->
                "jdbc:mysql://$host:$port/$dbName"
            Dbms.SQLite ->
                "jdbc:sqlite:$dbName.db"
            Dbms.Oracle ->
                ""
        }
    }
}

operator fun DbConfig.invoke(block: DbConfig.() -> Unit): DbConfig {
    this.block()
    return this
}
