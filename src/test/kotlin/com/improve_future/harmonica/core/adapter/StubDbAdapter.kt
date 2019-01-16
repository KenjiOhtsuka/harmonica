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
        tableName: String, columnName: String, unique: Boolean,
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
}