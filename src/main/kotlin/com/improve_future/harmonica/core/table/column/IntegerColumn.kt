package com.improve_future.harmonica.core.table.column

class IntegerColumn(name: String) : AbstractColumn(name, java.sql.Types.INTEGER) {
    var default: Long? = null
}