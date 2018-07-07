package com.improve_future.harmonica.core.table.column

class VarcharColumn(name: String) : AbstractColumn(name, java.sql.Types.VARCHAR) {
    var size: Int? = null
}