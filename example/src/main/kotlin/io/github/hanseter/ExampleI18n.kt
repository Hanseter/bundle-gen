package io.github.hanseter

class ExampleI18n(private val translate: TranslationProvider) {
 fun  interface TranslationProvider {
  operator fun invoke(key: String): String
 }
 constructor() : this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString)
fun foo(): String = translate("foo")
 companion object {
  const val BUNDLE_PATH = "exampleI18n"
 }
}