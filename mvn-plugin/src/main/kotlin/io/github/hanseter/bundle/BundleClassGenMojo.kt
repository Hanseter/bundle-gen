package io.github.hanseter.bundle

import org.apache.maven.model.Resource
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText


@Mojo(name = "generate-bundle-classes", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
class BundleClassGenMojo : AbstractMojo() {
    @Parameter(defaultValue = "\${project}", required = true, readonly = true)
    lateinit var project: MavenProject

    @Parameter(defaultValue = "\${project.resources}", required = true, readonly = true)
    lateinit var resources: List<Resource>

    @Parameter(required = true, property = "bundles")
    lateinit var bundles: List<GenConfig>
    override fun execute() {
        generateExplicitBundles()
        autoGenerateClasses()
    }

    private fun generateExplicitBundles() {
        bundles.forEach { cfg ->
            generateClass(cfg)
        }
    }

    private fun autoGenerateClasses() {
        findBundles().forEach {
            generateClass(it)
        }
    }

    private fun findBundles(): Sequence<GenConfig> {
        return resources.asSequence()
            .flatMap { res -> File(res.directory).walkTopDown() }
            .filter {
                log.debug("Checking file ${it}")
                it.extension == "properties"
            }
            .filter { file ->
                file.bufferedReader().use {
                    val firstLine = it.readLine().orEmpty().removeWhiteSpace().lowercase()
                    firstLine == "#generateclass"
                }
            }.mapNotNull { readConfig(it) }
    }

    private fun readConfig(file: File): GenConfig? {
        log.debug("Generating for $file")
        val props = readProps(file)
        log.debug("Read props of file $file")
        val language = props["#language"]
        if (language == null) {
            log.warn("Missing required attribute language in file ${file}.")
            return null
        }
        return GenConfig().also {
            it.language = language
            it.file = file.toString()
            it.packageName = props["#packageName"]
            it.className = props["#className"]
            it.outDir = props["#outDir"]
        }
    }

    private fun readProps(file: File): Map<String, String> {
        return file.bufferedReader().use { reader ->
            reader.lineSequence()
                .takeWhile { it.isBlank() || it.startsWith('#') }
                .mapNotNull {
                    val index = it.indexOf('=')
                    if (index == -1) null
                    else {
                        val key = it.take(index)
                        val value = it.drop(index + 1).trim()
                        if (value.isNotBlank()) key to value
                        else null
                    }
                }.toMap()
        }
    }

    private fun generateClass(cfg: GenConfig) {
        val filePath = cfg.file.replace('\\', '/')
        val content = Paths.get(filePath).readText()
        val className = cfg.className ?: Paths.get(filePath).toFile().nameWithoutExtension.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
        val resDir = resources.map { it.directory.replace('\\', '/') }.find { filePath.startsWith(it) }
            ?: throw IllegalArgumentException("Bundle ${filePath} doesn't seem to be in a resource directory!")

        val relativeFile = filePath.drop(resDir.length + 1)
        val packageName = cfg.packageName ?: getDefaultPackageName(relativeFile)


        val fileName = relativeFile.substringBeforeLast('.')
        val result = when (cfg.language.lowercase()) {
            "java" -> generateJava(content, packageName, className, fileName)
            "kotlin" -> generateKotlin(content, packageName, className, fileName)
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
        val outDir = cfg.outDir ?: "${project.basedir}/src/main/${cfg.language}"
        Paths.get(outDir)
            .resolve(packageName.replace('.', '/'))
            .resolve(className + extension)
            .apply {
                createParentDirectories()
                writeText(result)
            }
    }

    private fun getDefaultPackageName(relativeFile: String): String {
        val lastSlash = relativeFile.indexOfLast { it == '/' }
        return if (lastSlash == -1) {
            log.warn("Found $relativeFile in resource root, class will be generated in empty package.")
            ""
        } else relativeFile.take(lastSlash).replace('/', '.')
    }

    private fun String.removeWhiteSpace(): String = filter { !it.isWhitespace() }
}