# Kotlin Database Migration Tool

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) [![](https://jitpack.io/v/KenjiOhtsuka/harmonica.svg)](https://jitpack.io/#KenjiOhtsuka/harmonica)
[![CircleCI](https://circleci.com/gh/KenjiOhtsuka/harmonica/tree/master.svg?style=svg)](https://circleci.com/gh/KenjiOhtsuka/harmonica/tree/master)
[![Twitter Follow](https://img.shields.io/twitter/follow/_kjot.svg?style=social)](https://twitter.com/_kjot)


Gradle Plugin Page: https://plugins.gradle.org/plugin/com.improve_future.harmonica

This is Database Migration Tool, gradle plugin, made with Kotlin.
It is made similar to Phinx, Rails.

Now, this tool is for PostgreSQL, MySQL and SQLite.

## Overview

With this library, you can write database migration like as follows.

```kotlin
package your.migration

import com.improve_future.harmonica.core.AbstractMigration

/**
 * HolloWorld
 */
class M20180624011127699_HolloWorld : AbstractMigration() {
    override fun up() {
        createTable("table_name") {
            // If you add the next line,
            // migration doesn't create auto incremental id column.
            // id = false

            // You can easily define columns with their type name.
            boolean("boolean_column", nullable = false, default = true)
            integer("integer_column", default = 1)
            decimal("decimal_column", 5, 2, default = 3)
            varchar("varchar_column", size = 10, nullable = false)
            text("text_column", default = "default value")
            blob("blob_column", default = "abcde".toByteArray())
            date("date_column_1", default = "2019-01-01")
            date("date_column_2", default = Date())
            date("date_column_3", default = LocalDate.of(2018, 2, 2))
            time("time_column_1", default = "11:22:33")
            time("time_column_2", default = Date())
            time("time_column_3", default = LocalTime.now(), nullable = false)
            dateTime("date_tiem_column_1", default = "2011-11-12 12:34:56")
            dateTime("date_time_column_2", default = Date())
            dateTime("date_time_column_3", default = LocalDateTime.now())
            timestamp("timestamp_column_1", default = "2012-10-04 1:2:3")
            timestamp("timestamp_column_2", default = Date())
            timestamp("timestamp_column_3", default = LocalDateTime.now())
        }

        // When you add column, `add*****Column` method works.
        addBooleanColumn(
            "table_name", "added_boolean",
            default = true, nullable = false
        )
        addIntegerColumn(
            "table_name", "added_integer", nullable = false
        )
        addDecimalColumn("table_name", "added_decimal_column_name")
        addVarcharColumn(
            "table_name", "added_boolean_column_name",
            default = "default", nullable = false
        )
        addTextColumn("table_name", "added_text_column_name")
        addDateColumn(
            "table_name", "added_date",
            default = LocalDate.of(2018, 12, 11)
        )

        // When you add index, use `addIndex`.
        createIndex("table_name", "column_name")

        // You can execute SQL directly.
        executeSql("SELECT 1;")
    }

    override fun down() {
        dropIndex("table_name", "table_name_integer_column_idx")
        dropTable("table_name")
    }
}
```

## Usage, Command

Please look at [Wiki](https://github.com/KenjiOhtsuka/harmonica/wiki).

## Caution

When you use harmonica, not jarmonica,
sometimes it says "The connection has already been closed" and the migrations fail.
Then, execute `gradlew --stop` to clear dead connections.

## Contribute

Pull requests are welcomed!! Please feel free to use it, and to contribute.

## Links

* [API Document](https://kenjiohtsuka.github.io/harmonica/api/harmonica/index.html)

## Test Project

Test with real database is in different repository, [Harmonica Test](https://github.com/KenjiOhtsuka/harmonica_test).
