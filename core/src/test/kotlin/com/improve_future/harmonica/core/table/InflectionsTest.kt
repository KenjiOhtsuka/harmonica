package com.improve_future.harmonica.core.table

import kotlin.test.Test
import kotlin.test.assertEquals

class InflectionsTest {
    @Test
    fun testSingularizeRegularPlural() {
        assertEquals("user", "users".singularize())
        assertEquals("category", "categories".singularize())
        assertEquals("box", "boxes".singularize())
        assertEquals("wife", "wives".singularize())
    }

    @Test
    fun testSingularizeIrregular() {
        assertEquals("person", "people".singularize())
        assertEquals("man", "men".singularize())
        assertEquals("child", "children".singularize())
    }

    @Test
    fun testSingularizeUncountable() {
        assertEquals("fish", "fish".singularize())
        assertEquals("sheep", "sheep".singularize())
        assertEquals("information", "information".singularize())
    }

    @Test
    fun testSingularizeNoMatch() {
        assertEquals("address", "address".singularize())
    }
}
