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


## Python / C++ Project (Experimental)

Python support in Djinni is experimental, but ready to try out.  It can generate code for bridging
C++ with Python 3.

For more information, you can check out the talk from CppCon 2015.
[Slides](https://bit.ly/djinnitalk2) and [video](https://bit.ly/djinnivideo2) are available online.

### Includes & Build Target

When bridging to Python, Djinni generates 4 types of output:

* `python` Generated Python classes and proxies for interacting with C++ via [CFFI](https://cffi.readthedocs.org/).
* `cffi` Python code run at build time to create a Python extension out of the C++ code.
* `cwrapper` A C interface implemented in C++ to allowing Python to interact with C++ classes.
* `cpp` The same C++ classes generated for all other Djinni languages.

Generated files for Python / C++ are as follows:

| Type       | C++ header             | C++ source                   | Python files        | CFFI                | C Wrapper            |
|------------|------------------------|------------------------------|---------------------|---------------------|----------------------|
| Enum/Flags | my\_enum.hpp           |                              | my_enum.py          |                     | dh__my_enum.cpp      |
|            |                        |                              |                     |                     | dh__my_enum.h        |
|            |                        |                              |                     |                     | dh__my_enum.hpp      |
| Record     | my\_record[\_base].hpp | my\_record[\_base].cpp :one: | my_record[_base].py |                     | dh__my_record.cpp    |
|            |                        |                              |                     |                     | dh__my_record.h      |
|            |                        |                              |                     |                     | dh__my_record.hpp    |
| Interface  | my\_interface.hpp      | my\_interface.cpp :one:      | my_interface.py     | pycffi_lib_build.py | cw__my_interface.cpp |
|            |                        |                              |                     |                     | cw__my_interface.h   |
|            |                        |                              |                     |                     | cw__my_interface.hpp |

- :one: Generated only for types that contain constants.

Additional C Wrapper files are generated for data structures; their names are encoded as:

    dh__{list,set,map}_{encoded_type(s)}.cpp
    dh__{list,set,map}_{encoded_type(s)}.h
    dh__{list,set,map}_{encoded_type(s)}.hpp

See the in the table below a few examples:

| Type                    | C Wrapper                              |
|-------------------------|----------------------------------------|
| `list<i32>`             | dh__list_int32_t.{cpp,h,hpp}           |
| `set<string>`           | dh__set_string.{cpp,h,hpp}             |
| `map<i32, set<string>>` | dh__map_int32_t_set_string.{cpp,h,hpp} |

Add all generated C and C++ source files to your build target, and link it against the
[djinni-support-lib](https://github.com/cross-language-cpp/djinni-support-lib).

Compile the Python extension module (CFFI) by executing `pycffi_lib_build.py` while providing all C
Wrapper header files (`.h`) as arguments. The resulting shared library will enable Python to access
your C++ library through the CFFI bridge.

### Known limitations of the generator

* External types defined in YAML are not yet supported.
* Use of non-nullable pointers is not yet supported.


## C++/CLI / C++ Project

C++/CLI is a technology by Microsoft that provides interoperability of C++ with Microsoft .NET languages such as C#.
It is only supported on Windows.

Djinni generates a shallow C++/CLI wrapper around the C++ interfaces. Once compiled to a shared library, the resulting `dll`
just needs to be added to your C# project as reference, and you can call your Djinni interfaces from C# like any other .NET library.

### Includes & Build target

The following code will be generated for each defined type:

| Type       | C++ header             | C++ source                 | C++/CLI header/sources              |              
|------------|------------------------|----------------------------|-------------------------------------|
| Enum/Flags | my\_enum.hpp           |                            | MyEnum.hpp, MyEnum.cpp              | 
| Record     | my\_record.hpp         | my\_record.cpp             | MyRecord.hpp, MyRecord.cpp          |
| Interface  | my\_interface.hpp      | my\_interface.cpp (+)      | MyInterface.hpp, MyInterface.cpp    |

Add all generated files to your build target, and link against the [djinni-support-lib](https://github.com/cross-language-cpp/djinni-support-lib).

C++/CLI sources have to be compiled with MSVC and the [`/clr` (Common Language Runtime Compilation)](https://docs.microsoft.com/en-us/cpp/build/reference/clr-common-language-runtime-compilation?view=msvc-160) option.

