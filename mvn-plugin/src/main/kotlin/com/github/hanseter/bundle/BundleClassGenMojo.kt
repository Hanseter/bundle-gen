package com.github.hanseter.bundle

import org.apache.maven.model.Resource
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.lang.IllegalArgumentException
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.createParentDirectories
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.writeText


@Mojo(name = "generate-bundle-classes", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = false)
class BundleClassGenMojo : AbstractMojo() {
    @Parameter(defaultValue = "\${project}", required = true, readonly = true)
    lateinit var project: MavenProject

    @Parameter(defaultValue = "\${project.resources}", required = true, readonly = true)
    lateinit var resources: List<Resource>

    @Parameter(required = true, property = "bundles")
    lateinit var bundles: List<GenConfig>
    override fun execute() {
        bundles.forEach { cfg ->
            val content = Paths.get(cfg.file).readText()
            val className = cfg.className ?: Paths.get(cfg.file).toFile().nameWithoutExtension.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            }
            val resDir = resources.find { cfg.file.startsWith(it.directory) }
                ?: throw IllegalArgumentException("Bundle ${cfg.file} doesn't seem to be in a resource directory!")

            val fileName = cfg.file.drop(resDir.directory.length + 1).substringBeforeLast('.')
            val result = when (cfg.language.lowercase()) {
                "java" -> generateJava(content, cfg.packageName, className, fileName)
                "kotlin" -> generateKotlin(content, cfg.packageName, className, fileName)
                else -> {
                    log.error("Unknown language ${cfg.language}")
                    ""
                }
            }
            val extension = when (cfg.language.lowercase()) {
                "java" -> ".java"
                "kotlin" -> ".kt"
                else -> ""
            }
            Paths.get(cfg.outDir ?: project.compileSourceRoots.first() as String)
                .resolve(cfg.packageName.replace('.', '/'))
                .resolve(className + extension)
                .apply {
                    createParentDirectories()
                    writeText(result)
                }

        }
    }
}