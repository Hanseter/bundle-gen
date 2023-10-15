package io.github.hanseter;

public final class InlineConfig {
private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("io/github/hanseter/InlineConfig");
public String exampleString() { return bundle.getString("example.string"); }
}