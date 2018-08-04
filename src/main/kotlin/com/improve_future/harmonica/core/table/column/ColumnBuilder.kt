package com.improve_future.harmonica.core.table.column

class ColumnBuilder internal constructor(
    internal val column: AbstractColumn
) {
    fun refer(tableName: String, columnName: String? = null) {
        column.let {
            it.referenceTable = tableName
            it.referenceColumn = columnName
        }
    }

    internal fun build(): AbstractColumn {
        return column
    }
}