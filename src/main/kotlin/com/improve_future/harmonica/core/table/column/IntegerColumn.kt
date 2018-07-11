package com.improve_future.harmonica.core.table.column

class IntegerColumn(name: String) : AbstractColumn(name, java.sql.Types.INTEGER) {
    override val hasDefault: Boolean
        get() = default != null
    override val sqlDefault: String?
        get() = default?.toString()
    var default: Long? = null

}