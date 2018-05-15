package com.improve_future.harmonica.core

import org.jetbrains.exposed.sql.Transaction

open class Migration {
    companion object {
        fun create(block: Migration.() -> Unit): Migration {
            val builder = Migration()
            builder.block()
            return  builder
        }
    }

    open var upFun: () -> Unit = {}
    open var downFun: () -> Unit = {}

    fun up(block: () -> Unit) {
        upFun = block
    }

    fun down(block: () -> Unit) {
        downFun = block
    }
}