package com.improve_future.harmonica.core.table.column


class BooleanColumn(name: String) : AbstractColumn(name, java.sql.Types.BOOLEAN) {
    var default: Boolean? = null
}