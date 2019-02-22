/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Harmonica.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This file is part of Harmonica.
 *
 * Harmonica is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Harmonica is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.stub.core

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.ConnectionInterface
import com.improve_future.harmonica.core.DbConfig
import java.sql.Statement

class StubConnection : ConnectionInterface {
    override val config = DbConfig()

    val executedSqlList = mutableListOf<String>()

    override fun transaction(block: Connection.() -> Unit) {

    }

    override fun execute(sql: String): Boolean {
        executedSqlList.add(sql)
        return true
    }

    override fun doesTableExist(tableName: String): Boolean {
        return true
    }

    @Deprecated(
        "Cause Error",
        ReplaceWith("Can't be replaced. It's created only to meet interface.")
    )
    override fun createStatement(): Statement {
        return Statement::class.java.newInstance()
    }
}