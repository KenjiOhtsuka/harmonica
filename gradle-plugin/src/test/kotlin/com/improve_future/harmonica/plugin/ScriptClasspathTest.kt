package com.improve_future.harmonica.plugin

import com.improve_future.harmonica.core.AbstractMigration
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import java.io.File
import javax.tools.ToolProvider
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ScriptClasspathTest {
    @Test
    fun scriptCanUseClassesFromTheHarmonicaConfiguration() {
        val workDir = File.createTempFile("harmonica-script-classpath", "").let {
            it.delete()
            it.mkdirs()
            it
        }
        val markerFile = File(workDir, "touched")
        val classesDir = compileScriptProbe(workDir)
        val markerPath = markerFile.invariantSeparatorsPath

        val migration = createMigrationTask(classesDir).evalScript(
            """
            import com.improve_future.harmonica.core.AbstractMigration
            import scriptprobe.ScriptProbe

            object : AbstractMigration() {
                override fun up() {
                    ScriptProbe.touch("$markerPath")
                }
            }
            """.trimIndent()
        )
        migration.up()

        assertTrue(markerFile.exists())
    }

    @Test
    fun scriptWithoutExtraClasspathFailsToResolveScriptProbe() {
        val task = createMigrationTask(null)
        assertFailsWith<Exception> {
            task.evalScript(
                """
                import com.improve_future.harmonica.core.AbstractMigration
                import scriptprobe.ScriptProbe

                object : AbstractMigration() {
                    override fun up() {
                        ScriptProbe.touch("/tmp/harmonica-should-not-exist")
                    }
                }
                """.trimIndent()
            )
        }
    }

    private fun createMigrationTask(scriptClasspath: File?): ProbeMigrationTask {
        val project = ProjectBuilder.builder().build()
        val task = project.tasks.create("probe", ProbeMigrationTask::class.java)
        scriptClasspath?.let { task.scriptClasspath.from(it) }
        return task
    }

    private fun compileScriptProbe(workDir: File): File {
        val source = File(workDir, "scriptprobe/ScriptProbe.java").apply {
            parentFile.mkdirs()
            writeText(
                """
                package scriptprobe;

                import java.io.File;
                import java.io.IOException;

                public class ScriptProbe {
                    public static void touch(String path) throws IOException {
                        File file = new File(path);
                        file.getParentFile().mkdirs();
                        if (!file.createNewFile()) {
                            throw new IOException("already exists: " + path);
                        }
                    }
                }
                """.trimIndent()
            )
        }
        val classesDir = File(workDir, "classes").apply { mkdirs() }
        val compiler = ToolProvider.getSystemJavaCompiler()
        val result = compiler.run(
            null, null, null,
            "-d", classesDir.absolutePath, source.absolutePath
        )
        check(result == 0) { "Failed to compile ScriptProbe" }
        return classesDir
    }
}

abstract class ProbeMigrationTask : AbstractMigrationTask() {
    fun evalScript(script: String): AbstractMigration = readMigration(script)
}
