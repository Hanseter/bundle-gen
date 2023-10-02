package com.github.hanseter.bundle

import java.text.MessageFormat
import kotlin.text.StringBuilder

fun generateKotlin(resProps: String, packageName: String, className: String, fileName: String): String {
    val sb = StringBuilder()
    val entries = toI18nEntries(resProps)
    sb + "package " + packageName + "\n\nclass " + className + " {\n"
    sb + "private val bundle: java.util.ResourceBundle = java.util.ResourceBundle.getBundle(\"" + fileName + "\")\n"
    entries.forEach { appendKotlinMethod(it, sb) }
    sb + "}"
    return sb.toString()
}

private fun appendKotlinMethod(entry: Entry, sb: StringBuilder) {
    sb + "fun "
    appendMethodName(entry, sb)
    sb + "("
    repeat(entry.paramCount) { i ->
        val annotation = entry.annotations.find { it.number == i }
        if (annotation != null) {
            sb + annotation.name + ": " + annotation.kotlinType
        } else {
            sb + "param" + i + ": String"
        }
        sb.append(", ")
    }
    if (entry.paramCount > 0) {
        sb.setLength(sb.length-2)
    }
    sb + "): String = "
    if (entry.paramCount == 0) {
        sb + "bundle.getString(\"" + entry.key+"\")"
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
        sb.setLength(sb.length-2)
        sb + ")"
    }
    sb + "\n"
}

internal fun appendMethodName(entry: Entry, sb: StringBuilder) {
    var skipped = false
    entry.key.forEach {
        if (it.isLetterOrDigit()) {
            sb.append(if (skipped) it.uppercase() else it)
            skipped = false
        } else {
            skipped = true
        }
    }
}

internal fun toI18nEntries(resProps: String): List<Entry> {
    val ret = ArrayList<Entry>()
    val annotations = ArrayList<ParamAnnotation>()
    var key = ""
    var value = ""
    var inValue = false
    resProps.lines().forEach { line ->
        if (line.isBlank()) return@forEach
        val trimmedLine = line.trim()
        if (trimmedLine.startsWith("#")) {
            tryParseAnnotation(trimmedLine.substring(1))?.also { annotations += it }
        } else {
            if (inValue) {
                value += trimmedLine
            } else {
                val index = trimmedLine.indexOf('=')
                if (index == -1) {
                    return@forEach
                }
                key = trimmedLine.take(index)
                value = trimmedLine.substring(index+1)
            }
            if (value.endsWith("\\")) {
                value = value.dropLast(1)
                inValue = true
            } else {
                ret += Entry(key, countParameters(value), annotations.toList())
                annotations.clear()
                key = ""
                value = ""
                inValue = false
            }
        }
    }
    if (key.isNotEmpty()) {
        ret += Entry(key, countParameters(value), annotations)
    }
    return ret
}

private fun tryParseAnnotation(line: String): ParamAnnotation? {
    val numberString = line.takeWhile { it.isDigit() }
    if (numberString.isEmpty()) return null
    val number = numberString.toInt()
    val lineWithoutNumber = line.substring(numberString.length)
    val name = lineWithoutNumber.takeWhile { it != ':' }
    val type = lineWithoutNumber.substring(name.length)
    return ParamAnnotation(number, name.trim().drop(1).trim(), type.drop(1).trim())
}

private fun countParameters(value: String): Int {
    return MessageFormat(value).formats.size
}

internal class ParamAnnotation(val number: Int, val name: String, val type: String) {
    val kotlinType: String
        get() = type

    val javaType: String
        get() = when(type) {
            "Integer" -> "int"
            "Long" -> "long"
            "Double" -> "double"
            "Float" -> "float"
            else -> type
        }
}

internal class Entry(val key: String, val paramCount: Int, val annotations: List<ParamAnnotation>)

internal operator fun StringBuilder.plus(s: String) = append(s)
internal operator fun StringBuilder.plus(c: Char) = append(c)
internal operator fun StringBuilder.plus(i: Int) = append(i)