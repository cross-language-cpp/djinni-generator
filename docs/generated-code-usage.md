# Use Generated Code

## Java / JNI / C++ Project

### Includes & Build target
The following headers / code will be generated for each defined type:

| Type       | C++ header             | C++ source                 | Java                | JNI header            | JNI source            |
|------------|------------------------|----------------------------|---------------------|-----------------------|-----------------------|
| Enum/Flags | my\_enum.hpp           |                            | MyEnum.java         | NativeMyEnum.hpp      | NativeMyEnum.cpp      |
| Record     | my\_record[\_base].hpp | my\_record[\_base].cpp (+) | MyRecord[Base].java | NativeMyRecord.hpp    | NativeMyRecord.cpp    |
| Interface  | my\_interface.hpp      | my\_interface.cpp (+)      | MyInterface.java    | NativeMyInterface.hpp | NativeMyInterface.cpp |

(+) Generated only for types that contain constants.

Add all generated source files to your build target, and link the C++ code against the [djinni-support-lib](https://github.com/cross-language-cpp/djinni-support-lib).

### Our JNI approach
JNI stands for [Java Native Interface](http://docs.oracle.com/javase/6/docs/technotes/guides/jni/spec/jniTOC.html), an extension of the Java language to allow interop with
native (C/C++) code or libraries.


For each type, built-in (`list`, `string`, etc.) or user-defined, Djinni produces a translator
class with a `toJava` and `fromJava` function to translate back and forth.

Application code is responsible for the initial load of the JNI library. Add a static block
somewhere in your code:

```java
class Main {
    static {
      System.loadLibrary("YourLibraryName");
      // The name  of the library is specified in Android.mk / build.gradle / Makefile / CMakeLists.txt, 
      // depending on your build system.
    }
}
```

If you package your native library in a jar, you can also use the [`NativeLibLoader`](https://github.com/cross-language-cpp/djinni-support-lib/blob/main/java/com/dropbox/djinni/NativeLibLoader.java)
to help unpack and load your lib(s).

When a native library is called, JNI calls a special function called [`JNI_OnLoad`](https://github.com/cross-language-cpp/djinni-support-lib/blob/main/djinni/jni/djinni_main.cpp#L23).

If your app doesn't use JNI except through Djinni, include [`djinni/jni/djinni_main.cpp`](https://github.com/cross-language-cpp/djinni-support-lib/blob/main/djinni/jni/djinni_main.cpp). 
It defines default `JNI_Onload` and `JNI_OnUnload` functions for Djinni.

If your app also includes a non-Djinni JNI interface, you'll need to define your own `JNI_OnLoad` and `JNI_OnUnload` functions.

## Objective-C / C++ Project

### Includes & Build Target
Generated files for Objective-C / C++ are as follows (assuming prefix is `DB`):

| Type       | C++ header             | C++ source                   | Objective-C files         | Objective-C++ files         |
|------------|------------------------|------------------------------|---------------------------|-----------------------------|
| Enum/Flags | my\_enum.hpp           |                              | DBMyEnum.h                |                             |
| Record     | my\_record[\_base].hpp | my\_record[\_base].cpp :one: | DBMyRecord[Base].h        | DBMyRecord[Base]+Private.h  |
|            |                        |                              | DBMyRecord[Base].mm :two: | DBMyRecord[Base]+Private.mm |
| Interface  | my\_interface.hpp      | my\_interface.cpp :one:      | DBMyInterface.h           | DBMyInterface+Private.h     |
|            |                        |                              |                           | DBMyInterface+Private.mm    |

- :one: Generated only for types that contain constants.
- :two: Generated only for types with derived operations and/or constants. These have `.mm` extensions to allow non-trivial constants.

Add all generated files to your build target, and link against the [djinni-support-lib](https://github.com/cross-language-cpp/djinni-support-lib).

Note that `+Private` files can only be used with ObjC++ source (other headers are pure ObjC) and are not required by Objective-C users of your interface.
