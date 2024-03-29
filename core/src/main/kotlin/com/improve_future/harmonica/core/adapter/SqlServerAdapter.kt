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
import com.improve_future.harmonica.core.table.column.AbstractColumn
import com.improve_future.harmonica.core.table.column.AddingColumnOption

internal class SqlServerAdapter(connection: ConnectionInterface) : DbAdapter(connection) {
    override fun createTable(tableName: String, tableBuilder: TableBuilder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createIndex(
        tableName: String, columnNameArray: Array<String>, unique: Boolean,
        method: IndexMethod?
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dropIndex(tableName: String, indexName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addColumn(
        tableName: String,
        column: AbstractColumn,
        option: AddingColumnOption
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renameTable(oldTableName: String, newTableName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renameIndex(
        tableName: String, oldIndexName: String, newIndexName: String
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addForeignKey(
        tableName: String, columnName: String, referencedTableName: String,
        referencedColumnName: String
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dropForeignKey(
        tableName: String, columnName: String, keyName: String
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}