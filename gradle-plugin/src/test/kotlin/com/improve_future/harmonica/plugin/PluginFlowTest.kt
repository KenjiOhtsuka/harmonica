package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.Dbms
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PluginFlowTest {
    @Test
    fun harmonicaUpAndDownWithoutExposed() {
        val projectDir = createProject(
            "without-exposed",
            migrationFileName = "20260811000001_PlainDemo.kts",
            migrationScript = plainMigration,
            withExposed = false
        )
        harmonicaUp(projectDir)
        assertMigrated(projectDir, "demo_plain", "20260811000001", migrated = true)
        harmonicaDown(projectDir)
        assertMigrated(projectDir, "demo_plain", "20260811000001", migrated = false)
    }

    @Test
    fun harmonicaUpAndDownWithExposed() {
        val projectDir = createProject(
            "with-exposed",
            migrationFileName = "20260811000000_ExposedDemo.kts",
            migrationScript = exposedMigration,
            withExposed = true
        )
        harmonicaUp(projectDir)
        assertMigrated(projectDir, "demo_exposed", "20260811000000", migrated = true)
        harmonicaDown(projectDir)
        assertMigrated(projectDir, "demo_exposed", "20260811000000", migrated = false)
    }

    private fun createProject(
        name: String,
        migrationFileName: String,
        migrationScript: String,
        withExposed: Boolean
    ): File {
        val projectDir = File(File("build", "testkit"), name).apply { mkdirs() }
        val dbPath = dbPath(projectDir)
        val dbParent = File(dbPath).parentFile!!
        dbParent.deleteRecursively()
        check(!dbParent.exists()) {
            "Test database parent still exists: $dbParent"
        }

        File(projectDir, "settings.gradle.kts").writeText(
            """
            pluginManagement {
                includeBuild(${repoRoot.invariantSeparatorsPath.quoted()})
                repositories {
                    gradlePluginPortal()
                    mavenCentral()
                }
            }
            rootProject.name = "plugin-flow"
            includeBuild(${repoRoot.invariantSeparatorsPath.quoted()})
            """.trimIndent()
        )

        val buildScript = StringBuilder().apply {
            appendLine("buildscript {")
            appendLine("    repositories {")
            appendLine("        mavenCentral()")
            appendLine("    }")
            appendLine("    dependencies {")
            appendLine("        classpath(\"org.xerial:sqlite-jdbc:3.45.3.0\")")
            appendLine("    }")
            appendLine("}")
            appendLine("plugins {")
            appendLine("    id(\"harmonica\") version \"2.0.0\"")
            appendLine("}")
            appendLine("repositories {")
            appendLine("    mavenCentral()")
            appendLine("}")
            if (withExposed) {
                appendLine("dependencies {")
                appendLine("    harmonica(\"com.improve_future:exposed:2.0.0\")")
                appendLine("}")
            }
        }.toString()
        File(projectDir, "build.gradle.kts").writeText(buildScript)

        File(projectDir, "src/main/kotlin/db/config/default.kts").apply {
            parentFile.mkdirs()
            writeText(
                """
                import com.improve_future.harmonica.core.DbConfig
                import com.improve_future.harmonica.core.Dbms

                DbConfig {
                    dbms = Dbms.SQLite
                    dbName = ${dbPath.quoted()}
                }
                """.trimIndent()
            )
        }
        File(projectDir, "src/main/kotlin/db/migration/$migrationFileName").apply {
            parentFile.mkdirs()
            writeText(migrationScript)
        }
        return projectDir
    }

    private fun harmonicaUp(projectDir: File) = runGradle(projectDir, "harmonicaUp")

    private fun harmonicaDown(projectDir: File) = runGradle(projectDir, "harmonicaDown")

    private fun runGradle(projectDir: File, vararg tasks: String) {
        val result = try {
            GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(*tasks)
                .build()
        } catch (e: UnexpectedBuildFailure) {
            throw AssertionError("Nested build ${tasks.joinToString(" ")} failed:\n${e.message}", e)
        }
        for (task in tasks) {
            assertEquals(TaskOutcome.SUCCESS, result.task(":$task")?.outcome, "Task :$task")
        }
    }

    private fun assertMigrated(
        projectDir: File, tableName: String, version: String, migrated: Boolean
    ) {
        val dbPath = dbPath(projectDir)
        Connection.create {
            dbms = Dbms.SQLite
            dbName = dbPath
            user = ""
            password = ""
        }.use { connection ->
            connection.transaction {
                assertEquals(migrated, connection.doesTableExist(tableName), tableName)
                val count = connection.createStatement().use { statement ->
                    statement.executeQuery(
                        "SELECT COUNT(1) FROM harmonica_migration WHERE version = '$version'"
                    ).use { resultSet ->
                        resultSet.next()
                        resultSet.getLong(1)
                    }
                }
                assertEquals(if (migrated) 1L else 0L, count, "harmonica_migration rows")
            }
        }
    }

    private fun dbPath(projectDir: File) =
        File(projectDir, "build/db/harmonica").absolutePath.replace("\\", "/")

    private fun String.quoted() = "\"$this\""

    companion object {
        private val repoRoot: File = File("..").canonicalFile.also {
            check(File(it, "settings.gradle.kts").exists()) {
                "Expected the repository root at $it; the test working directory must be the gradle-plugin module"
            }
        }

        private val plainMigration = """
            import com.improve_future.harmonica.core.AbstractMigration

            object : AbstractMigration() {
                override fun up() {
                    createTable("demo_plain") {
                        varchar("name")
                    }
                    executeSql("INSERT INTO demo_plain (name) VALUES ('harmonica')")
                }

                override fun down() {
                    dropTable("demo_plain")
                }
            }
        """.trimIndent()

        private val exposedMigration = """
            import com.improve_future.harmonica.core.AbstractMigration
            import com.improve_future.harmonica.exposed.exposedTransaction
            import org.jetbrains.exposed.sql.SchemaUtils
            import org.jetbrains.exposed.sql.Table
            import org.jetbrains.exposed.sql.insert
            import org.jetbrains.exposed.sql.selectAll

            object DemoExposedTable : Table("demo_exposed") {
                val id = integer("id").autoIncrement()
                val name = varchar("name", 100)
                override val primaryKey = PrimaryKey(id)
            }

            object : AbstractMigration() {
                override fun up() {
                    exposedTransaction {
                        SchemaUtils.create(DemoExposedTable)
                        DemoExposedTable.insert { it[name] = "harmonica" }
                        println("exposed rows: " + DemoExposedTable.selectAll().count())
                    }
                }

                override fun down() {
                    exposedTransaction {
                        SchemaUtils.drop(DemoExposedTable)
                    }
                }
            }
        """.trimIndent()
    }
}
