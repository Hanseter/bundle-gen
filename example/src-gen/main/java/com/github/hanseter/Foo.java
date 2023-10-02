package com.github.hanseter;

public final class Foo {
private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("exampleI18n.properties");
public String foo() { return bundle.getString("foo"); }
}