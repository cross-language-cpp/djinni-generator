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
    --objc-out out/objc"
```

```bash
sbt "run --help"  # show all options
```

*It is important to put `run` and all arguments in `"`, to be able to pass arguments to the executed jar*

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
You can run the jar like this (no need for `java -jar`):

```bash
./djinni --help
```

On Windows the file must be renamed to `djinni.bat` to make it executable.

!!! attention

    The resulting binary still requires Java to able to run! [Details on how the self-executing jar works](https://github.com/sbt/sbt-assembly#prepending-a-launch-script).

## Project Structure

!!! bug "TODO"

## Release process

To release a new version of the generator, the following steps must be followed:

1. Create a new release-tag. Set a meaningful version number and describe what has changed in the new version.
2. Wait. Github will automatically build the project and upload the resulting binaries to the release.
