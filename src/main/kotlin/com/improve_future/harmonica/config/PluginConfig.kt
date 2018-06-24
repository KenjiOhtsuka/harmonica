package com.improve_future.harmonica.config

object PluginConfig {
    const val groupName = "migration"

    fun hasExposed(): Boolean {
        return try {
            null != Class.forName("org.jetbrains.exposed.sql.Database")
        } catch (e: Exception) {
            false
        }
    }
}