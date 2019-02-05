package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.RawSql

internal class VarcharColumn(name: String) : AbstractColumn(name) {
    var size: Int? = null

    var default: String?
        get() = sqlDefault
        set(value) {
            sqlDefault = "'$default'"
        }

    override var sqlDefault: String? = null
}