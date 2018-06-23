package com.improve_future.harmonica.core

object PluginStatus {
    fun hasExposed(): Boolean {
        return try {
            null != Class.forName("org.jetbrains.exposed.sql.Database")
        } catch (e: Exception) {
            false
        }
    }
}