package com.improve_future.harmonica.document.view

object MigrationMethod : ViewInterface {
    override val path = "migration_method.html"

    override fun index(): String {
        return Template.default("Migration method") {

        }
    }
}