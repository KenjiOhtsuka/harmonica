package com.improve_future.harmonica.core.table.column

internal class VarcharColumn(name: String) : AbstractColumn(name) {
    var size: Int? = null
    var default: String? = null
    override val hasDefault: Boolean
        get() = default != null
    override val sqlDefault: String?
        get() = default?.let { "'$default'" }
}