package com.improve_future.harmonica.demo.jarmonica.migration

import com.improve_future.harmonica.core.AbstractMigration

class M20180714203511949_OtherMigration : AbstractMigration() {
    override fun up() {
        createTable("other_table") {
            integer("integer_column")
            varchar("varchar_column", size = 1)
            decimal("decimal_column", 5, 2)
            boolean("boolean_column")
            blob("blob_column")
            date("date_column")
            time("time_column", withTimeZone = true)
            dateTime("date_time_column")
            timestamp("timestamp_column", withTimeZone = true)
            text("text_column")
        }
        val tableName = "other_table_for_add"
        createTable(tableName) {}
        addIntegerColumn(tableName, "integer_column")
        addVarcharColumn(tableName, "varchar_column", size = 1)
        addDecimalColumn(
            tableName, "decimal_column", precision = 5, scale = 2
        )
        addBooleanColumn(tableName, "boolean_column")
        addDateColumn(tableName, "date_column")
        addTimeColumn(tableName, "time_column", withTimeZone = true)
        addDateTimeColumn(tableName, "date_time_column")
        addTimestampColumn(tableName, "timestamp_column", withTimeZone = true)
        addTextColumn(tableName, "text_column")
    }

    override fun down() {
        dropTable("other_table_for_add")
        dropTable("other_table")
    }
}
