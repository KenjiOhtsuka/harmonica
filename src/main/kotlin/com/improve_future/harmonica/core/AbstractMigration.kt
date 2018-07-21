package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.adapter.DbAdapter
import com.improve_future.harmonica.core.adapter.MySqlAdapter
import com.improve_future.harmonica.core.adapter.PostgreSqlAdapter
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@MigrationDsl
abstract class AbstractMigration {
    internal lateinit var connection: ConnectionInterface

    private val adapter: DbAdapter by lazy {
        when (connection.config.dbms) {
            Dbms.MySQL -> MySqlAdapter(connection)
            else -> PostgreSqlAdapter(connection)
        }
    }

    @MigrationDsl
    fun createTable(name: String, block: TableBuilder.() -> Unit) {
        println("Create Table: $name")
        adapter.createTable(name, block)
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
     * @param first You add column at first of the column (valid only for MySQL)
     * @param justBeforeColumnName Column name the new column to be added just after.
     * (valid only for MySQL)
     */
    fun addIntegerColumn(
        tableName: String, columnName: String,
        nullable: Boolean = true, default: Long? = null,
        first: Boolean = false,
        justBeforeColumnName: String? = null
    ) {
        val integerColumn = IntegerColumn(columnName)
        integerColumn.also {
            it.nullable = nullable
            it.default = default
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
     * Create Index
     */
    fun createIndex(tableName: String, columnName: String) {
        println("Add Index: $tableName $columnName")
        adapter.createIndex(tableName, columnName)
    }

//    fun createIndex(tableName: String, columnNameArray: Array<String>) {
//        // ToDo
//        // println("Add Index:")
//    }

    /**
     * Drop Index
     */
    fun dropIndex(tableName: String, indexName: String) {
        adapter.dropIndex(tableName, indexName)
        println("Drop Index: $tableName $indexName")
    }

    /**
     * Rename table.
     *
     * @param oldTableName
     * @param newTableName
     */
    fun renameTable(oldTableName: String, newTableName: String) {
        adapter.renameTable(oldTableName, newTableName)
        println("Rename Table: $oldTableName => $newTableName")
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