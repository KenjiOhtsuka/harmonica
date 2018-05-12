package com.improve_future.harmonica.core

import java.sql.Types

class Column(val name: String, val type: Type) {
    val sqlType: String
        get() {
            return when (type) {
                Types.VARCHAR -> "VARCHAR"
                Types.INTEGER -> "INTEGER"
                Types.BOOLEAN -> "BOOL"
                Types.BLOB -> "BLOB"
                Types.DATE -> "DATE"
                else -> throw Exception()
            }
        }
}