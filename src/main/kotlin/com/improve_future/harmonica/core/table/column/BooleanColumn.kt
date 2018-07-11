package com.improve_future.harmonica.core.table.column


class BooleanColumn(name: String) : AbstractColumn(name, java.sql.Types.BOOLEAN) {
    override val sqlDefault: String?
        get() = default?.let { if (it) "TRUE" else "FALSE" }
    var default: Boolean? = null
    override val hasDefault: Boolean
        get() = default != null

}