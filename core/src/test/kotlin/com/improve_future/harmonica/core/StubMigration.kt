/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.adapter.StubDbAdapter
import com.improve_future.harmonica.stub.core.StubConnection
import java.lang.reflect.Modifier

internal class StubMigration : AbstractMigration() {
    private val delegateAdapterField = AbstractMigration::class.java
        .getDeclaredField("adapter\$delegate").also {
            it.isAccessible = true
        }

    val adapter = StubDbAdapter()

    init {
        connection = StubConnection()
        delegateAdapterField.also { field ->
            field.isAccessible = true
            field.set(this as AbstractMigration, lazyOf(adapter))
        }
    }
}