/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.table.IndexMethod
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*

internal class MySqlAdapter(connection: ConnectionInterface) :
    DbAdapter(connection) {
    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        var sql = "CREATE TABLE $tableName (\n"
        if (tableBuilder.id) {
            sql += "  id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY"
            if (tableBuilder.columnList.size > 0) sql += ','
            sql += "\n"
        }
        sql += tableBuilder.columnList.joinToString(",\n") {
            "  " + buildColumnDeclarationForCreateTableSql(it)
        }
        sql += "\n)"
        if (tableBuilder.comment != null)
            sql += "\ncomment='${tableBuilder.comment}'"
        sql += ";"
        execute(sql)
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
                is IntegerColumn -> {
                    if (column.unsigned) {
                        sql += " UNSIGNED"
                    }
                }
            }
            if (!column.nullable) sql += " NOT NULL"
            if (column.hasDefault) {
                when (column) {
                    is TextColumn, is BlobColumn -> Unit
                    else -> {
                        sql += " DEFAULT " + column.sqlDefault
                    }
                }
            }
            if (column.hasReference)
                sql += " REFERENCES ${column.referenceTable} (${column.referenceColumn})"
            if (column.hasComment)
                sql += " COMMENT '${column.comment}'"
            return sql
        }

        override fun sqlIndexMethod(method: IndexMethod?): String? {
            return when (method) {
                IndexMethod.BTree -> "BTREE"
                IndexMethod.Hash -> "HASH"
                else -> null
            }
        }
    }

    override fun createIndex(
        tableName: String, columnNameArray: Array<String>, unique: Boolean,
        method: IndexMethod?
    ) {
        var sql = "CREATE"
        if (unique) sql += " UNIQUE"
        sql += " INDEX ${tableName}_${columnNameArray.joinToString("_")}_idx"
        sqlIndexMethod(method)?.let { sql += " $it" }
        sql += " ON $tableName (${columnNameArray.joinToString(", ")});"
        execute(sql)
    }

    override fun dropIndex(tableName: String, indexName: String) {
        execute("DROP INDEX $indexName ON $tableName;")
    }


    override fun addColumn(
        tableName: String,
        column: AbstractColumn,
        option: AddingColumnOption
    ) {
        var sql = "ALTER TABLE $tableName ADD COLUMN "
        sql += buildColumnDeclarationForCreateTableSql(column)
        sql += ";"
        execute(sql)
    }

    override fun renameTable(oldTableName: String, newTableName: String) {
        val sql = "RENAME TABLE $oldTableName TO $newTableName;"
        execute(sql)
    }


    override fun renameIndex(
        tableName: String, oldIndexName: String, newIndexName: String
    ) {
        val sql = "ALTER TABLE $tableName" +
                " RENAME INDEX $oldIndexName TO $newIndexName"
        execute(sql)
    }

    override fun addForeignKey(
        tableName: String, columnName: String,
        referencedTableName: String, referencedColumnName: String
    ) {
        val sql = "ALTER TABLE $tableName" +
                " ADD CONSTRAINT ${tableName}_${columnName}_fkey" +
                " FOREIGN KEY ($columnName) REFERENCES" +
                " $referencedTableName ($referencedColumnName);"
        execute(sql)
    }

    override fun dropForeignKey(
        tableName: String,
        columnName: String,
        keyName: String
    ) {
        val sql = "ALTER TABLE $tableName" +
                " DROP FOREIGN KEY $keyName;"
        execute(sql)
    }
}