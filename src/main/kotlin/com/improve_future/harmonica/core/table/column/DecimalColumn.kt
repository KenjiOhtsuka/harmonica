package com.improve_future.harmonica.core.table.column

class DecimalColumn(name: String) : AbstractColumn(name, java.sql.Types.DECIMAL) {
    override val sqlDefault: String?
        get() = default.toString()
    var default: Double? = null
    override val hasDefault: Boolean
        get() = default != null

    var precision: Int? = null
    var scale: Int? = null

}