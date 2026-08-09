package com.improve_future.harmonica.core.table.column

// A database BigInteger can be bigger than Long, but for now we limit it to the
// size of a Long
internal class BigIntegerColumn(name: String) : AbstractColumn(name) {
    override var sqlDefault: String? = null

    var default: Long?
        get() = sqlDefault?.toLongOrNull()
        set(value) {
            sqlDefault = value?.toString()
        }

    var unsigned = false
}