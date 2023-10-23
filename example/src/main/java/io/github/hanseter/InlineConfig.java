package io.github.hanseter;

public final class InlineConfig {
 public static final String BUNDLE_PATH = "io/github/hanseter/InlineConfig";
 public interface TranslationProvider {
  String translate(String key);
 }
 private final TranslationProvider translator;
 public InlineConfig() {
  this(java.util.ResourceBundle.getBundle(BUNDLE_PATH)::getString);
 }
 public InlineConfig(TranslationProvider translator) {
  this.translator = translator;
 }
public String exampleString() { return translator.translate("example.string"); }
}