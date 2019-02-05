package com.improve_future.harmonica.core.table.column

internal class DateTimeColumn(name: String) :
    AbstractDateTimeColumn(name) {
    override var sqlDefault: String? = null
}