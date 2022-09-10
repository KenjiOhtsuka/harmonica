/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.table.IndexMethod
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*

internal class OracleAdapter(connection: ConnectionInterface) : DbAdapter(connection) {
    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        var sql = "CREATE TABLE $tableName (\n"
        if (tableBuilder.id) {
            sql += "  id NUMBER GENERATED ALWAYS AS IDENTITY"
            if (tableBuilder.columnList.size > 0) sql += ','
            sql += "\n"
        }
        sql += tableBuilder.columnList.joinToString(",\n") {
            "  " + buildColumnDeclarationForCreateTableSql(it)
        }
        sql += "\n)"
        execute(sql)

        // Add Comments
        if (tableBuilder.comment != null) {
            sql = "COMMENT ON TABLE $tableName IS '${tableBuilder.comment}'"
            execute(sql)
        }
        tableBuilder.columnList.forEach {
            if (it.hasComment) {
                sql = "COMMENT ON COLUMN $tableName.${it.name} IS" +
                        " '${it.comment}'"
                execute(sql)
            }
        }
    }

    internal companion object : DbAdapter.CompanionInterface() {
        private fun buildColumnDeclarationForCreateTableSql(
            column: AbstractColumn
        ): String {
            var sql = column.name + " " + sqlType(column)
            when (column) {
                is VarcharColumn -> {
                    sql +=
                        if (column.size == null) "(255)"
                        else "(" + column.size + ")"
                }
                is DecimalColumn -> {
                    if (column.precision != null) {
                        sql += "(" + column.precision.toString()
                        if (column.scale != null) {
                            sql += ", " + column.scale.toString()
                        }
                        sql += ")"
                    }
                }
//                is TimeZoneInterface -> {
//                    if (column.withTimeZone) {
//                        sql += " WITH TIME ZONE"
//                    }
//                }
            }
            if (!column.nullable) sql += " NOT NULL"
            if (column.hasDefault) {
                sql += " DEFAULT " + column.sqlDefault
            }
//            if (column.hasReference)
//                sql += " REFERENCES ${column.referenceTable} (${column.referenceColumn})"
            return sql
        }

        override fun sqlType(column: AbstractColumn): String {
            return when (column) {
                is IntegerColumn -> "NUMBER"
                is BigIntegerColumn -> "NUMBER"
                is DecimalColumn -> "NUMBER"
                is DateTimeColumn -> "DATE"
                is TimeColumn -> "TIMESTAMP"
                is BooleanColumn -> "NUMBER(1)"
                is TextColumn -> "LONG"
                else -> super.sqlType(column)
            }
        }

        override fun sqlIndexMethod(method: IndexMethod?): String? {
//            return when (method) {
//                IndexMethod.BTree -> "btree"
//                IndexMethod.Hash -> "hash"
//                IndexMethod.Gist -> "gist"
//                IndexMethod.SpGist -> "spgist"
//                IndexMethod.Gin -> "gin"
//                IndexMethod.BRin -> "brin"
//                else -> null
//            }
            return null
        }
    }

    override fun createIndex(
        tableName: String,
        columnNameArray: Array<String>,
        unique: Boolean,
        method: IndexMethod?
    ) {
        var sql = "CREATE"
        if (unique) sql += " UNIQUE"
        // for bitmap index: if (bitmap) sql += " BITMAP"
        sql += " INDEX ${tableName}_${columnNameArray.joinToString("_")}_idx" +
                " ON $tableName"
        //PostgreSqlAdapter.sqlIndexMethod(method)?.let { sql += " USING $it" }
        sql += " (${columnNameArray.joinToString(", ")});"
        execute(sql)
    }

    override fun dropIndex(tableName: String, indexName: String) {
        execute("DROP INDEX $indexName")
    }

    override fun addColumn(
        tableName: String, column: AbstractColumn, option: AddingColumnOption
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renameTable(oldTableName: String, newTableName: String) {
        val sql = "ALTER TABLE $oldTableName RENAME TO $newTableName"
        execute(sql)
    }

    override fun renameIndex(
        tableName: String, oldIndexName: String, newIndexName: String
    ) {
        val sql = "ALTER INDEX $oldIndexName RENAME TO $newIndexName"
        execute(sql)
    }

    override fun addForeignKey(
        tableName: String, columnName: String,
        referencedTableName: String, referencedColumnName: String
    ) {
        var sql = "ALTER TABLE $tableName ADD CONSTRAINT ${tableName}_${columnName}_fkey"
        sql += " FOREIGN KEY ($columnName)"
        sql += " REFERENCES $referencedTableName ($referencedColumnName)"
        execute(sql)
    }

    override fun dropForeignKey(
        tableName: String, columnName: String, keyName: String
    ) {
        val sql = "ALTER TABLE $tableName DROP CONSTRAINT $keyName"
        execute(sql)
    }
}