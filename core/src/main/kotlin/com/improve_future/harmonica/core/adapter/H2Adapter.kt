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
import com.improve_future.harmonica.core.table.column.AbstractColumn
import com.improve_future.harmonica.core.table.column.AddingColumnOption
import com.improve_future.harmonica.core.table.column.DecimalColumn
import com.improve_future.harmonica.core.table.column.VarcharColumn

internal class H2Adapter(connection: ConnectionInterface) : DbAdapter(connection) {
    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        var sql = "CREATE TABLE $tableName (\n"

        if (tableBuilder.id) {
            sql += "  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY"

            if (tableBuilder.columnList.size > 0) sql += ','
            sql += '\n'
        }

        sql += tableBuilder.columnList.joinToString(",\n") {
            "  " + buildColumnDeclarationForCreateTableSql(it)
        }
        sql += ");\n"

        if (tableBuilder.comment != null) {
            sql += "\n"
            sql += "comment on table $tableName is '${tableBuilder.comment}'"
        }

        execute(sql)
    }

    internal companion object : DbAdapter.CompanionInterface() {
        private fun buildColumnDeclarationForCreateTableSql(
                column: AbstractColumn
        ): String {
            var sql = "${column.name} ${sqlType(column)}"

            when (column) {
                is VarcharColumn -> sql += (column.size?.let { return "($it)" } ?: "")
                is DecimalColumn -> sql += column.precision?.let {
                    "(" + column.precision + (column.scale?.let {
                        return ", ${column.scale}"
                    } ?: "") + ")"
                } ?: ""
            }

            if (!column.nullable)
                sql += " NOT NULL"
            if (column.hasDefault)
                sql += " DEFAULT " + column.sqlDefault

            if (column.hasReference)
                sql += " REFERENCES ${column.referenceTable} (${column.referenceColumn})"
            if (column.hasComment)
                sql += " COMMENT '${column.comment}'"

            return sql
        }

        override fun sqlIndexMethod(method: IndexMethod?): String? {
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
        sql += " INDEX ${tableName}_${columnNameArray.joinToString("_")}_index"
        sql += "\n  ON $tableName (${columnNameArray.joinToString(",")});"

        execute(sql)
    }

    override fun dropIndex(tableName: String, indexName: String) {
        val sql = "drop index $indexName;"
        execute(sql)
    }

    override fun addColumn(
            tableName: String,
            column: AbstractColumn,
            option: AddingColumnOption
    ) {
        var sql = "ALTER TABLE $tableName\n  ADD "
        sql += buildColumnDeclarationForCreateTableSql(column)
        sql += ";"

        execute(sql)
    }

    override fun renameTable(oldTableName: String, newTableName: String) {
        val sql = "alter table $oldTableName rename to $newTableName"
        execute(sql)
    }

    override fun renameIndex(
            tableName: String,
            oldIndexName: String,
            newIndexName: String
    ) {
        val sql = "ALTER INDEX $oldIndexName RENAME TO $newIndexName"

        execute(sql)
    }

    override fun addForeignKey(
            tableName: String,
            columnName: String,
            referencedTableName: String,
            referencedColumnName: String
    ) {
        val sql = "ALTER TABLE $tableName\n" +
                "  ADD CONSTRAINT ${tableName}_${columnName}_fkey\n" +
                "    FOREIGN KEY ($columnName) REFERENCES" +
                " $referencedTableName ($referencedColumnName);"

        execute(sql)
    }

    override fun dropForeignKey(
            tableName: String,
            columnName: String,
            keyName: String
    ) {
        val sql = "ALTER TABLE $tableName DROP CONSTRAINT $keyName"
        execute(sql)
    }
}