package com.improve_future.harmonica.plugin

class JarmonicaArgument() {
    lateinit var migrationPackage: String
    lateinit var taskType: JarmonicaTaskType
    lateinit var env: String
    lateinit var migrationDirectory: String
    private val additionalArgList = mutableListOf<String>()

    fun toArray(): Array<String> {
        return arrayOf(
                migrationPackage,
                taskType.name,
                migrationDirectory,
                env) + additionalArgList.toTypedArray()
    }

    fun toList(): List<String> {
        return listOf(
                migrationPackage,
                taskType.name,
                migrationDirectory,
                env) + additionalArgList
    }

    fun add(arg: String): JarmonicaArgument {
        additionalArgList.add(arg)
        return this
    }
}