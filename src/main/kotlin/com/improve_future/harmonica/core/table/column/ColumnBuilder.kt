package com.improve_future.harmonica.core.table.column

class ColumnBuilder {
    internal var column: AbstractColumn? = null

    fun refer(tableName: String, columnName: String? = null) {
        column?.let {
            it.referenceTable = tableName
            it.referenceColumn = columnName
        }
    }
}