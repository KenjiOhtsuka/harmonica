package com.improve_future.harmonica.core.table.column

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class DateColumn(name: String) : AbstractColumn(name, java.sql.Types.DATE) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    var default: String?
        get() {
            return defaultDate?.let { dateFormat.format(defaultDate) }
        }
        set(value) {
            defaultDate = value?.let { dateFormat.parse(value) }
        }
    var defaultDate: Date? = null
    var defaultLocalDate: LocalDate?
        get() {
            defaultDate ?: return null
            val calendar = Calendar.getInstance()
            calendar.time = defaultDate
            return LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH + 1),
                calendar.get(Calendar.DAY_OF_MONTH))
        }
        set(value) {
            default = value?.toString()
        }

    override val hasDefault: Boolean
        get() = default != null
    override val sqlDefault: String?
        get() = "'$default'"
}