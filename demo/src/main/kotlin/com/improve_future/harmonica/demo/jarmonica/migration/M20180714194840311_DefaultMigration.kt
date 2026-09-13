package com.improve_future.harmonica.demo.jarmonica.migration

import com.improve_future.harmonica.core.AbstractMigration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date

class M20180714194840311_DefaultMigration : AbstractMigration() {
    override fun up() {
        createTable("default_table") {
            integer("integer_column", default = 1)
            varchar("varchar_column", default = "text")
            decimal("decimal_column", default = 1.1)
            boolean("boolean_column", default = true)
            blob("blob_column", default = "abcdefg".toByteArray())
            date("date_column_1", default = Date())
            date("date_column_2", default = LocalDate.now())
            date("date_column_3", default = "2018-01-02")
            time("time_column_1", default = "11:22:33")
            time("time_column_2", default = Date())
            time("time_column_3", default = LocalTime.now())
            dateTime("date_time_column_1", default = "2011-11-12 12:34:56")
            dateTime("date_time_column_2", default = Date())
            dateTime("date_time_column_3", default = LocalDateTime.now())
            timestamp("timestamp_column_1", default = "2012-10-04 1:2:3")
            timestamp("timestamp_column_2", default = Date())
            timestamp("timestamp_column_3", default = LocalDateTime.now())
            text("text_column", default = "text")
        }
        val tableName = "default_table_for_add"
        createTable(tableName) {}
        addIntegerColumn(tableName, "integer_column", default = 1)
        addVarcharColumn(tableName, "varchar_column", default = "text")
        addDecimalColumn(tableName, "decimal_column", default = 1.1)
        addBooleanColumn(tableName, "boolean_column", default = false)
        addBlobColumn(tableName, "blob_column", default = "abcdefg".toByteArray())
        addDateColumn(tableName, "date_column_1", default = Date())
        addDateColumn(tableName, "date_column_2", default = LocalDate.now())
        addDateColumn(tableName, "date_column_3", default = "2018-01-02")
        addTimeColumn(tableName, "time_column_1", default = "11:22:33")
        addTimeColumn(tableName, "time_column_2", default = Date())
        addTimeColumn(tableName, "time_column_3", default = LocalTime.now())
        addDateTimeColumn(
            tableName, "date_time_column_1", default = "2011-11-12 12:34:56"
        )
        addDateTimeColumn(
            tableName, "date_time_column_2", default = Date()
        )
        addDateTimeColumn(
            tableName, "date_time_column_3", default = LocalDateTime.now()
        )
        addTimestampColumn(
            tableName, "timestamp_column_1", default = "2012-10-04 1:2:3"
        )
        addTimestampColumn(
            tableName, "timestamp_column_2", default = Date()
        )
        addTimestampColumn(
            tableName, "timestamp_column_3", default = LocalDateTime.now()
        )
        addTextColumn(tableName, "text_column", default = "text")
    }

    override fun down() {
        dropTable("default_table_for_add")
        dropTable("default_table")
    }
}
