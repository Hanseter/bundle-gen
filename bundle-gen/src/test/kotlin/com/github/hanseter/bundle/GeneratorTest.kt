package com.github.hanseter.bundle

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class GeneratorTest {

    @Test
    fun simpleInput() {
        val input = """
            foo=Example
            foo.bar=Example2
        """.trimIndent()

        expectThat(
            generateKotlin(input, "package.name", "ClassName", "bundle.properties").lines()
                .joinToString("\n") { it.trim() })
            .isEqualTo(
                """
                package package.name
                
                class ClassName {
                  private val bundle: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("bundle.properties")
                  fun foo(): String = bundle.getString("foo")
                  fun fooBar(): String = bundle.getString("foo.bar")
                }
            """.trimIndent().lines().joinToString("\n") { it.trim() }
            )

        expectThat(
            generateJava(input, "package.name", "ClassName", "bundle.properties").lines()
                .joinToString("\n") { it.trim() })
            .isEqualTo(
                """
                package package.name;
                
                public final class ClassName {
                  private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle.properties");
                  public String foo() { return bundle.getString("foo"); }
                  public String fooBar() { return bundle.getString("foo.bar"); }
                }
            """.trimIndent().lines().joinToString("\n") { it.trim() }
            )
    }

    @Test
    fun multiLineInput() {
        val input = """
            multi.line=Hello \
            World
            single.line=Hello World
        """.trimIndent()

        expectThat(
            generateKotlin(input, "package.name", "ClassName", "bundle.properties").lines()
                .joinToString("\n") { it.trim() })
            .isEqualTo(
                """
                package package.name
                
                class ClassName {
                  private val bundle: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("bundle.properties")
                  fun multiLine(): String = bundle.getString("multi.line")
                  fun singleLine(): String = bundle.getString("single.line")
                }
            """.trimIndent().lines().joinToString("\n") { it.trim() }
            )

        expectThat(
            generateJava(input, "package.name", "ClassName", "bundle.properties").lines()
                .joinToString("\n") { it.trim() })
            .isEqualTo(
                """
                package package.name;
                
                public final class ClassName {
                  private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle.properties");
                  public String multiLine() { return bundle.getString("multi.line"); }
                  public String singleLine() { return bundle.getString("single.line"); }
                }
            """.trimIndent().lines().joinToString("\n") { it.trim() }
            )
    }

    @Test
    fun annotatedInput() {
        val input = """
            #0=name: String
            multi.line=Hello \
            {0}
            #0=name: String
            #2=otherName: Integer
            single.line=Hello {0} and {1} and {2}
        """.trimIndent()

        expectThat(
            generateKotlin(input, "package.name", "ClassName", "bundle.properties").lines()
                .joinToString("\n") { it.trim() })
            .isEqualTo(
                """
                package package.name
                
                class ClassName {
                  private val bundle: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("bundle.properties")
                  fun multiLine(name: String): String = java.text.MessageFormat.format(bundle.getString("multi.line"), name)
                  fun singleLine(name: String, param1: String, otherName: Integer): String = java.text.MessageFormat.format(bundle.getString("single.line"), name, param1, otherName)
                }
            """.trimIndent().lines().joinToString("\n") { it.trim() }
            )

        expectThat(
            generateJava(input, "package.name", "ClassName", "bundle.properties").lines()
                .joinToString("\n") { it.trim() })
            .isEqualTo(
                """
                package package.name;
                
                public final class ClassName {
                  private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle.properties");
                  public String multiLine(String name) { return java.text.MessageFormat.format(bundle.getString("multi.line"), name); }
                  public String singleLine(String name, String param1, int otherName) { return java.text.MessageFormat.format(bundle.getString("single.line"), name, param1, otherName); }
                }
            """.trimIndent().lines().joinToString("\n") { it.trim() }
            )
    }
}