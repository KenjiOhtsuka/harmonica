package com.improve_future.harmonica.core

import java.sql.Types

class Column(val name: String, val type: Type) {
    val sqlType: String
        get() {
            return when (type) {
                Types.BIGINT -> "BIGINT" // BITSERIAL
                Types.BIT -> "BIT"
                Types.BINARY -> "BIT VARYING"
                Types.BLOB -> "BLOB"
                Types.BOOLEAN -> "BOOL"
                Types.LONGVARBINARY -> "BYTEA"
                Types.CHAR -> "CHARACTER"
                Types.DATE -> "DATE"
                Types.DOUBLE -> "DOUBLE PRECISION"
                Types.NUMERIC -> "NUMERIC"
                Types.REAL -> "REAL"
                Types.SMALLINT -> "SMALLINT"
                Types.LONGNVARCHAR -> "TEXT"
                Types.TIMESTAMP -> "TIMESTAMP" // TIME WITH TIMEZONE, TIMESTAMP WITH TIMEZONE
                Types.VARCHAR -> "VARCHAR"
                Types.INTEGER -> "INTEGER" // SERIAL
                Types.SQLXML -> "XML"
                else -> throw Exception()
            }
        }
}