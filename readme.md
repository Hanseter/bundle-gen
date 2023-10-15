# Bundle Gen

Generates Java and/or Kotlin classes from your i18n bundle files.

## Example

For the following resource file:

```properties
foo=This is a foo.
foo.bar=This is a bar.
```

Would generate the following Java code.

```java
package example;

public final class ExampleClass {
    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle.properties");

    public String foo() {
        return bundle.getString("foo");
    }

    public String fooBar() {
        return bundle.getString("foo.bar");
    }
}

```

## Usage

### Input format

The input format is the regular java resource bundle format.
So you do not need to adapt anything to use this library.

The actual values of the keys are assumed to be in the `java.text.MessageFormat` format.
So a value `Example {0}` would generate the method `public String example(String param1)`.
In order to improve the API readability you can provide comments for each key value pair in the following format.

```properties
#0=paramName: String
#1=secondParam: Double
foo=Example {0} and {1}
```

The following types are supported:

- Integer
- Double
- Long
- Float
- String

If no type is specified `String` is assumed.

For now only `.properties` files are supported.

### Maven

There is a maven plugin to easily integrate this plugin into your maven build.

In the maven plugin you can either provide a list of all bundle files that shall be processed, or you can simply add the
needed configuration to the top of a bundle file inline as comments and let the plugin automatically find these files.
When using the inline configuration please make sure, to only include it in one properties file of the bundle.
Examples for both can be found in the `example` module.