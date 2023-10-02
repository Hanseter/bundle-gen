package com.github.hanseter.bundle

fun generateJava(resProps: String, packageName: String, className: String, fileName: String): String {
    val sb = StringBuilder()
    val entries = toI18nEntries(resProps)
    sb + "package " + packageName + ";\n\npublic final class " + className + " {\n"
    sb + "private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(\"" + fileName + "\");\n"
    entries.forEach { appendKotlinMethod(it, sb) }
    sb + "}"
    return sb.toString()
}

private fun appendKotlinMethod(entry: Entry, sb: StringBuilder) {
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
        sb + "bundle.getString(\"" + entry.key + "\")"
    } else {
        sb + "java.text.MessageFormat.format(bundle.getString(\"" + entry.key + "\"), "
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
