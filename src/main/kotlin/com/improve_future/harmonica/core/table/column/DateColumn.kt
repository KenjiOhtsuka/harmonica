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

package com.improve_future.harmonica.core.table.column

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

internal class DateColumn(name: String) : AbstractColumn(name) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    var default: String?
        get() {
            return defaultDate?.let { dateFormat.format(defaultDate) }
        }
        set(value) {
            defaultDate = value?.let { dateFormat.parse(value) }
        }

    var defaultDate: Date? = null
        set(value) {
            field = value
            sqlDefault = default?.let { "'$it'" }
        }

    var defaultLocalDate: LocalDate?
        get() {
            defaultDate ?: return null
            val calendar = Calendar.getInstance()
            calendar.time = defaultDate
            return LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        set(value) {
            default = value?.toString()
        }

    override var sqlDefault: String? = null
}