package com.improve_future.harmonica.core.table.column

class TextColumn(name: String) : AbstractColumn(name, java.sql.Types.LONGVARCHAR) {
    var default: String? = null
}