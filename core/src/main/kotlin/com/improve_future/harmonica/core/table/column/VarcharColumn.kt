/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.table.column

internal class VarcharColumn(name: String) : AbstractColumn(name) {
    var size: Int? = null

    var default: String? = null
        set(value) {
            field = value
            sqlDefault = value?.let { "'$it'" }
        }

    override var sqlDefault: String? = null
}