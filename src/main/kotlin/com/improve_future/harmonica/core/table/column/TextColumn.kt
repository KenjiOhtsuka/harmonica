package com.improve_future.harmonica.core.table.column

class TextColumn(name: String) : AbstractColumn(name, java.sql.Types.LONGVARCHAR) {
    override val defaultForSql: String?
        get() = default
    override val hasDefault: Boolean
        get() = default != null
    var default: String? = null
}