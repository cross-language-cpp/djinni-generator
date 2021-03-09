# Using the Generator

When the Djinni file(s) are ready, from the command line you can run:

```bash
djinni \
   --java-out JAVA_OUTPUT_FOLDER \
   --java-package com.example.jnigenpackage \
   --java-cpp-exception DbxException \ # Choose between a customized C++ exception in Java and java.lang.RuntimeException (the default).
   --ident-java-field mFooBar \ # Optional, this adds an "m" in front of Java field names
   \
   --cpp-out CPP_OUTPUT_FOLDER \
   \
   --jni-out JNI_OUTPUT_FOLDER \
   --ident-jni-class NativeFooBar \ # This adds a "Native" prefix to JNI class
   --ident-jni-file NativeFooBar \ # This adds a prefix to the JNI filenames otherwise the cpp and jni filenames are the same.
   \
   --objc-out OBJC_OUTPUT_FOLDER \
   --objc-type-prefix DB \ # Apple suggests Objective-C classes have a prefix for each defined type.
   \
   --objcpp-out OBJC_OUTPUT_FOLDER \
   \
   --idl MY_PROJECT.djinni

```

!!! note

    If a language's output folder is not specified, that language will not be generated.

## All Arguments

### General

| Argument | Description |
| -------- | ----------- |
| `--help` | Print help  |
| `--version` | Print version |
| `--idl <in-file>` | The IDL file with the type definitions, typically with extension `.djinni`. |
| `--idl-include-path <path> ...` | An include path to search for Djinni @import directives. Can specify multiple paths. |

### Java
| Argument | Description |
| -------- | ----------- |
| `--java-out <out-folder>` | The output for the Java files (Generator disabled if unspecified). |
| `--java-package ...` | The package name to use for generated Java classes. |
| `--java-class-access-modifier <public/package>` | The access modifier to use for generated Java classes (default: `public`). |
| `--java-cpp-exception <exception-class>` | The type for translated C++ exceptions in Java (default: `java.lang.RuntimeException` that is not checked) |
| `--java-annotation <annotation-class>` | Java annotation (`@Foo`) to place on all generated Java classes |
| `--java-generate-interfaces <true/false>` | Whether Java interfaces should be used instead of abstract classes where possible (default: false). |
| `--java-nullable-annotation <nullable-annotation-class>` | Java annotation (`@Nullable`) to place on all fields and return values that are optional |
| `--java-nonnull-annotation <nonnull-annotation-class>` | Java annotation (`@Nonnull`) to place on all fields and return values that are not optional |
| `--java-implement-android-os-parcelable <true/false>` | all generated java classes will implement the interface android.os.Parcelable |
| `--java-use-final-for-record <use-final-for-record>` | Whether generated Java classes for records should be marked `final` (default: `true`). |

### C++

| Argument | Description |
| -------- | ----------- |
| `--cpp-out <out-folder>` | The output folder for C++ files (Generator disabled if unspecified). |
| `--cpp-header-out <out-folder>` | The output folder for C++ header files (default: the same as `--cpp-out`). |
| `--cpp-include-prefix <prefix>` | The prefix for #includes of header files from C++ files. |
| `--cpp-namespace ...` | The namespace name to use for generated C++ classes. |
| `--cpp-ext <ext>` | The filename extension for C++ files (default: `cpp`). |
| `--hpp-ext <ext>` | The filename extension for C++ header files (default: `hpp`). |
| `--cpp-optional-template <template>` | The template to use for optional values (default: `std::optional`) |
| `--cpp-optional-header <header>` | The header to use for optional values (default: `<optional>`) |
| `--cpp-enum-hash-workaround <true/false>` | Work around LWG-2148 by generating std::hash specializations for C++ enums (default: `true`) |
| `--cpp-nn-header <header>` | The header to use for non-nullable pointers |
| `--cpp-nn-type <header>` | The type to use for non-nullable pointers (as a substitute for `std::shared_ptr`) |
| `--cpp-nn-check-expression <header>` | The expression to use for building non-nullable pointers |
| `--cpp-use-wide-strings <true/false>` | Use wide strings in C++ code (default: `false`) |

### JNI

| Argument | Description |
| -------- | ----------- |
| `--jni-out <out-folder>` | The folder for the JNI C++ output files (Generator disabled if unspecified). |
| `--jni-header-out <out-folder>` | The folder for the JNI C++ header files (default: the same as `--jni-out`). |
| `--jni-include-prefix <prefix>` | The prefix for #includes of JNI header files from JNI C++ files. |
| `--jni-include-cpp-prefix <prefix>` | The prefix for #includes of the main header files from JNI C++ files. |
| `--jni-namespace ...` | The namespace name to use for generated JNI C++ classes. |
| `--jni-base-lib-include-prefix ...` | The JNI base support library's include path (default: djinni/jni/). |

### Objective-C

| Argument | Description |
| -------- | ----------- |
| `--objc-out <out-folder>` | The output folder for Objective-C files (Generator disabled if unspecified). |
| `--objc-header-out <out-folder>` | The output folder for Objective-C header files (default: the same as --objc-out). |
| `--objc-h-ext <ext>` | The filename extension for Objective-C[++] header files (default: `h`) |
| `--objc-type-prefix <pre>` | The prefix for Objective-C data types (usually two or three letters) |
| `--objc-include-prefix <prefix>` | The prefix for #import of header files from Objective-C files. |
| `--objc-swift-bridging-header <name>` | The name of Objective-C Bridging Header used in XCode's Swift projects. |
| `--objc-closed-enums <true/false>` | All generated Objective-C enums will be NS_CLOSED_ENUM (default: `false`). |

### Objective-C++

| Argument | Description |
| -------- | ----------- |
| `--objcpp-out <out-folder>` | The output folder for private Objective-C++ files (Generator disabled if unspecified). |
| `--objcpp-ext <ext>` | The filename extension for Objective-C++ files (default: `mm`) |
| `--objcpp-include-prefix <prefix>` | The prefix for #import of Objective-C++ header files from Objective-C++ files. |
| `--objcpp-include-cpp-prefix <prefix>` | The prefix for #include of the main C++ header files from Objective-C++ files. |
| `--objcpp-include-objc-prefix <prefix>` | The prefix for #import of the Objective-C header files from Objective-C++ files (default: the same as `--objcpp-include-prefix`) |
| `--cpp-extended-record-include-prefix <prefix>` | The prefix path for #include of the extended record C++ header (`.hpp`) files |
|`--objc-extended-record-include-prefix <prefix>` | The prefix path for #import of the extended record Objective-C header (`.h`) files  |
|`--objcpp-namespace <prefix>` | The namespace name to use for generated Objective-C++ classes.  |
|`--objc-base-lib-include-prefix ...` | The Objective-C base support library's include path (default: djinni/objc/).  |

### Yaml Generation

| Argument | Description |
| -------- | ----------- |
|`--yaml-out <out-folder>` | The output folder for YAML files (Generator disabled if unspecified).  |
|`--yaml-out-file <out-file>` | If specified all types are merged into a single YAML file instead of generating one file per type (relative to `--yaml-out`).  |
|`--yaml-prefix <pre>` | The prefix to add to type names stored in YAML files (default: "").  |

### Other

| Argument | Description |
| -------- | ----------- |
|`--list-in-files <list-in-files>` | Optional file in which to write the list of input files parsed.  |
| `--list-out-files <list-out-files>` | Optional file in which to write the list of output files produced.  |
| `--skip-generation <true/false>` | Way of specifying if file generation should be skipped (default: `false`)  |

### Identifier Style

Identifier styles (ex: `FooBar`, `fooBar`, `foo_bar`, `FOO_BAR`, `m_fooBar`)

| Argument |
| -------- |
| `--ident-java-enum ...` |
| `--ident-java-field ...` |
| `--ident-java-type ...` |
| `--ident-cpp-enum ...` |
| `--ident-cpp-field ...` |
| `--ident-cpp-method ...` |
| `--ident-cpp-type ...` |
| `--ident-cpp-enum-type ...` |
| `--ident-cpp-type-param ...` |
| `--ident-cpp-local ...` |
| `--ident-cpp-file ...` |
| `--ident-jni-class ...` |
| `--ident-jni-file ...` |
| `--ident-objc-enum ...` |
| `--ident-objc-field ...` |
| `--ident-objc-method ...` |
| `--ident-objc-type ...` |
| `--ident-objc-type-param ...` |
| `--ident-objc-local ...` |
| `--ident-objc-file ...` |

The _Identifier Style_ is a text conversion hint on how to generate names from the djinni idl file.

Example:
The djinni idl for an enum

```
SomeEnum = enum {
    Value;
}
```

transforms per default to the following Java code

```
public enum SomeEnum {
    VALUE,
    ;
}
```
As you see, _VALUE_ is now in upper case letters.

If you use `--ident-java-enum foo_bar` then _Value_ will not be transformed into upper case and the following Java code will be generated.

```
public enum SomeEnum {
    Value,
    ;
}
```

This way you can adopt code generation to some extend to your existing coding style.


The best way to find out how that works is playing around with different values for different identifier options.

**If you wish not text transformation to happen, use the `foo_bar` style.**

!!! note

    Some identifiers do have text transformation enabled by default.
    Nevertheless make sure to **explicitly set** text transformation styles if you need them! All text transformation may be disabled by default in a future release of the generator.

### Default Identifier Style settings
(Please note that this list is work in progress)
#### C++

| Argument | Default  |
| -------- | -------- |
| `--ident-cpp-enum` | FOO_BAR |
| `--ident-cpp-field` | foo_bar |
| `--ident-cpp-method` | foo_bar |
| `--ident-cpp-type` | FooBar |
| `--ident-cpp-enum-type` | FooBar |
| `--ident-cpp-type-param` | FooBar |
| `--ident-cpp-local` | foo_bar |
| `--ident-cpp-file` | foo_bar |

#### Java

| Argument | Default  |
| -------- | -------- |
| `--ident-java-enum` | FOO_BAR |
| `--ident-java-field` | fooBar |
| `--ident-java-type` | FooBar |
| `--ident-jni-class` | FooBar |
| `--ident-jni-file` | foo_bar |

#### Objective C

| Argument | Default      |
| -------- | -------- |
| `--ident-objc-enum` | FooBar |
| `--ident-objc-field` | fooBar |
| `--ident-objc-method` | fooBar |
| `--ident-objc-type` | FooBar |
| `--ident-objc-type-param` | FooBar |
| `--ident-objc-local` | fooBar |
| `--ident-objc-file` | FooBar |
