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
        assertEquals("goose", "geese".singularize())
        assertEquals("foot", "feet".singularize())
        assertEquals("tooth", "teeth".singularize())
    }

    @Test
    fun testSingularizeIrregularGreekLatin() {
        assertEquals("index", "indices".singularize())
        assertEquals("index", "indexes".singularize())
        assertEquals("datum", "data".singularize())
        assertEquals("appendix", "appendices".singularize())
        assertEquals("focus", "foci".singularize())
        assertEquals("crisis", "crises".singularize())
        assertEquals("matrix", "matrices".singularize())
        assertEquals("bacterium", "bacteria".singularize())
    }

    @Test
    fun testSingularizeUncountable() {
        assertEquals("fish", "fish".singularize())
        assertEquals("sheep", "sheep".singularize())
        assertEquals("information", "information".singularize())
        assertEquals("water", "water".singularize())
        assertEquals("metadata", "metadata".singularize())
        assertEquals("japanese", "japanese".singularize())
        assertEquals("mathematics", "mathematics".singularize())
        assertEquals("music", "music".singularize())
        assertEquals("economics", "economics".singularize())
        assertEquals("physics", "physics".singularize())
    }

    @Test
    fun testSingularizeNoMatch() {
        assertEquals("address", "address".singularize())
    }

    @Test
    fun testSingularizeDoesNotCorruptCompounds() {
        assertEquals("database", "databases".singularize())
        assertEquals("biomatrix", "biomatrices".singularize())
        assertEquals("forefoot", "forefeet".singularize())
    }
}
