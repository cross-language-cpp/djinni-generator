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

Add all generated source files to your build target, as well as the contents of
`support-lib/java`.

### Our JNI approach
JNI stands for Java Native Interface, an extension of the Java language to allow interop with
native (C/C++) code or libraries. Complete documentation on JNI is available at:
http://docs.oracle.com/javase/6/docs/technotes/guides/jni/spec/jniTOC.html

For each type, built-in (`list`, `string`, etc.) or user-defined, Djinni produces a translator
class with a `toJava` and `fromJava` function to translate back and forth.

Application code is responsible for the initial load of the JNI library. Add a static block
somewhere in your code:

    System.loadLibrary("YourLibraryName");
    // The name is specified in Android.mk / build.gradle / Makefile, depending on your build system.

If you package your native library in a jar, you can also use `com.dropbox.djinni.NativeLibLoader` 
to help unpack and load your lib(s).  See the [Localhost README](example/localhost/README.md)
for details.

When a native library is called, JNI calls a special function called `JNI_OnLoad`. If you use
Djinni for all JNI interface code, include `support_lib/jni/djinni_main.cpp`; if not,
you'll need to add calls to your own `JNI_OnLoad` and `JNI_OnUnload` functions. See
`support-lib/jni/djinni_main.cpp` for details.

## Objective-C / C++ Project

### Includes & Build Target
Generated files for Objective-C / C++ are as follows (assuming prefix is `DB`):

| Type       | C++ header             | C++ source                 | Objective-C files        | Objective-C++ files         |
|------------|------------------------|----------------------------|--------------------------|-----------------------------|
| Enum/Flags | my\_enum.hpp           |                            | DBMyEnum.h               |                             |
| Record     | my\_record[\_base].hpp | my\_record[\_base].cpp (+) | DBMyRecord[Base].h       | DBMyRecord[Base]+Private.h  |
|            |                        |                            | DBMyRecord[Base].mm (++) | DBMyRecord[Base]+Private.mm |
| Interface  | my\_interface.hpp      | my\_interface.cpp (+)      | DBMyInterface.h          | DBMyInterface+Private.h     |
|            |                        |                            |                          | DBMyInterface+Private.mm    |

(+) Generated only for types that contain constants.
(++) Generated only for types with derived operations and/or constants. These have `.mm` extensions to allow non-trivial constants.

Add all generated files to your build target, as well as the contents of `support-lib/objc`.
Note that `+Private` files can only be used with ObjC++ source (other headers are pure ObjC) and are not required by Objective-C users of your interface.