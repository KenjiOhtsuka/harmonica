package com.improve_future.harmonica.core.table.column

internal class BlobColumn(name: String) : AbstractColumn(name, java.sql.Types.BLOB) {
    var default: ByteArray? = null
    override val sqlDefault = "" // ToDo
    override val hasDefault = default != null
}