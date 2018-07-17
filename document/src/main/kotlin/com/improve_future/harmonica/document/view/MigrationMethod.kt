package com.improve_future.harmonica.document.view

object MigrationMethod : AbstractView() {
    override val title = "Migration method"
    override val pathKey = "migration_method"

    override fun index(): String {
        return Template.default(title) {

        }
    }
}