/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Harmonica.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.core.adapter

import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.table.IndexMethod
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.*

internal class PostgreSqlAdapter(connection: ConnectionInterface) :
    DbAdapter(connection) {

    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        var sql = "CREATE TABLE $tableName (\n"
        if (tableBuilder.id) {
            sql += "  id SERIAL PRIMARY KEY"
            if (tableBuilder.columnList.size > 0) sql += ','
            sql += "\n"
        }
        sql += tableBuilder.columnList.joinToString(",\n") {
            "  " + buildColumnDeclarationForCreateTableSql(it)
        }
        sql += "\n);"
        connection.execute(sql)

        // Add Comments
        if (tableBuilder.comment != null) {
            sql = "COMMENT ON TABLE $tableName IS '${tableBuilder.comment}';"
            connection.execute(sql)
        }
        tableBuilder.columnList.forEach {
            if (it.hasComment) {
                sql = "COMMENT ON COLUMN $tableName.${it.name} IS" +
                        " '${it.comment}';"
                connection.execute(sql)
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
                    if (column.size != null)
                        sql += "(" + column.size.toString() + ")"
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
                is TimeZoneInterface -> {
                    if (column.withTimeZone) {
                        sql += " WITH TIME ZONE"
                    }
                }
            }
            if (!column.nullable) sql += " NOT NULL"
            if (column.hasDefault) {
                sql += " DEFAULT " + column.sqlDefault
            }
            if (column.hasReference)
                sql += " REFERENCES ${column.referenceTable} (${column.referenceColumn})"
            return sql
        }

        override fun sqlType(column: AbstractColumn): String {
            return when (column) {
                is DateTimeColumn -> "TIMESTAMP"
                is BlobColumn -> "BYTEA"
                else -> super.sqlType(column)
            }
        }

        override fun sqlIndexMethod(method: IndexMethod?): String? {
            return when (method) {
                IndexMethod.BTree -> "btree"
                IndexMethod.Hash -> "hash"
                IndexMethod.Gist -> "gist"
                IndexMethod.SpGist -> "spgist"
                IndexMethod.Gin -> "gin"
                IndexMethod.BRin -> "brin"
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
        sql += " INDEX ON $tableName"
        sqlIndexMethod(method)?.let { sql += " USING $it" }
        sql += " (${columnNameArray.joinToString(", ")});"
        connection.execute(sql)
    }

    override fun dropIndex(tableName: String, indexName: String) {
        connection.execute("DROP INDEX $indexName;")
    }

    override fun addColumn(
        tableName: String,
        column: AbstractColumn,
        option: AddingColumnOption
    ) {
        var sql = "ALTER TABLE $tableName ADD COLUMN "
        sql += buildColumnDeclarationForCreateTableSql(column)
        sql += ";"
        connection.execute(sql)

        if (!column.hasComment) return

        sql = "COMMENT ON COLUMN $tableName.${column.name} IS" +
                " '${column.comment}';"
        connection.execute(sql)
    }

    override fun renameTable(oldTableName: String, newTableName: String) {
        var sql = "ALTER TABLE $oldTableName RENAME TO $newTableName;"
        connection.execute(sql)
    }

    override fun renameIndex(
        tableName: String, oldIndexName: String, newIndexName: String
    ) {
        val sql = "ALTER INDEX $oldIndexName RENAME TO $newIndexName;"
        connection.execute(sql)
    }

    override fun addForeignKey(
        tableName: String, columnName: String,
        referencedTableName: String, referencedColumnName: String
    ) {
        val sql = "ALTER TABLE $tableName" +
                " ADD CONSTRAINT ${tableName}_${columnName}_fkey" +
                " FOREIGN KEY ($columnName)" +
                " REFERENCES $referencedTableName ($referencedColumnName);"
        connection.execute(sql)
    }

    override fun dropForeignKey(
        tableName: String,
        columnName: String,
        keyName: String
    ) {
        val sql = "ALTER TABLE $tableName DROP CONSTRAINT $keyName;"
        connection.execute(sql)
    }
}