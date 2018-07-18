package com.improve_future.harmonica.core.table.column

import java.util.*

internal class DateTimeColumn(name: String): AbstractColumn(name, java.sql.Types.TIMESTAMP) {
    var default: Date? = null
    override val sqlDefault = ""
    override val hasDefault = default != null
    // ToDo: with time zone
}