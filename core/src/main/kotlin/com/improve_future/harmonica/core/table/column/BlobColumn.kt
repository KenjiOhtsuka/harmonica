/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.table.column

import com.improve_future.harmonica.core.toHexString

internal class BlobColumn(name: String) : AbstractColumn(name) {
    var default: ByteArray? = null
        set(value) {
            field = value
            sqlDefault = value?.let { "E'\\\\x" + it.toHexString() + "'" }
        }

    override var sqlDefault: String? = null
}
