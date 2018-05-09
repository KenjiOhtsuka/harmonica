package com.improve_future.harmonica.core

class Migration : AbstractMigration() {
    override fun up() {
        createTable("sample") {
            integer("column_1")
            integer("column_2")
            integer("column_3")
            varchar("column_4")
        }
    }

    override fun down() {
        dropTable("sample")
    }
}