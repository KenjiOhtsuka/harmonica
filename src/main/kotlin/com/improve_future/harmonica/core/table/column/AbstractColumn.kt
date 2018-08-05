package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.Index
import java.sql.Types

internal typealias Type = Int

internal abstract class AbstractColumn(
    val name: String
) {
    val indexList: MutableList<Index> = mutableListOf()

//    val sqlType: String
//        get() {
//            return when (type) {
//                Types.BIT -> "BIT"
//                Types.BOOLEAN -> "BOOL"
//
//            // NUMBER
//                Types.BIGINT -> "BIGINT" // BITSERIAL
//                Types.SMALLINT -> "SMALLINT"
//                Types.INTEGER -> "INTEGER" // SERIAL
//                Types.NUMERIC -> "NUMERIC"
//                Types.DECIMAL -> "DECIMAL"
//                Types.DOUBLE -> "DOUBLE PRECISION"
//                Types.REAL -> "REAL"
//
//            // LETTER
//                Types.CHAR -> "CHARACTER"
//                Types.VARCHAR -> "VARCHAR"
//                Types.LONGVARCHAR -> "TEXT"
//                Types.LONGNVARCHAR -> "TEXT"
//
//            // BINARY
//                Types.LONGVARBINARY -> "BYTEA"
//                Types.BINARY -> "BIT VARYING"
//                Types.BLOB -> "BLOB"
//
//            // DATE AND TIME
//                Types.DATE -> "DATE"
//                Types.TIME -> "TIME"
//                Types.TIMESTAMP -> "TIMESTAMP" // TIME WITH TIMEZONE, TIMESTAMP WITH TIMEZONE
//
//                Types.SQLXML -> "XML"
//                else -> throw Exception()
//            }
//        }

    fun index() {
        indexList.add(Index())
    }

    var nullable = true


    abstract val sqlDefault: String?

    abstract val hasDefault: Boolean

    var referenceTable: String? = null
    var referenceColumn: String? = null

    val hasReference: Boolean
        get() {
            return !(
                    referenceTable.isNullOrBlank() ||
                            referenceColumn.isNullOrBlank()
                    )
        }
}