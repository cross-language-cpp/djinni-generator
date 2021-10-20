# Interface Definition Language (IDL)

Djinni's input is an interface description file. Here's an example:

```
# Multi-line comments can be added here. This comment will be propagated
# to each generated definition.
my_enum = enum {
    option1;
    option2;
    option3;
}

my_flags = flags {
    flag1;
    flag2;
    flag3;
    no_flags = none;
    all_flags = all;
}

my_record = record {
    id: i32;
    info: string;
    store: set<string>;
    hash: map<string, i32>;

    values: list<another_record>;

    # Comments can also be put here

    # Constants can be included
    const string_const: string = "Constants can be put here";
    const min_value: another_record = {
        key1 = 0,
        key2 = ""
    };
}

another_record = record {
    key1: i32;
    key2: string;
} deriving (eq, ord)

# This interface will be implemented in C++ and can be called from any language.
my_cpp_interface = interface +c {
    method_returning_nothing(value: i32);
    method_returning_some_type(key: string): another_record;
    static get_version(): i32;

    # Interfaces can also have constants
    const version: i32 = 1;
}

# This interface will be implemented in Java, ObjC, Python and C# and can be called from C++.
my_client_interface = interface +j +o +p +s {
    log_string(str: string): bool;
}
```

Djinni files can also include each other. Adding the line:

```
@import "relative/path/to/filename.djinni"
```

at the beginning of a file will simply include another file. Child file paths are
relative to the location of the file that contains the @import. Two different djinni files
cannot define the same type. `@import` behaves like `#include` with `#pragma once` in C++, or
like ObjC's `#import`: if a file is included multiple times through different paths, then it
will only be processed once.

## Data Types

The available data types for a record, argument, or return value, and their equivalent in the target languages, are:

=== "C++"

    | Djinni                    | C++                                                                              |
    |---------------------------|----------------------------------------------------------------------------------|
    | `bool`                    | `bool`                                                                           |
    | `i8`, `i16`, `i32`, `i64` | `int8_t`, `int16_t`, `int32_t`, `int64_t`                                        |
    | `f32`, `f64`              | `float`, `double`                                                                |
    | `string`                  | `std::string`                                                                    |
    | `binary`                  | `std::vector<uint8_t>`                                                           |
    | `date`                    | `chrono::system_clock::time_point`                                               |
    | `list<T>`                 | `std::vector<T>`                                                                 |
    | `set<T>`                  | `std::unordered_set<T>`                                                          |
    | `map<K, V>`               | `std::unordered_map<K, V>`                                                       |
    | `optional<T>`             | `std::optional<T>` for value types and  `std::shared_ptr<T>` for reference types |


=== "Java"

    | Djinni                    | Java                                                         | Boxed                              |
    |---------------------------|--------------------------------------------------------------|------------------------------------|
    | `bool`                    | `boolean`                                                    | `Boolean`                          |
    | `i8`, `i16`, `i32`, `i64` | `byte`, `short`, `int`, `long`                               | `Byte`, `Short`, `Integer`, `Long` |
    | `f32`, `f64`              | `float`, `double`                                            | `Float`, `Double`                  |
    | `string`                  | `String`                                                     |                                    |
    | `binary`                  | `byte[]`                                                     |                                    |
    | `date`                    | `java.util.Date`                                             |                                    |
    | `list<T>`                 | `java.util.ArrayList<T>` ✱                                   |                                    |
    | `set<T>`                  | `java.util.HashSet<T>` ✱                                     |                                    |
    | `map<K, V>`               | `java.util.HashMap<K, V>` ✱                                  |                                    |
    | `optional<T>`             | object / boxed primitive reference<br>(which can be  `null`) |                                    |

=== "Objective-C"

    | Djinni                    | Objective-C                               | Boxed      |
    |---------------------------|-------------------------------------------|------------|
    | `bool`                    | `BOOL`                                    | `NSNumber` |
    | `i8`, `i16`, `i32`, `i64` | `int8_t`, `int16_t`, `int32_t`, `int64_t` | `NSNumber` |
    | `f32`, `f64`              | `float`, `double`                         | `NSNumber` |
    | `string`                  | `NSString`                                |            |
    | `binary`                  | `NSData`                                  |            |
    | `date`                    | `NSDate`                                  |            |
    | `list<T>`                 | `NSArray` ✱                               |            |
    | `set<T>`                  | `NSSet` ✱                                 |            |
    | `map<K, V>`               | `NSDictionary` ✱                          |            |
    | `optional<T>`             | strong reference (which can be`nil`)      |            |

=== "C#"

    | Djinni                    | C#                                            |
    |---------------------------|-----------------------------------------------|
    | `bool`                    | `bool`                                        |
    | `i8`, `i16`, `i32`, `i64` | `sbyte`, `short`, `int`, `long`               |
    | `f32`, `f64`              | `float`, `double`                             |
    | `string`                  | `System.String`                               |
    | `binary`                  | `System.Array<System.Byte>`                   |
    | `date`                    | `System.DateTime`                             |
    | `list<T>`                 | `System.Collections.Generic.List<T>`          |
    | `set<T>`                  | `System.Collections.Generic.HashSet<T>`       |
    | `map<K, V>`               | `System.Collections.Generic.Dictionary<K, V>` |
    | `optional<T>`             | `System.Nullable<T>`                          |

=== "Python"

    | Djinni                    | Python                                    |
    |---------------------------|-------------------------------------------|
    | `bool`                    |                                           |
    | `i8`, `i16`, `i32`, `i64` |                                           |
    | `f32`, `f64`              |                                           |
    | `string`                  |                                           |
    | `binary`                  | object supporting the `buffer` interface  |
    | `date`                    | `datetime.datetime`                       |
    | `list<T>`                 | `List`                                    |
    | `set<T>`                  | `Set`                                     |
    | `map<K, V>`               | `Dictionary`                              |
    | `optional<T>`             |                                           |


✱ *Primitives will be boxed in Java and Objective-C.*

Additional possible data types are: 

- Enumerations / Flags
- Other record types. This is generated with a by-value semantic, i.e. the copy method will
  deep-copy the contents.

## Types

An IDL file can contain 4 kinds of declarations: enums, flags, records, and interfaces.

* [**Enums**](#enums) become C++ enum classes, Java enums, ObjC `NS_ENUM`s, Python `IntEnum`s, or C# `System.Enum`s.
* [**Flags**](#flags) become C++ enum classes with convenient bit-oriented operators, Java enums with `EnumSet`, 
  ObjC `NS_OPTIONS`, Python `IntFlag`s, or C# `System.Enum`s with the [`[Flags]` Attribute](https://docs.microsoft.com/en-us/dotnet/api/system.flagsattribute?view=net-5.0).
* [**Records**](#records) are pure-data value objects.
* [**Interfaces**](#interfaces) are objects with defined methods to call (in C++, passed by `shared_ptr`). Djinni
  produces code allowing an interface implemented in C++ to be transparently used from ObjC,
  Java, Python or C#, and vice versa.

### Enums

```
my_enum = enum {
    option1;
    option2;
    option3;
}
```

Enums are translated to C++ `enum class`es with underlying type `int`, ObjC `NS_ENUM`s with
underlying type `NSInteger`, Java enums, Python `IntEnum`s, and C# `System.Enum`s.

### Flags

```
my_flags = flags {
    flag1;
    flag2;
    flag3;
    no_flags = none;
    all_flags = all;
}
```

Flags are translated to C++ `enum class`es with underlying type `unsigned` and a generated set of
overloaded bitwise operators for convenience, ObjC `NS_OPTIONS` with underlying type `NSUInteger`,
Java `EnumSet<>`, Python `IntFlag`, and C# `System.Enum`s with the [`[Flags]` Attribute](https://docs.microsoft.com/en-us/dotnet/api/system.flagsattribute?view=net-5.0). 
Contrary to the above enums, the enumerants of flags represent single bits instead of integral values.

In the above example the elements marked with `none` and `all` are given special meaning.  In C++,
ObjC, and Python the `no_flags` option is generated with a value that has no bits set (i.e. `0`),
and `all_flags` is generated as a bitwise-or combination of all other values. In Java these special
options are not generated as one can just use `EnumSet.noneOf()` and `EnumSet.allOf()`.

### Records

```
my_record = record {
    id: i32;
    info: string;
    store: set<string>;
    hash: map<string, i32>;

    values: list<another_record>;

    # Comments can also be put here

    # Constants can be included
    const string_const: string = "Constants can be put here";
    const min_value: another_record = {
        key1 = 0,
        key2 = ""
    };
}
```

Records are data objects. In C++, records contain all their elements by value, including other
records (so a record cannot contain itself).

#### Extensions

To support extra fields and/or methods, a record can be "extended" in any language. To extend a
record in a language, you can add a `+c` (C++), `+j` (Java), `+o` (ObjC), `+p` (Python), or `+s` (C#) flag
after the record tag. The generated type will have a `Base` suffix, and you should create a derived
type without the suffix that extends the record type.

The derived type must be constructible in the same way as the `Base` type. Interfaces will
always use the derived type.

#### Derived methods

```
another_record = record {
    key1: i32;
    key2: string;
} deriving (eq, ord)
```

For record types, Haskell-style "deriving" declarations are supported to generate some common
methods. Djinni is capable of generating equality and order comparators, implemented as operator
overloading in C++ and standard comparison functions in Java, Objective-C, Python and C#.

!!! note

    - All fields in the record are compared in the order they appear in the record declaration.
    If you need to add a field later, make sure the order is correct.
    - Ordering comparison is not supported for collection types, optionals, and booleans.
    - To compare records containing other records, the inner record must derive at least the same
    types of comparators as the outer record.

### Interfaces

```
# This interface will be implemented in C++ and can be called from any language.
my_cpp_interface = interface +c {
    method_returning_nothing(value: i32);
    method_returning_some_type(key: string): another_record;
    static get_version(): i32;

    # Interfaces can also have constants
    const version: i32 = 1;
}

# This interface will be implemented in Java, ObjC, Python and C# and can be called from C++.
my_client_interface = interface +j +o +p +s {
    log_string(str: string): bool;
}
```

Interfaces are objects with defined methods to call (in C++, passed by `shared_ptr`). Djinni
produces code allowing an interface implemented in C++ to be transparently used from ObjC,
Java Python or C# and vice versa.

#### Special Methods for C++ Only
`+c` interfaces (implementable only in C++) can have methods flagged with the special keywords const and static which 
have special effects in C++:

   special_methods = interface +c {
       const accessor_method();
       static factory_method();
   }

- `const` methods will be declared as const in C++, though this cannot be enforced on callers in other languages, which lack this feature.
- `static` methods will become a static method of the C++ class, which can be called from other languages without an object.  
  This is often useful for factory methods to act as a cross-language constructor.

#### Exception Handling
When an interface implemented in C++ throws a `std::exception`, it will be translated to a
`java.lang.RuntimeException` in Java, an `NSException` in Objective-C, a `RuntimeError` in Python, 
or a `System.Exception` in C#.
The `what()` message will be translated as well.

#### Constants
Constants can be defined within interfaces and records. In Java, Python, C# and C++ they are part of the
generated class; and in Objective-C, constant names are globals with the name of the
interface/record prefixed. Example:

```
record_with_const = record +c +j +o +p +s {
    const const_value: i32 = 8;
}
```

will be `RecordWithConst::CONST_VALUE` in C++, `RecordWithConst.CONST_VALUE` in Java,
`RecordWithConst.CONST_VALUE` in Python, `RecordWithConst.ConstValue` in C#, and `RecordWithConstConstValue` in Objective-C.

## Comments

```
# This is a comment
```

If comments are placed on top or inside a type definition, they will be converted to
Javadoc / Doxygen compatible comments in the generated Java, C++, Objective-C and C++/CLI interfaces, or a
Python docstring.
