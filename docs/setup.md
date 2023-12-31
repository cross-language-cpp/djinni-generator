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

#### MacOS homebrew

We have a brew tap.

Add the tap:

```bash
brew tap cross-language-cpp/brew https://github.com/cross-language-cpp/brew.git
```

Install the djinni generator:

```bash
brew install djinni
```

### Windows manually

You can download the generator manually and place it in your `PATH`

[![GitHub release (latest by date)](https://img.shields.io/github/v/release/cross-language-cpp/djinni-generator?label=Download&logo=windows&style=for-the-badge)](https://github.com/cross-language-cpp/djinni-generator/releases/latest/download/djinni.bat)

### Windows via Scoop

You can use the [scoop package manager](https://scoop.sh/).

We maintain a [djinni bucket](https://github.com/cross-language-cpp/djinni-bucket), the [README contains instructions on how to add it to scoop](https://github.com/cross-language-cpp/djinni-bucket/blob/main/README.md).

### Conan

Please note that the Conan recipe is not maintained by us but by the Conan community.

The generator is available at the [conan-center](https://conan.io/center/djinni-generator) for Windows, Linux & macOS.

Add the generator as a build requirement in `conanfile.txt` or your `conanfile.py` and it will be available for you.
