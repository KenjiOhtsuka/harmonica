package com.improve_future.harmonica.core.table.column

internal class TimestampColumn(name: String) :
    AbstractDateTimeColumn(name), TimeZoneInterface {
    override var withTimeZone = false
}