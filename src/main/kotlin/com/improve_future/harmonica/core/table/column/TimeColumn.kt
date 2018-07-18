package com.improve_future.harmonica.core.table.column

import java.time.LocalTime

class TimeColumn(name: String) : AbstractColumn(name, java.sql.Types.TIME) {
    var default: LocalTime? = null
    override val sqlDefault = default?.toString()
    override val hasDefault = default != null
}