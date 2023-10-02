package com.github.hanseter

class ExampleI18n {
private val bundle: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("exampleI18n.properties")
fun foo(): String = bundle.getString("foo")
}