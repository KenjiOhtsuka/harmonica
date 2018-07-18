package com.improve_future.harmonica.core.table.column

class BlobColumn(name: String) : AbstractColumn(name, java.sql.Types.BLOB) {
    var default: ByteArray? = null
    override val sqlDefault = "" // ToDo
    override val hasDefault = default != null
}