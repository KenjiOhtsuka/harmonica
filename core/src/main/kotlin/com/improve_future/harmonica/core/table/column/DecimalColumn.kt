/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.table.column

internal class DecimalColumn(name: String) : AbstractColumn(name) {
    override var sqlDefault: String? = null

    var default: Double?
        get() = sqlDefault?.toDoubleOrNull()
        set (value) {
            sqlDefault = value?.toString()
        }

    var precision: Int? = null
    var scale: Int? = null

}