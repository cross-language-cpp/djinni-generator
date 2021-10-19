# Djinni Generator

[![CI](https://github.com/cross-language-cpp/djinni-generator/actions/workflows/main.yaml/badge.svg)](https://github.com/cross-language-cpp/djinni-generator/actions/workflows/main.yaml)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/cross-language-cpp/djinni-generator)](https://github.com/cross-language-cpp/djinni-generator/releases/latest)
![GitHub all releases](https://img.shields.io/github/downloads/cross-language-cpp/djinni-generator/total)

:arrow_right: Documentation: [djinni.xlcpp.dev](https://djinni.xlcpp.dev/djinni-generator/setup)

Djinni is a tool for generating cross-language type declarations and interface bindings. It's designed to connect C++ with either Java or Objective-C.

Djinni generator parses an interface definition file and generates:

- C++ implementations of types (enums, records)
- Java implementations of types
- Objective-C implementations of types
- Python implementation of types
- C++/CLI implementation of types
- C++ code to convert between C++ and Java over JNI
- C++ code to serialize/deserialize types to/from JSON
- Objective-C++ code to convert between C++ and Objective-C
- Python and C code to convert between C++ and Python over CFFI
- C++/CLI code to convert between C++ and C#


## Installation

Djinni **requires Java** to be able to execute!

### Linux, macOS

#### Manually

[![GitHub release (latest by date)](https://img.shields.io/github/v/release/cross-language-cpp/djinni-generator?label=Download&logo=linux&logoColor=%23fff&style=for-the-badge)](https://github.com/cross-language-cpp/djinni-generator/releases/latest/download/djinni)

#### Install with [asdf](https://asdf-vm.com/)

```bash
asdf plugin add djinni
asdf install djinni latest
```

#### MacOS homebrew


Add the tap:

```bash
brew tap cross-language-cpp/brew https://github.com/cross-language-cpp/brew.git
```

Install the djinni generator:

```bash
brew install djinni
```



### Windows

[![GitHub release (latest by date)](https://img.shields.io/github/v/release/cross-language-cpp/djinni-generator?label=Download&logo=windows&style=for-the-badge)](https://github.com/cross-language-cpp/djinni-generator/releases/latest/download/djinni.bat)

### Conan

The generator is available at [conan-center](https://conan.io/center/djinni-generator) for Windows, Linux & macOS.

Add the generator as a build requirement in `conanfile.txt`:

```text
[build_requires]
djinni-generator/1.2.0
```

## Credits

[Thanks goes to these contributors!](https://github.com/cross-language-cpp/djinni-generator/graphs/contributors)

The code in this repository is in large portions copied from [dropbox/djinni](https://github.com/dropbox/djinni) which was created by

- Kannan Goundan
- Tony Grue
- Derek He
- Steven Kabbes
- Jacob Potter
- Iulia Tamas
- Andrew Twyman
