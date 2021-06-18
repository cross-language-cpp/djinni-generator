# Generator Setup

## Installation

!!! important

    The generator **requires Java**!

### Linux, macOS

#### Manually

[![GitHub release (latest by date)](https://img.shields.io/github/v/release/cross-language-cpp/djinni-generator?label=Download&logo=linux&logoColor=%23fff&style=for-the-badge)](https://github.com/cross-language-cpp/djinni-generator/releases/latest/download/djinni)

#### Install with [asdf](https://asdf-vm.com/)

```bash
asdf plugin add djinni
asdf install djinni latest
```

### Windows

[![GitHub release (latest by date)](https://img.shields.io/github/v/release/cross-language-cpp/djinni-generator?label=Download&logo=windows&style=for-the-badge)](https://github.com/cross-language-cpp/djinni-generator/releases/latest/download/djinni.bat)

### Conan

The generator is available at [conan-center](https://conan.io/center/djinni-generator) for Windows, Linux & macOS.

Add the generator as a build requirement in `conanfile.txt`:

```text
[build_requires]
djinni-generator/1.1.0
```
