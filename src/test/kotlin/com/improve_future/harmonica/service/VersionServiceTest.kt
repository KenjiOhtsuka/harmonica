package com.improve_future.harmonica.service

import org.junit.Test
import kotlin.test.assertEquals

class VersionServiceTest {
    @Test
    fun pickUpVersionFromClassTest() {
        val versionService = VersionService("test")
        val version =
                versionService.pickUpVersionFromClassName("M12345_Migration")
        assertEquals("12345", version)
    }
}