/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core

// https://stackoverflow.com/a/52225984/8220327
internal fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
