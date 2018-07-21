package com.improve_future.harmonica.core.table.column

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal abstract class AbstractDateTimeColumn(
    name: String,
    sqlType: Type
) : AbstractColumn(name, sqlType) {
    private val formatter = DateTimeFormatter.ofPattern(
        "yyyy-M[M]-d[d][ H[H]:m[m][:s[s]][.SSS][ zzz]]"
    )
    var default: String?
        get() {
            return defaultLocalDateTime?.toString()
        }
        set(value) {
            defaultLocalDateTime = value?.let {
                LocalDateTime.parse(it, formatter)
            }
        }
    var defaultLocalDateTime: LocalDateTime? = null
    var defaultDate: Date?
        get() {
            return defaultLocalDateTime?.let {
                Date.from(ZonedDateTime.of(it, ZoneId.systemDefault()).toInstant())
            }
        }
        set(value) {
            defaultLocalDateTime = value?.let {
                LocalDateTime.ofInstant(it.toInstant(), ZoneId.systemDefault())
            }
        }

    override val sqlDefault
        get() = "'" + defaultLocalDateTime.toString() + "'"
    override val hasDefault
        get() = defaultLocalDateTime != null
}