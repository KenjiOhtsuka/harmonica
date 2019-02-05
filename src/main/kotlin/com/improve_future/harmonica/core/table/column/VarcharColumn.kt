package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.RawSql

internal class VarcharColumn(name: String) : AbstractColumn(name) {
    var size: Int? = null

    var default: String? = null
        set(value) {
            field = value
            sqlDefault = value?.let { "'$it'" }
        }

    override var sqlDefault: String? = null
}