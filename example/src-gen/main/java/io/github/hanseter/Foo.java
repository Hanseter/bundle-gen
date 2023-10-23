package io.github.hanseter;

public final class Foo {
 public static final String BUNDLE_PATH = "exampleI18n";
 public interface TranslationProvider {
  String translate(String key);
 }
 private final TranslationProvider translator;
 public Foo() {
  this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString);
 }
 public Foo(TranslationProvider translator) {
  this.translator = translator;
 }
public String foo() { return translator.translate("foo"); }
}