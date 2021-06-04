/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2021  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.plugin.migration

import com.improve_future.harmonica.core.AbstractMigration

class M20200203181017458_2test_migration : AbstractMigration() {
    override fun up() {
        createTable("refresh_token_d") {
            id = false
            text("column1", nullable = false)
            integer("column2", nullable = false)
            integer("column3", nullable = false)
            dateTime("column4", nullable = false)
        }
        executeSql("ALTER TABLE refresh_token_d ADD CONSTRAINT PK_refresh_token_d PRIMARY KEY (column1)")
    }

    override fun down() {
        dropTable("refresh_token_d")
    }
}
