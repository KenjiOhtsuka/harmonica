/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Harmonica.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.Index
import com.improve_future.harmonica.core.RawSql


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


    abstract var sqlDefault: String?

    val hasDefault: Boolean
        get() = sqlDefault != null

    var referenceTable: String? = null
    var referenceColumn: String? = null

    val hasReference: Boolean
        get() {
            return !(
                    referenceTable.isNullOrBlank() ||
                            referenceColumn.isNullOrBlank()
                    )
        }

    var comment: String? = null

    val hasComment: Boolean
        get() {
            return comment != null
        }
}