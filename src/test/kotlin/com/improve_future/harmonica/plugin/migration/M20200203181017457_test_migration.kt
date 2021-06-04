package com.improve_future.harmonica.plugin.migration

import com.improve_future.harmonica.core.AbstractMigration

class M20200203181017457_test_migration : AbstractMigration() {
    override fun up() {
        createTable("refresh_token") {
            id = false
            text("column1", nullable = false)
            integer("column2", nullable = false)
            integer("column3", nullable = false)
            dateTime("column4", nullable = false)
        }
        executeSql("ALTER TABLE refresh_token ADD CONSTRAINT PK_refresh_column1 PRIMARY KEY (column1)")
    }

    override fun down() {
        dropTable("refresh_token")
    }
}
