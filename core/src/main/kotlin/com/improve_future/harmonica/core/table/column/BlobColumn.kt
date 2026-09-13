package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.toHexString

internal class BlobColumn(name: String) : AbstractColumn(name) {
    var default: ByteArray? = null
        set(value) {
            field = value
            sqlDefault = value?.let { "E'\\\\x" + it.toHexString() + "'" }
        }

    override var sqlDefault: String? = null
}
