package io.github.hanseter.bundle

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
                
                class ClassName(private val translate: TranslationProvider) {
                  fun  interface TranslationProvider {
                    operator fun invoke(key: String): String
                  }
                  constructor() : this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString)
                  fun foo(): String = translate("foo")
                  fun fooBar(): String = translate("foo.bar")
                  companion object {
                   const val BUNDLE_PATH = "bundle.properties"
                  }
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
                  public static final String BUNDLE_PATH = "bundle.properties";
                  public interface TranslationProvider {
                    String translate(String key);
                  }
                  private final TranslationProvider translator;
                  public ClassName() {
                   this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString);
                  }
                  public ClassName(TranslationProvider translator) {
                   this.translator = translator;
                  }
                  public String foo() { return translator.translate("foo"); }
                  public String fooBar() { return translator.translate("foo.bar"); }
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
                
                class ClassName(private val translate: TranslationProvider) {
                  fun  interface TranslationProvider {
                    operator fun invoke(key: String): String
                  }
                  constructor() : this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString)
                  fun multiLine(): String = translate("multi.line")
                  fun singleLine(): String = translate("single.line")
                  companion object {
                   const val BUNDLE_PATH = "bundle.properties"
                  }
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
                  public static final String BUNDLE_PATH = "bundle.properties";
                  public interface TranslationProvider {
                    String translate(String key);
                  }
                  private final TranslationProvider translator;
                  public ClassName() {
                   this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString);
                  }
                  public ClassName(TranslationProvider translator) {
                   this.translator = translator;
                  }
                  public String multiLine() { return translator.translate("multi.line"); }
                  public String singleLine() { return translator.translate("single.line"); }
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
                
                class ClassName(private val translate: TranslationProvider) {
                  fun  interface TranslationProvider {
                    operator fun invoke(key: String): String
                  }
                  constructor() : this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString)
                  fun multiLine(name: String): String = java.text.MessageFormat.format(translate("multi.line"), name)
                  fun singleLine(name: String, param1: String, otherName: Integer): String = java.text.MessageFormat.format(translate("single.line"), name, param1, otherName)
                  companion object {
                   const val BUNDLE_PATH = "bundle.properties"
                  }
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
                  public static final String BUNDLE_PATH = "bundle.properties";
                  public interface TranslationProvider {
                    String translate(String key);
                  }
                  private final TranslationProvider translator;
                  public ClassName() {
                   this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString);
                  }
                  public ClassName(TranslationProvider translator) {
                   this.translator = translator;
                  }
                  public String multiLine(String name) { return java.text.MessageFormat.format(translator.translate("multi.line"), name); }
                  public String singleLine(String name, String param1, int otherName) { return java.text.MessageFormat.format(translator.translate("single.line"), name, param1, otherName); }
                }
            """.trimIndent().lines().joinToString("\n") { it.trim() }
            )
    }
}