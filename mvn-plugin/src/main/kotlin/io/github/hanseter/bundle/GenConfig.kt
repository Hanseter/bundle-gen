package io.github.hanseter.bundle

import org.apache.maven.plugins.annotations.Parameter

class GenConfig() {
    @Parameter(required = true, property = "language")
    lateinit var language: String
    @Parameter(required = true, property = "file")
    lateinit var file: String
    @Parameter(property = "packageName")
    var packageName: String? = null
    @Parameter(property = "className")
    var className: String? = null
    @Parameter(property = "outDir")
    var outDir: String? = null
}