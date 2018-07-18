package com.improve_future.harmonica.core.table

import com.improve_future.harmonica.core.table.column.*
import java.time.LocalDate
import java.util.*

typealias Type = Int

class TableBuilder {
    lateinit var tableName: String
    internal val columnList = mutableListOf<AbstractColumn>()
    /** Specify add auto incremental id column or not */
    var id = true

    private fun addColumn(column: AbstractColumn) {
        columnList.add(column)
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
    ) {
        val decimalColumn = DecimalColumn(columnName).also {
            it.nullable = nullable
            it.default = default
            it.precision = precision
            it.scale = scale
        }
        addColumn(decimalColumn)
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
    ) {
        addColumn(IntegerColumn(columnName).also {
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
    ) {
        addColumn(VarcharColumn(columnName).also {
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
    ) {
        varchar(columnName, size, nullable, default)
    }

    /**
     * add boolean column
     *
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun boolean(
        columnName: String,
        nullable: Boolean = true,
        default: Boolean? = null
    ) {
        addColumn(BooleanColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        })
    }

    /**
     * add date column of `java.util.Date` default value.
     *
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun date(
        columnName: String,
        nullable: Boolean = true,
        default: Date
    ) {
        val dateColumn = DateColumn(columnName).also {
            it.nullable = nullable
            it.defaultDate = default
        }
        addColumn(dateColumn)
    }

    /**
     * add date column of no default value or `String` default value
     *
     * @param default Must be formatted as yyyy-MM-dd
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun date(
        columnName: String,
        nullable: Boolean = true,
        default: String? = null
    ) {
        val dateColumn = DateColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        }
        addColumn(dateColumn)
    }

    /**
     * add date column of `java.time.LocalDate` default value
     *
     * @param nullable null constraint. `false` means `NOT NULL` constraint
     */
    fun date(
        columnName: String,
        nullable: Boolean = true,
        default: LocalDate
    ) {
        val dateColumn = DateColumn(columnName).also {
            it.nullable = nullable
            it.defaultLocalDate = default
        }
        addColumn(dateColumn)
    }

    /**
     * add TEXT column, unlimited length string
     *
     * @param default Invalid for MySQL
     */
    fun text(
        columnName: String,
        nullable: Boolean = true,
        default: String? = null
    ) {
        addColumn(TextColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        })
    }

    /**
     * add BLOB column
     *
     * @param default Invalid for MySQL
     */
    fun blog(
        columnName: String,
        nullable: Boolean = true,
        default: ByteArray? = null
    ) {
        addColumn(BlobColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        })
    }
}