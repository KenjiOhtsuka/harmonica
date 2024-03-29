/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.adapter.DbAdapter
import com.improve_future.harmonica.core.adapter.MySqlAdapter
import com.improve_future.harmonica.core.adapter.OracleAdapter
import com.improve_future.harmonica.core.adapter.PostgreSqlAdapter
import com.improve_future.harmonica.core.adapter.SqliteAdapter
import com.improve_future.harmonica.core.table.IndexMethod
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@MigrationDsl
abstract class AbstractMigration {
    lateinit var connection: ConnectionInterface
    internal var tableNameIsInPluralForm = false
    var dispSql = false
    var isReview = false

    val config
        get() = connection.config

    private val adapter: DbAdapter by lazy {
        when (connection.config.dbms) {
            Dbms.SQLite -> SqliteAdapter(connection)
            Dbms.MySQL -> MySqlAdapter(connection)
            Dbms.Oracle -> OracleAdapter(connection)
            else -> PostgreSqlAdapter(connection)
        }.also {
            it.dispSql = dispSql
            it.isReview = isReview
        }
    }

    @MigrationDsl
    fun createTable(name: String, block: TableBuilder.() -> Unit) {
        println("Create Table: $name")

        adapter.createTable(name) {
            this.tableNameIsInPluralForm =
                this@AbstractMigration.tableNameIsInPluralForm
            block()
        }
    }

    fun dropTable(name: String) {
        println("Drop Table: $name")
        adapter.dropTable(name)
    }

    fun removeColumn(tableName: String, columnName: String) {
        println("Remove Column: $tableName $columnName")
        adapter.removeColumn(tableName, columnName)
    }

    /**
     * Add new column to existing table
     *
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL.
     */
    private fun addColumn(
        tableName: String, column: AbstractColumn,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val option = AddingColumnOption().also {
            it.first = first
            it.justBeforeColumn = justBeforeColumnName
        }
        println("Add column: $tableName ${column.name}")
        adapter.addColumn(tableName, column, option)
    }

    /**
     * Add new integer column to existing table.
     *
     * @param tableName Table name.
     * @param columnName Column name.
     * @param nullable
     * @param default
     * @param unsigned Valid only for MySQL.
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addIntegerColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: Long? = null,
        unsigned: Boolean = false,
        first: Boolean = false,
        justBeforeColumnName: String? = null
    ) {
        val integerColumn = IntegerColumn(columnName)
        integerColumn.also {
            it.nullable = nullable
            it.default = default
            it.unsigned = unsigned
        }
        addColumn(tableName, integerColumn, first, justBeforeColumnName)
    }

    /**
     * Add new integer column to existing table.
     *
     * @param tableName Table name.
     * @param columnName Column name.
     * @param nullable
     * @param default
     * @param unsigned Valid only for MySQL.
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addIntegerColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: RawSql,
        unsigned: Boolean = false,
        first: Boolean = false,
        justBeforeColumnName: String? = null
    ) {
        val integerColumn = IntegerColumn(columnName)
        integerColumn.also {
            it.nullable = nullable
            it.sqlDefault = default.sql
            it.unsigned = unsigned
        }
        addColumn(tableName, integerColumn, first, justBeforeColumnName)
    }

    /**
     * Add new big integer column to existing table.
     *
     * @param tableName Table name.
     * @param columnName Column name.
     * @param nullable
     * @param default
     * @param unsigned Valid only for MySQL.
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addBigIntegerColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: Long? = null,
        unsigned: Boolean = false,
        first: Boolean = false,
        justBeforeColumnName: String? = null
    ) {
        val bigIntegerColumn = BigIntegerColumn(columnName)
        bigIntegerColumn.also {
            it.nullable = nullable
            it.default = default
            it.unsigned = unsigned
        }
        addColumn(tableName, bigIntegerColumn, first, justBeforeColumnName)
    }

    /**
     * Add new big integer column to existing table.
     *
     * @param tableName Table name.
     * @param columnName Column name.
     * @param nullable
     * @param default
     * @param unsigned Valid only for MySQL.
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addBigIntegerColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: RawSql,
        unsigned: Boolean = false,
        first: Boolean = false,
        justBeforeColumnName: String? = null
    ) {
        val integerColumn = BigIntegerColumn(columnName)
        integerColumn.also {
            it.nullable = nullable
            it.sqlDefault = default.sql
            it.unsigned = unsigned
        }
        addColumn(tableName, integerColumn, first, justBeforeColumnName)
    }

    /**
     * Add new decimal column to existing table.
     *
     * @param first You add column at first of the columns (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be add just after
     * (valid only for MySQL)
     */
    fun addDecimalColumn(
        tableName: String, columnName: String,
        precision: Int? = null, scale: Int? = null,
        nullable: Boolean = true, default: Double? = null,
        first: Boolean = false,
        justBeforeColumnName: String? = null
    ) {
        val decimalColumn = DecimalColumn(columnName).also {
            it.precision = precision
            it.scale = scale
            it.nullable = nullable
            it.default = default
        }
        addColumn(tableName, decimalColumn, first, justBeforeColumnName)
    }

    /**
     * Add new decimal column to existing table.
     *
     * @param first You add column at first of the columns (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be add just after
     * (valid only for MySQL)
     */
    fun addDecimalColumn(
        tableName: String, columnName: String,
        precision: Int? = null, scale: Int? = null,
        nullable: Boolean = true, default: RawSql,
        first: Boolean = false,
        justBeforeColumnName: String? = null
    ) {
        val decimalColumn = DecimalColumn(columnName).also {
            it.precision = precision
            it.scale = scale
            it.nullable = nullable
            it.sqlDefault = default.sql
        }
        addColumn(tableName, decimalColumn, first, justBeforeColumnName)
    }

    /**
     * Add new varchar column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param size
     * @param nullable
     * @param default
     * @param first You add column at first of the columns (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be add just after
     * (valid only for MySQL)
     */
    fun addVarcharColumn(
        tableName: String, columnName: String, size: Int? = null,
        nullable: Boolean = true, default: String? = null,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val varcharColumn = VarcharColumn(columnName)
        varcharColumn.also {
            it.size = size
            it.nullable = nullable
            it.default = default
        }
        addColumn(tableName, varcharColumn, first, justBeforeColumnName)
    }

    /**
     * Add new varchar column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param size
     * @param nullable
     * @param default
     * @param first You add column at first of the columns (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be add just after
     * (valid only for MySQL)
     */
    fun addVarcharColumn(
        tableName: String, columnName: String, size: Int? = null,
        nullable: Boolean = true, default: RawSql,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val varcharColumn = VarcharColumn(columnName)
        varcharColumn.also {
            it.size = size
            it.nullable = nullable
            it.sqlDefault = default.sql
        }
        addColumn(tableName, varcharColumn, first, justBeforeColumnName)
    }


    /**
     * Add new boolean column to existing table.
     *
     * In PostgreSQL, BOOLEAN column will be added.
     * In MySQL, TINYINT column will be added.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the columns (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addBooleanColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: Boolean? = null,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val booleanColumn = BooleanColumn(columnName)
        booleanColumn.also {
            it.nullable = nullable
            it.default = default
        }
        addColumn(tableName, booleanColumn, first, justBeforeColumnName)
    }

    /**
     * Add new boolean column to existing table.
     *
     * In PostgreSQL, BOOLEAN column will be added.
     * In MySQL, TINYINT column will be added.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the columns (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addBooleanColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: RawSql,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val booleanColumn = BooleanColumn(columnName)
        booleanColumn.also {
            it.nullable = nullable
            it.sqlDefault = default.sql
        }
        addColumn(tableName, booleanColumn, first, justBeforeColumnName)
    }

    /**
     * Add new date column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addDateColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: Date,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val dateColumn = DateColumn(columnName).also {
            it.nullable = nullable
            it.defaultDate = default
        }
        addColumn(tableName, dateColumn, first, justBeforeColumnName)
    }

    /**
     * Add new date column to existing table, with String default value.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default Must be formatted as yyyy-MM-dd
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addDateColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: String? = null,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val dateColumn = DateColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        }
        addColumn(tableName, dateColumn, first, justBeforeColumnName)
    }

    /**
     * Add new date column to existing table, with String default value.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addDateColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: LocalDate,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val dateColumn = DateColumn(columnName).also {
            it.nullable = nullable
            it.defaultLocalDate = default
        }
        addColumn(tableName, dateColumn, first, justBeforeColumnName)
    }

    /**
     * Add new date column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addDateColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: RawSql,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val dateColumn = DateColumn(columnName).also {
            it.nullable = nullable
            it.sqlDefault = default.sql
        }
        addColumn(tableName, dateColumn, first, justBeforeColumnName)
    }

    /**
     * Add new text column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTextColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: String? = null,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val textColumn = TextColumn(columnName)
        textColumn.also {
            it.nullable = nullable
            it.default = default
        }
        addColumn(tableName, textColumn, first, justBeforeColumnName)
    }

    /**
     * Add new text column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTextColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: RawSql,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val textColumn = TextColumn(columnName)
        textColumn.also {
            it.nullable = nullable
            it.sqlDefault = default.sql
        }
        addColumn(tableName, textColumn, first, justBeforeColumnName)
    }

    /**
     * Add new BLOB column to existing table.
     *
     * ## PostgreSQL
     *
     * add BYTEA column instead, because PstgreSQL doesn't have BLOB type.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default Not valid for MySQL
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addBlobColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: ByteArray? = null,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val blobColumn = BlobColumn(columnName)
        blobColumn.also {
            it.nullable = nullable
            it.default = default
        }
        addColumn(tableName, blobColumn, first, justBeforeColumnName)
    }

    /**
     * Add new BLOB column to existing table.
     *
     * ## PostgreSQL
     *
     * add BYTEA column instead, because PstgreSQL doesn't have BLOB type.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default Not valid for MySQL
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addBlobColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: RawSql,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val blobColumn = BlobColumn(columnName)
        blobColumn.also {
            it.nullable = nullable
            it.sqlDefault = default.sql
        }
        addColumn(tableName, blobColumn, first, justBeforeColumnName)
    }

    /**
     * Add new TIME column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param withTimeZone Valid only in PostgreSQL
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTimeColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: LocalTime,
        withTimeZone: Boolean = false,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val timeColumn = TimeColumn(columnName).also {
            it.nullable = nullable
            it.defaultLocalTime = default
            it.withTimeZone = withTimeZone
        }
        addColumn(tableName, timeColumn, first, justBeforeColumnName)
    }

    /**
     * Add new TIME column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default Must be formatted as `HH:MM:ss.SSS`.
     * @param withTimeZone Valid only for PostgreSQL.
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTimeColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: String? = null,
        withTimeZone: Boolean = false,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val timeColumn = TimeColumn(columnName).also {
            it.nullable = nullable
            it.default = default
            it.withTimeZone = withTimeZone
        }
        addColumn(tableName, timeColumn, first, justBeforeColumnName)
    }

    /**
     * Add new TIME column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param withTimeZone Valid only for PostgreSQL
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTimeColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: Date,
        withTimeZone: Boolean = false,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val timeColumn = TimeColumn(columnName).also {
            it.nullable = nullable
            it.defaultDate = default
            it.withTimeZone = withTimeZone
        }
        addColumn(tableName, timeColumn, first, justBeforeColumnName)
    }

    /**
     * Add new TIME column to existing table.
     *
     * @param tableName Table name.
     * @param columnName Column name.
     * @param nullable
     * @param default
     * @param withTimeZone Valid only for PostgreSQL
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTimeColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: RawSql,
        withTimeZone: Boolean = false,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val timeColumn = TimeColumn(columnName).also {
            it.nullable = nullable
            it.sqlDefault = default.sql
            it.withTimeZone = withTimeZone
        }
        addColumn(tableName, timeColumn, first, justBeforeColumnName)
    }

    /**
     * Add new DATETIME column to existing table.
     *
     * ## PostgreSQL
     *
     * TIMESTAMP column will be added instead.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default Must be formed like yyyy-MM-dd HH:MM:SS
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addDateTimeColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: String? = null,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val dateTimeColumn = DateTimeColumn(columnName).also {
            it.nullable = nullable
            it.default = default
        }
        addColumn(tableName, dateTimeColumn, first, justBeforeColumnName)
    }

    /**
     * Add new DATETIME column to existing table.
     *
     * In PostgreSQL, TIMESTAMP column will be added instead,
     * becuse PostgreSQL.doesn't have DATETIME type.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addDateTimeColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: Date,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val dateTimeColumn = DateTimeColumn(columnName).also {
            it.nullable = nullable
            it.defaultDate = default
        }
        addColumn(tableName, dateTimeColumn, first, justBeforeColumnName)
    }

    /**
     * Add new DATETIME column to existing table.
     *
     * In PostgreSQL, TIMESTAMP column will be added instead,
     * becuse PostgreSQL.doesn't have DATETIME type.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addDateTimeColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: LocalDateTime,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val dateTimeColumn = DateTimeColumn(columnName).also {
            it.nullable = nullable
            it.defaultLocalDateTime = default
        }
        addColumn(tableName, dateTimeColumn, first, justBeforeColumnName)
    }

    /**
     * Add new DATETIME column to existing table.
     *
     * In PostgreSQL, TIMESTAMP column will be added instead,
     * becuse PostgreSQL.doesn't have DATETIME type.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addDateTimeColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: RawSql,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val dateTimeColumn = DateTimeColumn(columnName).also {
            it.nullable = nullable
            it.sqlDefault = default.sql
        }
        addColumn(tableName, dateTimeColumn, first, justBeforeColumnName)
    }

    /**
     * Add new DATETIME column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default Must be formatted as `yyyy-MM-dd HH:mm:SS`.
     * @param withTimeZone Valid only in PostgreSQL
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTimestampColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: String? = null,
        withTimeZone: Boolean = true,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val timestampColumn = TimestampColumn(columnName).also {
            it.nullable = nullable
            it.default = default
            it.withTimeZone = withTimeZone
        }
        addColumn(tableName, timestampColumn, first, justBeforeColumnName)
    }

    /**
     * Add new DATETIME column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param withTimeZone Valid only in PostgreSQL
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTimestampColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: Date,
        withTimeZone: Boolean = true,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val timestampColumn = TimestampColumn(columnName).also {
            it.nullable = nullable
            it.defaultDate = default
            it.withTimeZone = withTimeZone
        }
        addColumn(tableName, timestampColumn, first, justBeforeColumnName)
    }

    /**
     * Add new DATETIME column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param withTimeZone Valid only in PostgreSQL.
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTimestampColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: LocalDateTime,
        withTimeZone: Boolean = true,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val timestampColumn = TimestampColumn(columnName).also {
            it.nullable = nullable
            it.defaultLocalDateTime = default
            it.withTimeZone = withTimeZone
        }
        addColumn(tableName, timestampColumn, first, justBeforeColumnName)
    }

    /**
     * Add new DATETIME column to existing table.
     *
     * @param tableName
     * @param columnName
     * @param nullable
     * @param default
     * @param withTimeZone Valid only in PostgreSQL.
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * valid only for MySQL
     */
    fun addTimestampColumn(
        tableName: String, columnName: String,
        nullable: Boolean = false, default: RawSql,
        withTimeZone: Boolean = true,
        first: Boolean = false, justBeforeColumnName: String? = null
    ) {
        val timestampColumn = TimestampColumn(columnName).also {
            it.nullable = nullable
            it.sqlDefault = default.sql
            it.withTimeZone = withTimeZone
        }
        addColumn(tableName, timestampColumn, first, justBeforeColumnName)
    }

    /**
     * Create Index
     *
     * @param tableName Table name.
     * @param columnName Column name.
     * @param unique `true` for unique index. The default value is `false`
     * @param method `null` means database default.
     */
    fun createIndex(
        tableName: String, columnName: String,
        unique: Boolean = false, method: IndexMethod? = null
    ) {
        println("Add Index: $tableName $columnName")
        adapter.createIndex(tableName, arrayOf(columnName), unique, method)
    }

    /**
     * Create Index
     *
     * @param tableName Table name.
     * @param columnNameArray Column names.
     * @param unique `true` for unique index. The default value is `false`
     * @param method `null` means database default.
     */
    fun createIndex(
        tableName: String, columnNameArray: Array<String>,
        unique: Boolean = false, method: IndexMethod? = null
    ) {
        println("Add Index: $tableName (${columnNameArray.joinToString(", ")})")
        adapter.createIndex(tableName, columnNameArray, unique, method)
    }

    /**
     * Create Index
     *
     * @param tableName Table name.
     * @param columnNameCollection Column name collection (List, Set).
     * @param unique `true` for unique index. The default value is `false`
     * @param method `null` means database default.
     */
    fun createIndex(
        tableName: String, columnNameCollection: Collection<String>,
        unique: Boolean = false, method: IndexMethod? = null
    ) = createIndex(
        tableName,
        columnNameCollection.toTypedArray(),
        unique,
        method
    )

//    fun createIndex(
//        tableName: String, vararg columnNameArray: String,
//        unique: Boolean = false, method: IndexMethod? = null, a :Int
//    ) {
//        createIndex(tableName, columnNameArray as Array<String>, unique, method)
//    }

    /**
     * Drop Index
     */
    fun dropIndex(tableName: String, indexName: String) {
        println("Drop Index: $tableName $indexName")
        adapter.dropIndex(tableName, indexName)
    }

    /**
     * Rename table.
     *
     * @param oldName Old table name.
     * @param newName New table name.
     */
    fun renameTable(oldName: String, newName: String) {
        println("Rename Table: $oldName => $newName")
        adapter.renameTable(oldName, newName)
    }

    /**
     * Rename column
     *
     * For MySQL, this can be used from version 8.0.
     *
     * @param tableName Table name
     * @param oldColumnName Old column name
     * @param newColumnName New column name
     */
    fun renameColumn(
        tableName: String, oldColumnName: String, newColumnName: String
    ) {
        println("Rename Column: $tableName.$oldColumnName => $newColumnName")
        adapter.renameColumn(tableName, oldColumnName, newColumnName)
    }

    /**
     * Rename index.
     *
     * @param tableName Table name.
     * @param oldIndexName Old index name
     * @param newIndexName New index name
     */
    fun renameIndex(
        tableName: String, oldIndexName: String, newIndexName: String
    ) {
        println("Rename Index: $oldIndexName => $newIndexName")
        adapter.renameIndex(tableName, oldIndexName, newIndexName)
    }

    /**
     * Add foreign key constraint.
     *
     * @param tableName
     * @param columnName
     * @param referencedTableName
     * @param referencedColumnName
     */
    fun addForeignKey(
        tableName: String,
        columnName: String,
        referencedTableName: String,
        referencedColumnName: String = "id"
    ) {
        println("Add foreign key: $tableName.$columnName - $referencedTableName.$referencedColumnName")
        adapter.addForeignKey(
            tableName, columnName,
            referencedTableName, referencedColumnName
        )
    }

    /**
     * Drop foreign key constraint.
     *
     * @param tableName
     * @param columnName
     * @param keyConstraintName Foreign key constraint name.
     */
    fun dropForeignKey(
        tableName: String,
        columnName: String,
        keyConstraintName: String
    ) {
        println("Drop foreign key: $tableName.$columnName ($keyConstraintName)")
        adapter.dropForeignKey(
            tableName, columnName, keyConstraintName
        )
    }

    /**
     * Drop foreign key constraint.
     *
     * @param tableName
     * @param columnName
     */
    fun dropForeignKey(tableName: String, columnName: String) {
        println("Drop foreign key: $tableName.$columnName")
        adapter.dropForeignKey(tableName, columnName)
    }

    /**
     * Execute SQL
     *
     * @param sql
     */
    fun executeSql(sql: String) {
        println("Execute SQL")
        connection.execute(sql)
    }

    /**
     * Migrate up
     */
    open fun up() {}

    /**
     * Migrate down
     */
    open fun down() {}
}