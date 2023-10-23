package io.github.hanseter.bundle

/**
 * Generates a java class from the provided resource bundle.
 *
 * @param resProps The resource bundle content as a string.
 * @param packageName The package the of the generated class.
 * @param className The class name of the generated code.
 * @param fileName The file name of the resource bundle.
 * Needs to contain the whole path that would be needed to load the file from classpath.
 *
 * @return The unformatted class code.
 */
fun generateJava(resProps: String, packageName: String, className: String, fileName: String): String {
    val sb = StringBuilder()
    val entries = toI18nEntries(resProps)
    if (packageName.isNotEmpty()) {
        sb + "package " + packageName + ";\n\n"
    }
    sb + "public final class " + className + " {\n"
    sb + " public static final String BUNDLE_PATH = \"" + fileName + "\";\n"
    sb + " public interface TranslationProvider {\n  String translate(String key);\n }\n"
    sb + " private final TranslationProvider translator;\n"
    sb + " public " + className +"() {\n  this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString);\n }\n"
    sb + " public " + className +"(TranslationProvider translator) {\n  this.translator = translator;\n }\n"
    entries.forEach { appendJavaMethod(it, sb) }
    sb + "}"
    return sb.toString()
}

private fun appendJavaMethod(entry: Entry, sb: StringBuilder) {
    sb + "public String "
    appendMethodName(entry, sb)
    sb + "("
    repeat(entry.paramCount) { i ->
        val annotation = entry.annotations.find { it.number == i }
        if (annotation != null) {
            sb + annotation.javaType + " " + annotation.name
        } else {
            sb + "String param" + i
        }
        sb + ", "
    }
    if (entry.paramCount > 0) {
        sb.setLength(sb.length - 2)
    }
    sb + ") { return "
    if (entry.paramCount == 0) {
        sb + "translator.translate(\"" + entry.key + "\")"
    } else {
        sb + "java.text.MessageFormat.format(translator.translate(\"" + entry.key + "\"), "
        repeat(entry.paramCount) { i ->
            val annotation = entry.annotations.find { it.number == i }
            if (annotation != null) {
                sb + annotation.name
            } else {
                sb + "param" + i
            }
            sb + ", "
        }
        sb.setLength(sb.length - 2)
        sb + ")"
    }
    sb + "; }\n"
}
