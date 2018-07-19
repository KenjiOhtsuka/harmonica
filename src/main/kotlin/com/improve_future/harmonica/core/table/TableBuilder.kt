package com.improve_future.harmonica.core.table

import com.improve_future.harmonica.core.MigrationDsl
import com.improve_future.harmonica.core.table.column.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@MigrationDsl
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
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
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     * @param default Invalid for MySQL
     */
    fun blob(
        columnName: String,
        nullable: Boolean = true,
        default: ByteArray? = null
    ) {
        addColumn(BlobColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        })
    }

    /**
     * add Time column
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     */
    fun time(
        columnName: String,
        nullable: Boolean = true,
        default: LocalTime? = null,
        withTimeZone: Boolean = false
    ) {
        addColumn(TimeColumn(columnName).also {
            it.nullable = nullable
            it.defaultLocalTime = default
            it.withTimeZone = withTimeZone
        })
    }

    /**
     * add Time column
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     * @param default Format as HH:mm:ss[.SSS][ zzz].
     * `22:21:22.123`, `22:21:22`, `12:23:34` can be accepted.
     */
    fun time(
        columnName: String,
        nullable: Boolean = true,
        default: String,
        withTimeZone: Boolean = false
    ) {
        addColumn(TimeColumn(columnName).also {
            it.nullable = nullable
            it.default = default
            it.withTimeZone = withTimeZone
        })
    }

    /**
     * add Time column
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     */
    fun time(
        columnName: String,
        nullable: Boolean = true,
        default: Date,
        withTimeZone: Boolean = false
    ) {
        addColumn(TimeColumn(columnName).also {
            it.nullable = nullable
            it.defaultDate = default
            it.withTimeZone = withTimeZone
        })
    }

    /**
     * add TIMESTAMP column
     *
     * ## MySQL
     *
     * If you store the value,
     * and then change the time zone and retrieve the value,
     * the retrieved value is different from the value you stored.
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     * @param withTimeZone Valid only for PostgreSQL.
     */
    fun timestamp(
        columnName: String,
        nullable: Boolean = true,
        default: String? = null,
        withTimeZone: Boolean = true
    ) {
        addColumn(TimestampColumn(columnName).also {
            it.nullable = nullable
            it.default = default
            it.withTimeZone = withTimeZone
        })
    }

    /**
     * add TIMESTAMP column
     *
     * ## MySQL
     *
     * If you store the value,
     * and then change the time zone and retrieve the value,
     * the retrieved value is different from the value you stored.
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     * @param withTimeZone Valid only for PostgreSQL.
     */
    fun timestamp(
        columnName: String,
        nullable: Boolean = true,
        default: Date,
        withTimeZone: Boolean = true
    ) {
        addColumn(TimestampColumn(columnName).also {
            it.nullable = nullable
            it.defaultDate = default
            it.withTimeZone = withTimeZone
        })
    }

    /**
     * add TIMESTAMP column
     *
     * ## MySQL
     *
     * If you store the value,
     * and then change the time zone and retrieve the value,
     * the retrieved value is different from the value you stored.
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     * @param withTimeZone Valid only for PostgreSQL.
     */
    fun timestamp(
        columnName: String,
        nullable: Boolean = true,
        default: LocalDateTime,
        withTimeZone: Boolean = true
    ) {
        addColumn(TimestampColumn(columnName).also {
            it.nullable = nullable
            it.defaultLocalDateTime = default
            it.withTimeZone = withTimeZone
        })
    }

    /**
     * add DATETIME column
     *
     * ## PostgreSQL
     *
     * There is no `DATETIME` type, so add `TIMESTAMP` column, instead.
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     */
    fun datetime(
        columnName: String,
        nullable: Boolean = true,
        default: Date? = null
    ) {
        addColumn(DateTimeColumn(columnName).also {
            it.nullable = nullable
            it.defaultDate = default
        })
    }

    /**
     * add DATETIME column
     *
     * ## PostgreSQL
     *
     * There is no `DATETIME` type, so add `TIMESTAMP` column, instead.
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     */
    fun datetime(
        columnName: String,
        nullable: Boolean = true,
        default: String? = null
    ) {
        addColumn(DateTimeColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        })
    }

    /**
     * add DATETIME column
     *
     * ## PostgreSQL
     *
     * There is no `DATETIME` type, so add `TIMESTAMP` column, instead.
     *
     * @param nullable `false` for `NOT NULL` constraint. The default value is `true`.
     */
    fun datetime(
        columnName: String,
        nullable: Boolean = true,
        default: LocalDateTime
    ) {
        addColumn(DateTimeColumn(columnName).also {
            it.nullable = nullable
            it.defaultLocalDateTime = default
        })
    }
}