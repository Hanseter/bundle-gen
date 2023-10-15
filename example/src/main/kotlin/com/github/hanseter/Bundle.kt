package com.github.hanseter

class Bundle {
private val bundle: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("nested/bundle")
fun key(): String = bundle.getString("key")
}