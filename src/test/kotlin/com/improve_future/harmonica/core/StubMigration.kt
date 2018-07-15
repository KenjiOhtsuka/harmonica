package com.improve_future.harmonica.core

import com.improve_future.harmonica.core.adapter.StubDbAdapter
import com.improve_future.harmonica.stub.core.StubConnection
import java.lang.reflect.Modifier

class StubMigration : AbstractMigration() {
    private val delegateAdapterField = AbstractMigration::class.java
        .getDeclaredField("adapter\$delegate").also {
            it.isAccessible = true
        }
    val adapter = StubDbAdapter()

    init {
        connection = StubConnection()
        delegateAdapterField.also { field ->
            field.isAccessible = true
            val modifier = field::class.java.getDeclaredField("modifiers")
            modifier.isAccessible = true
            modifier.setInt(field, field.modifiers and Modifier.FINAL.inv())
            field.set(this as AbstractMigration, lazyOf(adapter))
        }
    }

}