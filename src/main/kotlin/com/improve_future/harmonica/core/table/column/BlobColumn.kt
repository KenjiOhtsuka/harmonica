package com.improve_future.harmonica.core.table.column

import org.jetbrains.kotlin.daemon.common.toHexString

internal class BlobColumn(name: String) : AbstractColumn(name, java.sql.Types.BLOB) {
    var default: ByteArray? = null
    override val sqlDefault
        get() = default?.let { "E'\\\\x" + it.toHexString() + "'" }
    override val hasDefault
        get() = default != null
}