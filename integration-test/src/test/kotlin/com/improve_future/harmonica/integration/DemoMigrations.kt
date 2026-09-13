package com.improve_future.harmonica.integration

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.demo.jarmonica.migration.M20180714192339748_NormalMigration
import com.improve_future.harmonica.demo.jarmonica.migration.M20180714194338790_NotNullMigration
import com.improve_future.harmonica.demo.jarmonica.migration.M20180714194840311_DefaultMigration
import com.improve_future.harmonica.demo.jarmonica.migration.M20180714203511949_OtherMigration

object DemoMigrations {
    private fun all() = listOf(
        M20180714192339748_NormalMigration(),
        M20180714194338790_NotNullMigration(),
        M20180714194840311_DefaultMigration(),
        M20180714203511949_OtherMigration()
    )

    fun upAll(connection: Connection) = run(connection, all()) { it.up() }

    fun downAll(connection: Connection) = run(connection, all().reversed()) { it.down() }

    private fun run(
        connection: Connection,
        migrations: List<AbstractMigration>,
        action: (AbstractMigration) -> Unit
    ) {
        for (migration in migrations) {
            connection.transaction {
                migration.connection = this
                action(migration)
            }
        }
    }
}
