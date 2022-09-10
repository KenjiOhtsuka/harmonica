/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.table.column

class ColumnBuilder internal constructor(
    internal val column: AbstractColumn
) {
    fun refer(tableName: String, columnName: String? = null): ColumnBuilder {
        column.let {
            it.referenceTable = tableName
            it.referenceColumn = columnName
        }
        return this
    }

    fun comment(text: String): ColumnBuilder {
        column.comment = text
        return this
    }

    internal fun build(): AbstractColumn {
        return column
    }
}