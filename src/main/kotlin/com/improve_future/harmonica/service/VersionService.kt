package com.improve_future.harmonica.service

import com.improve_future.harmonica.Main
import com.improve_future.harmonica.core.Connection

class VersionService(val migrationTableName: String) {
    fun saveVersion(connection: Connection, version: String) {
        connection.execute(
                "INSERT INTO $migrationTableName(version) VALUES('$version');")
    }

    fun removeVersion(connection: Connection, version: String) {
        connection.execute(
                "DELETE FROM $migrationTableName WHERE version = '$version'")
    }
}