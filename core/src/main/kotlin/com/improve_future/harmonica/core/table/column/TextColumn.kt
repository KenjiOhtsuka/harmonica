package com.improve_future.harmonica.core.table.column

internal class TextColumn(name: String) : AbstractColumn(name) {
    override var sqlDefault: String? = null

    var default: String? = null
        set(value) {
            field = value
            sqlDefault = value?.let { "'$value'" }
        }
}