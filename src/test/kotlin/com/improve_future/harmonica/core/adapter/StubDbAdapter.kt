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

import com.improve_future.harmonica.core.table.IndexMethod
import com.improve_future.harmonica.core.table.TableBuilder
import com.improve_future.harmonica.core.table.column.AbstractColumn
import com.improve_future.harmonica.core.table.column.AddingColumnOption
import com.improve_future.harmonica.stub.core.StubConnection

internal class StubDbAdapter : DbAdapter(StubConnection()) {
    val addingColumnList = mutableListOf<AddingColumn>()
    val addingForeignKeyList = mutableListOf<AddingForeignKey>()

    data class AddingColumn(
        val tableName: String,
        val column: AbstractColumn,
        val option: AddingColumnOption
    )

    data class AddingForeignKey(
        val tableName: String,
        val columnName: String,
        val referencedTableName: String,
        val referencedColumnName: String
    )

    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
    }

    override fun renameTable(oldTableName: String, newTableName: String) {
    }

    override fun createIndex(
        tableName: String, columnNameArray: Array<String>, unique: Boolean,
        method: IndexMethod?
    ) {
    }

    override fun renameIndex(
        tableName: String,
        oldIndexName: String,
        newIndexName: String
    ) {
    }

    override fun dropIndex(tableName: String, indexName: String) {
    }

    override fun addColumn(
        tableName: String,
        column: AbstractColumn,
        option: AddingColumnOption
    ) {
        addingColumnList.add(AddingColumn(tableName, column, option))
    }

    override fun addForeignKey(
        tableName: String,
        columnName: String,
        referencedTableName: String,
        referencedColumnName: String
    ) {
        addingForeignKeyList.add(
            AddingForeignKey(
                tableName, columnName, referencedTableName, referencedColumnName
            )
        )
    }

    override fun dropForeignKey(
        tableName: String,
        columnName: String,
        keyName: String
    ) {
        TODO("not implemented")
    }
}