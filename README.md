# Djinni Generator

Parses an interface definition file and generates:
- C++ implementations of types (enums, records)
- Java implementations of types
- Objective-C implementations of types
- C++ code to convert between C++ and Java over JNI
- Objective-C++ code to convert between C++ and Objective-C.

## Build dependencies

- Java 8
- sbt

## Building

To build once:

```bash
sbt compile
```

To automatically re-build on every change, open the sbt shell & prefix `compile` with `~`

```bash
$ sbt
sbt:djinni> ~compile
```


## Running

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

## Packaging

To create a standalone jar run

```bash
sbt assembly
```

This will generate a standalone, self-executing jar in `target/bin`.
You can run the jar like this (no need for `java -jar`):

```bash
./djinni --help
```

On Windows the file must be renamed to `djinni.bat` to make it executable.
