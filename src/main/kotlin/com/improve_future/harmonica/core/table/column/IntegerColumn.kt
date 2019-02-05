package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.RawSql

internal class IntegerColumn(name: String) : AbstractColumn(name) {
    override var sqlDefault: String? = null

    var default: Long?
        get() = sqlDefault?.toLongOrNull()
        set(value) {
            sqlDefault = default?.toString()
        }

    var unsigned = false
}