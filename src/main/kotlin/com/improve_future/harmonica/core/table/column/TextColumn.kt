package com.improve_future.harmonica.core.table.column

internal class TextColumn(name: String) : AbstractColumn(name) {
    override val sqlDefault: String?
        get() = default?.let { "'$default'" }
    override val hasDefault: Boolean
        get() = default != null
    var default: String? = null
}