package com.improve_future.harmonica.core.table

import com.improve_future.harmonica.core.table.column.*

typealias Type = Int

class TableBuilder {
    lateinit var tableName: String
    val columnList = mutableListOf<AbstractColumn>()
    /** Specify add auto incremental id column or not */
    var id = true

    private fun addColumn(column: AbstractColumn): AbstractColumn {
        columnList.add(column)
        return column
    }

    /**
     * Add decimal column
     *
     * @param precision The number of digits in the number.
     * @param scale The number of digits to the right of the decimal point in the number.
     * @param nullable null constraint. `false` means `NOT NULL` constraint.
     */
    fun decimal(
        columnName: String,
        precision: Int? = null,
        scale: Int? = null,
        nullable: Boolean = true,
        default: Double? = null
    ): AbstractColumn {
        val decimalColumn = DecimalColumn(columnName).also {
            it.nullable = nullable
            it.default = default
            it.precision = precision
            it.scale = scale
        }
        return addColumn(decimalColumn)
    }

    /**
     * Add integer column.
     *
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun integer(
        columnName: String,
        nullable: Boolean = true,
        default: Long? = null
    ): AbstractColumn {
        return addColumn(IntegerColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        })
    }

    /**
     * add varchar column
     *
     * variable with limit
     *
     * @param size For MySQL, `null` means 255.
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun varchar(
        columnName: String,
        size: Int? = null,
        nullable: Boolean = true,
        default: String? = null
    ): AbstractColumn {
        return addColumn(VarcharColumn(columnName).also {
            it.nullable = nullable
            it.default = default
            it.size = size
        })
    }

    /**
     * add varchar column
     *
     * Alias for varchar.
     *
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun string(
        columnName: String,
        size: Int? = null,
        nullable: Boolean = true,
        default: String? = null
    ): AbstractColumn {
        return varchar(columnName, size, nullable, default)
    }

    /**
     * add boolean column
     *
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun boolean(
        columnName: String,
        nullable: Boolean = false,
        default: Boolean? = null
    ): AbstractColumn {
        return addColumn(BooleanColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        })
    }

    /**
     * add date column
     *
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun date(
        columnName: String,
        nullable: Boolean = true
    ): AbstractColumn {
        val dateColumn = DateColumn(columnName)
        dateColumn.nullable = nullable
        return addColumn(dateColumn)
    }

    /**
     * unlimited length
     *
     * @param default Invalid for MySQL
     */
    fun text(
        columnName: String,
        nullable: Boolean = true,
        default: String? = null
    ): AbstractColumn {
        return addColumn(TextColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        })
    }
}