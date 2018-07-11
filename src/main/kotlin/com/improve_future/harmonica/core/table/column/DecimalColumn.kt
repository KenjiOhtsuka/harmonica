package com.improve_future.harmonica.core.table.column

class DecimalColumn(name: String) : AbstractColumn(name, java.sql.Types.DECIMAL) {
    override val defaultForSql: String?
        get() = default.toString()
    var default: Boolean? = null
    override val hasDefault: Boolean
        get() = default != null

}