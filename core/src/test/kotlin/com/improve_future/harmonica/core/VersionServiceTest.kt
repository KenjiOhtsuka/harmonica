/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (c) 2022 Kenji Otsuka
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
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