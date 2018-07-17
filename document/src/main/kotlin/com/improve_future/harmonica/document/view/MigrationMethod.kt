package com.improve_future.harmonica.document.view

object MigrationMethod : ViewInterface {
    override val pathKey = "migration_method"

    override fun index(): String {
        return Template.default("Migration method") {

        }
    }
}