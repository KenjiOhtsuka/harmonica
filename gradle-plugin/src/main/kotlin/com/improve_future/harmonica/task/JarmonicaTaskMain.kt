package com.improve_future.harmonica.task

import com.improve_future.harmonica.config.PluginConfig
import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.VersionService
import java.io.File
import java.net.JarURLConnection

abstract class JarmonicaTaskMain {
    private val migrationTableName: String = "harmonica_migration"
    protected val versionService: VersionService
    private var classLoader: ClassLoader

    init {
        versionService = VersionService(migrationTableName)
        classLoader = ClassLoader.getSystemClassLoader()
    }

    protected fun createConnection(
        packageName: String, env: String
    ): Connection {
        return Connection(loadDbConfig(packageName, env), PluginConfig.hasExposed())
    }

    protected fun findMigrationClassList(packageName: String): List<Class<out AbstractMigration>> {
        return findClassesInPackage(packageName)
            .filter {
                it != AbstractMigration::class.java &&
                    AbstractMigration::class.java.isAssignableFrom(it)
            }
            .map { it as Class<out AbstractMigration> }
            .sortedBy { it.name }
    }

    private fun loadDbConfig(
        packageName: String, env: String = "Default"
    ): DbConfig {
        val classList = findClassesInPackage(packageName)
            .filter {
                it != DbConfig::class.java && DbConfig::class.java.isAssignableFrom(it)
            }
            .map { it as Class<out DbConfig> }
        classList.forEach {
            if (it.simpleName == env) {
                return try {
                    // for Class inherits DbConfig
                    it.getConstructor().newInstance()
                } catch (e: Exception) {
                    // for Object inherits DbConfig
                    it.getDeclaredField("INSTANCE").get(it) as DbConfig
                }
            }
        }
        throw Exception("no config was found.")
    }

    private fun findClassesInPackage(packageName: String): List<Class<*>> {
        val path = packageName.replace('.', '/')
        val urls = classLoader.getResources(path)
        val classes = mutableListOf<Class<*>>()
        while (urls.hasMoreElements()) {
            val url = urls.nextElement()
            val classesInUrl = when (url.protocol) {
                "file" -> findClassesInDirectory(File(url.toURI()), packageName)
                "jar" -> findClassesInJar(url, path)
                else -> emptyList()
            }
            classes.addAll(classesInUrl)
        }
        return classes
    }

    private fun findClassesInDirectory(dir: File, packageName: String): List<Class<*>> {
        if (!dir.isDirectory) return emptyList()
        return dir.walkTopDown()
            .filter { it.isFile && it.extension == "class" }
            .mapNotNull {
                val relative =
                    it.toRelativeString(dir).removeSuffix(".class").replace(File.separatorChar, '.')
                loadClass("$packageName.$relative")
            }
            .toList()
    }

    private fun findClassesInJar(url: java.net.URL, path: String): List<Class<*>> {
        val connection = url.openConnection() as JarURLConnection
        connection.jarFile.use { jar ->
            return jar.entries().asSequence()
                .filter {
                    !it.isDirectory && it.name.startsWith("$path/") && it.name.endsWith(".class")
                }
                .mapNotNull {
                    loadClass(it.name.removeSuffix(".class").replace('/', '.'))
                }
                .toList()
        }
    }

    private fun loadClass(className: String): Class<*>? {
        return try {
            Class.forName(className, false, classLoader)
        } catch (e: Throwable) {
            null
        }
    }
}