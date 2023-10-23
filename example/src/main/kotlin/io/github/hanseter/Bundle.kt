package io.github.hanseter

class Bundle(private val translate: TranslationProvider) {
 fun  interface TranslationProvider {
  operator fun invoke(key: String): String
 }
 constructor() : this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString)
fun key(): String = translate("key")
 companion object {
  const val BUNDLE_PATH = "nested/bundle"
 }
}