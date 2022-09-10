/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core.table

enum class IndexMethod {
    BTree,
    Hash,
    /** Only for PostgreSQL */
    Gist,
    /** Only for PostgreSQL */
    SpGist,
    /** Only for PostgreSQL */
    Gin,
    /** Only for PostgreSQL */
    BRin
}