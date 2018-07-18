package com.improve_future.harmonica.core.table.column

import java.util.*

internal class TimestampColumn(name: String) : AbstractColumn(name, java.sql.Types.TIMESTAMP) {
    var default: Date? = null
    override val sqlDefault: String? = ""
    override val hasDefault = default != null
}