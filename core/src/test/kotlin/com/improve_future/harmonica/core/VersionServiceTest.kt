/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2020  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VersionServiceTest {
    @Test
    fun pickUpVersionFromClassTest() {
        val versionService = VersionService("test")
        val version = versionService.pickUpVersionFromClassName(
            "com.improve_future.harmonica.test.AA\$M12345_Migration"
        )
        assertEquals("12345", version)
    }

    @Test
    fun filterMigrationClassWithVersionTest() {
        val versionService = VersionService("test")

        class M123456_Class1 : AbstractMigration()
        class M123457_Class2 : AbstractMigration()

        val classList = listOf<Class<AbstractMigration>>(
            M123456_Class1::class.java as Class<AbstractMigration>,
            M123457_Class2::class.java as Class<AbstractMigration>
        )

        val filteredList =
            versionService.filterClassCandidateWithVersion(
                classList, "123457"
            )

        assertEquals(1, filteredList.size)
        assertTrue(
            filteredList[0].simpleName.endsWith(
                "M123457_Class2"
            )
        )
    }
}