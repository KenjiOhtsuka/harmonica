package com.improve_future.harmonica.core.table.column

internal class TextColumn(name: String) : AbstractColumn(name) {
    override var sqlDefault: String? = null

    var default: String?
        get() = sqlDefault
        set(value) {
            sqlDefault = "'$value'"
        }
}