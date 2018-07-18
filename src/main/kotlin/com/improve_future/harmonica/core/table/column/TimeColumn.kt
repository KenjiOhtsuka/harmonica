package com.improve_future.harmonica.core.table.column

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

internal class TimeColumn(name: String) : AbstractColumn(name, java.sql.Types.TIME) {
    val formatter = DateTimeFormatter.ofPattern("H[H]:m[m][:s[s]][ zzz]")
    var default: String?
        get() {
            return defaultLocalTime.toString()
        }
        set(value) {
            defaultLocalTime = LocalTime.parse(
                value, formatter
            )
        }
    var defaultDate: Date?
        set(value) {
            defaultLocalTime = value?.let {
                LocalDateTime.ofInstant(
                    it.toInstant(), ZoneId.systemDefault()
                ).toLocalTime()
            }
        }
        get() {
            return defaultLocalTime?.let {
                Date.from(
                    it
                        .atDate(LocalDate.now())
                        .atZone(ZoneId.systemDefault()).toInstant())
            }
        }
    var defaultLocalTime: LocalTime? = null
        set(value) {
            default = value.toString()
        }
    override val sqlDefault = "'$default'"
    override val hasDefault = default != null
}