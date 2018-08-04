package com.improve_future.harmonica.core.table.column

internal class IntegerColumn(name: String) : AbstractColumn(name) {
    override val hasDefault: Boolean
        get() = default != null
    override val sqlDefault: String?
        get() = default?.toString()
    var default: Long? = null

    var unsigned = false
}