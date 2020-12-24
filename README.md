# Djinni Generator

![Build](https://github.com/cross-language-cpp/djinni-generator/workflows/CI/badge.svg)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/cross-language-cpp/djinni-generator)

:arrow_right: Documentation: [djinni.xlcpp.dev](https://djinni.xlcpp.dev/djinni-generator/setup/)

Djinni is a tool for generating cross-language type declarations and interface bindings. It's designed to connect C++ with either Java or Objective-C.

Djinni generator parses an interface definition file and generates:

- C++ implementations of types (enums, records)
- Java implementations of types
- Objective-C implementations of types
- C++ code to convert between C++ and Java over JNI
- Objective-C++ code to convert between C++ and Objective-C.


## Installation

Djinni **requires Java** to be able to execute!

### Linux, macOS

Download the [lastest released binary](https://github.com/cross-language-cpp/djinni-generator/releases/latest/download/djinni), make it executable and put the containing folder on your PATH:

```bash
curl -LO https://github.com/cross-language-cpp/djinni-generator/releases/latest/download/djinni
chmod u+x djinni
export PATH=$(pwd):$PATH
```

Make sure to [put it on your PATH permanently](https://stackabuse.com/how-to-permanently-set-path-in-linux/) once you fell in love with djinni! :blush:

### Windows

Download the [latest released batch file](https://github.com/cross-language-cpp/djinni-generator/releases/latest/download/djinni.bat), copy it where you want and [put the folder on your PATH](https://www.architectryan.com/2018/03/17/add-to-the-path-on-windows-10/).
