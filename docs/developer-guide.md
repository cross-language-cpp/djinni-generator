# Developer Guide

!!! info

    This chapter is only interesting if you intend to make changes to the code of djinni generator

## Building from source

### Build dependencies

- Java JDK 8 or 11
- [sbt](https://www.scala-sbt.org/)

### Building

To build once:

```bash
sbt compile
```

To automatically re-build on every change, open the sbt shell & prefix `compile` with `~`

```bash
$ sbt
sbt:djinni> ~compile
```


### Running

```bash
sbt "run
    --idl input.djinni
    --cpp-out out/cpp
    --java-out out/java/src
    --jni-out out/java/jni
    --objc-out out/objc
    --py-out out/python
    --pycffi-out out/cffi
    --c-wrapper-out out/cwrapper
    --cppcli-out out/cppcli"
```

```bash
sbt "run --help"  # show all options
```

*It is important to put `run` and all arguments in `"`, to be able to pass arguments to the executed jar!*

### Running Tests

```bash
sbt it:test
```

### Packaging

Create a binary like the one that is published on github releases:

```bash
sbt assembly
```

This will generate a standalone, self-executing jar in `target/bin`.
You can run the jar like this:

```bash
./djinni --help
```

On Windows the file must be renamed to `djinni.bat` to make it executable.

!!! attention

    The resulting binary still requires Java to be able to run! [Details on how the self-executing jar works](https://github.com/sbt/sbt-assembly#prepending-a-launch-script).

### Code Formatting

All **scala**- and **sbt**-files need to be formatted with the [scala-fmt](https://scalameta.org/scalafmt/) plugin.

To format all files, execute `sbt scalafmtAll`.

The version of the scala format tool can be changed in the file `.scalafmt.conf` and the version of the scala plugin can be changed in the file `project/plugins.sbt`.

To change the code formatter configurations, put all configuration options inside `.scalafmt.conf`. See all configuration options [here](https://scalameta.org/scalafmt/docs/configuration.html). 

You can check if the formatter is working with the command `sbt scalafmtCheck `.

All available tasks of the code formatter plugin can be checked [here](https://scalameta.org/scalafmt/docs/installation.html#task-keys).

## Project Structure

```text
.
├── CODE_OF_CONDUCT.md (1)
├── LICENSE (2)
├── README.md (3)
├── build.sbt (4)
├── docs (5)
│   └── ...
├── mkdocs.yml (6)
├── project (7)
│   └── ...
└── src (8)
    ├── it (9)
    │   ├── resources (10)
    │   │   ├── expected (11)
    │   │   │   └── ...
    │   │   └── result (12)
    │   │       └── ...
    │   └── scala 
    │       └── djinni (13)
    │           └── ...
    └── main
        └── scala
            └── djinni (14)
                └── ...
```

1. Project Code of Conduct.
2. Project License (Apache License).
3. Readme file.
4. Sbt build configuration.
5. Documentation folder containing markdown documentation that will be rendered with MkDocs.
6. [MkDocs](https://www.mkdocs.org/) configuration. Will be included by the [cross-language-cpp.github.io](https://github.com/cross-language-cpp/cross-language-cpp.github.io) repository and published to [djinni.xlcpp.dev](https://djinni.xlcpp.dev/).
7. Sbt configuration.
8. Sources folder.
9. Integration testing directory.
10. Resources for integration tests.
11. Folder containing expected outcomes of the djinni generator. These files are matched against the real result of the generator in the integration tests.
12. Folder that will be used for generator outputs in the integration tests. Files in here should not be checked in to source control.
13. Folder containing the integration testing code.
14. djinni-generator source code.

## Preview Documentation

The documentation in `docs` will be rendered as a part of [djinni.xlcpp.dev](https://djinni.xlcpp.dev/).

You can preview how the docs will look like:

```sh
# install required dependencies
pip install -r mkdocs-requirements.txt
# render a live preview of the docs under http://127.0.0.1:8000
mkdocs serve 
```

## Release process

To release a new version of the generator, the following steps must be followed:

1. Create a [new release](https://github.com/cross-language-cpp/djinni-generator/releases/new) on Github. Set a tag version following [semantic versioning](https://semver.org/) rules (`v<MAJOR>.<MINOR>.<PATCH>`) and describe what has changed in the new version.
2. Wait. The [Github "release" Action](https://github.com/cross-language-cpp/djinni-generator/blob/main/.github/workflows/release.yaml) will automatically build the project and upload the resulting binaries to the release.
3. Create a PR to the [conan-center-index](https://github.com/conan-io/conan-center-index/tree/master/recipes/djinni-generator) to publish the new version to [Conan Center](https://conan.io/center/djinni-generator).
