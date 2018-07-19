package com.improve_future.harmonica.core.table.column

internal class TimestampColumn(name: String) :
    AbstractDateTimeColumn(name, java.sql.Types.TIMESTAMP) {
    var withTimeZone = false
}