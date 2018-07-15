package com.improve_future.harmonica.core.table.column

import java.text.SimpleDateFormat
import java.util.*

class DateColumn(name: String) : AbstractColumn(name, java.sql.Types.DATE) {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    var default: Date? = null

    override val hasDefault: Boolean
        get() = default != null
    override val sqlDefault: String?
        get() = default?.let { "'" + simpleDateFormat.format(it) + "'" }
}