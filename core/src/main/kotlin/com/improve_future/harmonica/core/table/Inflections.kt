package com.improve_future.harmonica.core.table

// Ported and adapted from kotlin-pluralizer, MIT License,
// Copyright (c) 2016 César Ferreira (https://github.com/cesarferreira/kotlin-pluralizer),
// which is itself a port of pluralize, The MIT License (MIT),
// Copyright (c) 2013 Blake Embrey (https://github.com/plurals/pluralize).
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.

internal fun String.singularize(): String {
    if (uncountableWords.contains(this.lowercase())) return this

    val exception = irregularWords.firstOrNull { this.equals(it.second, ignoreCase = true) }
    if (exception != null) return exception.first

    val endsWith = irregularWords.firstOrNull { this.endsWith(it.second) }
    if (endsWith != null) return this.replace(endsWith.second, endsWith.first)

    val matchingRules = singularizeRules.filter {
        Regex(it.first, RegexOption.IGNORE_CASE).containsMatchIn(this)
    }
    if (matchingRules.isEmpty()) return this

    val rule = matchingRules.last()
    return Regex(rule.first, RegexOption.IGNORE_CASE).replace(this, rule.second)
}

private val uncountableWords = listOf(
    "equipment", "information", "rice", "money",
    "species", "series", "fish", "sheep", "aircraft", "bison",
    "flounder", "pliers", "bream",
    "gallows", "proceedings", "breeches", "graffiti", "rabies",
    "britches", "headquarters", "salmon", "carp", "herpes",
    "scissors", "chassis", "high-jinks", "sea-bass", "clippers",
    "homework", "cod", "innings", "shears",
    "contretemps", "jackanapes", "corps", "mackerel",
    "swine", "debris", "measles", "trout", "diabetes", "mews",
    "tuna", "djinn", "mumps", "whiting", "eland", "news",
    "wildebeest", "elk", "pincers", "sugar",
    "water", "oil", "air", "sand", "grass", "hair", "weather",
    "furniture", "luggage", "baggage", "accommodation", "metadata",
    "advice", "knowledge", "music", "milk", "bread", "butter", "cheese",
    "coffee", "tea", "juice", "wine", "beer", "gold", "silver", "iron",
    "steel", "wood", "glass", "paper", "cloth", "cotton", "wool", "dust",
    "dirt", "mud", "snow", "ice", "smoke", "steam", "fog", "mist",
    "lightning", "thunder", "darkness", "warmth", "cold", "silence",
    "noise", "anger", "happiness", "sadness", "joy", "fear", "love",
    "hate", "pride", "courage", "honesty", "patience", "kindness",
    "education", "research", "evidence", "progress", "traffic",
    "transportation", "electricity", "energy", "heat", "oxygen",
    "hydrogen", "nitrogen",
    "mathematics", "physics", "economics", "politics", "statistics",
    "ethics", "gymnastics", "acoustics", "athletics", "optics",
    "dynamics", "electronics", "linguistics", "genetics",
    "thermodynamics", "hydraulics", "mechanics", "aerobics",
    "japanese", "chinese", "portuguese", "vietnamese", "burmese",
    "lebanese", "maltese", "taiwanese", "sudanese", "senegalese",
    "congolese", "beninese", "gabonese", "faroese", "timorese",
    "ceylonese"
)

private val irregularWords = listOf(
    "person" to "people",
    "man" to "men",
    "goose" to "geese",
    "child" to "children",
    "move" to "moves",
    "stadium" to "stadiums",
    "database" to "databases",
    "deer" to "deer",
    "codex" to "codices",
    "murex" to "murices",
    "silex" to "silices",
    "radix" to "radices",
    "helix" to "helices",
    "alumna" to "alumnae",
    "alga" to "algae",
    "vertebra" to "vertebrae",
    "persona" to "personae",
    "stamen" to "stamina",
    "foramen" to "foramina",
    "lumen" to "lumina",
    "afreet" to "afreeti",
    "afrit" to "afriti",
    "efreet" to "efreeti",
    "cherub" to "cherubim",
    "goy" to "goyim",
    "human" to "humans",
    "seraph" to "seraphim",
    "Alabaman" to "Alabamans",
    "Bahaman" to "Bahamans",
    "Burman" to "Burmans",
    "German" to "Germans",
    "Liman" to "Limans",
    "Nakayaman" to "Nakayamans",
    "Oklahoman" to "Oklahomans",
    "Panaman" to "Panamans",
    "Selman" to "Selmans",
    "Sonaman" to "Sonamans",
    "Tacoman" to "Tacomans",
    "Yakiman" to "Yakimans",
    "Yokohaman" to "Yokohamans",
    "Yuman" to "Yumans",
    "criterion" to "criteria",
    "perihelion" to "perihelia",
    "aphelion" to "aphelia",
    "phenomenon" to "phenomena",
    "prolegomenon" to "prolegomena",
    "noumenon" to "noumena",
    "organon" to "organa",
    "asyndeton" to "asyndeta",
    "hyperbaton" to "hyperbata",
    "foot" to "feet",
    "index" to "indices",
    "datum" to "data",
    "appendix" to "appendices",
    "tooth" to "teeth",
    "focus" to "foci",
    "nucleus" to "nuclei",
    "curriculum" to "curricula",
    "memorandum" to "memoranda",
    "bacterium" to "bacteria",
    "matrix" to "matrices",
    "crisis" to "crises"
)

private val singularizeRules = listOf(
    "s$" to "",
    "(s|si|u)s$" to "$1s",
    "(n)ews$" to "$1ews",
    "([ti])a$" to "$1um",
    "((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$" to "$1$2sis",
    "(^analy)ses$" to "$1sis",
    "(^analy)sis$" to "$1sis",
    "([^f])ves$" to "$1fe",
    "(hive)s$" to "$1",
    "(tive)s$" to "$1",
    "([lr])ves$" to "$1f",
    "([^aeiouy]|qu)ies$" to "$1y",
    "(s)eries$" to "$1eries",
    "(m)ovies$" to "$1ovie",
    "(x|ch|ss|sh)es$" to "$1",
    "([m|l])ice$" to "$1ouse",
    "(bus)es$" to "$1",
    "(o)es$" to "$1",
    "(shoe)s$" to "$1",
    "(cris|ax|test)is$" to "$1is",
    "(cris|ax|test)es$" to "$1is",
    "(octop|vir)i$" to "$1us",
    "(octop|vir)us$" to "$1us",
    "(alias|status)es$" to "$1",
    "(alias|status)$" to "$1",
    "^(ox)en" to "$1",
    "(vert|ind)ices$" to "$1ex",
    "(matr)ices$" to "$1ix",
    "(quiz)zes$" to "$1",
    "a$" to "um",
    "i$" to "us",
    "ae$" to "a"
)
