# Interface Definition Language

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

# This interface will be implemented in Java and ObjC and can be called from C++.
my_client_interface = interface +j +o {
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

The available data types for a record, argument, or return value are:

 - Boolean (`bool`)
 - Primitives (`i8`, `i16`, `i32`, `i64`, `f32`, `f64`).
 - Strings (`string`)
 - Binary (`binary`). This is implemented as `std::vector<uint8_t>` in C++, `byte[]` in Java,
   and `NSData` in Objective-C.
 - Date (`date`).  This is `chrono::system_clock::time_point` in C++, `Date` in Java, and
   `NSDate` in Objective-C.
 - List (`list<type>`). This is `vector<T>` in C++, `ArrayList` in Java, and `NSArray`
   in Objective-C. Primitives in a list will be boxed in Java and Objective-C.
 - Set (`set<type>`). This is `unordered_set<T>` in C++, `HashSet` in Java, and `NSSet` in
   Objective-C. Primitives in a set will be boxed in Java and Objective-C.
 - Map (`map<typeA, typeB>`). This is `unordered_map<K, V>` in C++, `HashMap` in Java, and
   `NSDictionary` in Objective-C. Primitives in a map will be boxed in Java and Objective-C.
 - Enumerations / Flags
 - Optionals (`optional<typeA>`). This is `std::experimental::optional<T>` in C++11, object /
   boxed primitive reference in Java (which can be `null`), and object / NSNumber strong
   reference in Objective-C (which can be `nil`).
 - Other record types. This is generated with a by-value semantic, i.e. the copy method will
   deep-copy the contents.

## Types
An IDL file can contain 4 kinds of declarations: enums, flags, records, and interfaces.

* [**Enums**](#enums) become C++ enum classes, Java enums, or ObjC `NS_ENUM`s.
* [**Flags**](#flags) become C++ enum classes with convenient bit-oriented operators, Java enums with `EnumSet`, or ObjC `NS_OPTIONS`.
* [**Records**](#records) are pure-data value objects.
* [**Interfaces**](#interfaces) are objects with defined methods to call (in C++, passed by `shared_ptr`). Djinni
  produces code allowing an interface implemented in C++ to be transparently used from ObjC or
  Java, and vice versa.

### Enums

```
my_enum = enum {
    option1;
    option2;
    option3;
}
```

Enums are translated to C++ `enum class`es with underlying type `int`, ObjC `NS_ENUM`s with
underlying type `NSInteger`, and Java enums.

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

Flags are translated to C++ `enum class`es with underlying type `unsigned` and a generated set
of overloaded bitwise operators for convenience, ObjC `NS_OPTIONS` with underlying type
`NSUInteger`, and Java `EnumSet<>`. Contrary to the above enums, the enumerants of flags represent
single bits instead of integral values.

In the above example the elements marked with `none` and `all` are given special meaning.
In C++ and ObjC the `no_flags` option is generated with a value that has no bits set (i.e. `0`),
and `all_flags` is generated as a bitwise-or combination of all other values. In Java these
special options are not generated as one can just use `EnumSet.noneOf()` and `EnumSet.allOf()`.

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

To support extra fields and/or methods, a record can be "extended" in any language. To extend
a record in a language, you can add a `+c` (C++), `+j` (Java), or `+o` (ObjC) flag after the
record tag. The generated type will have a `Base` suffix, and you should create a derived type
without the suffix that extends the record type.

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
methods. Djinni is capable of generating equality and order comparators, implemented
as operator overloading in C++ and standard comparison functions in Java / Objective-C.

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

# This interface will be implemented in Java and ObjC and can be called from C++.
my_client_interface = interface +j +o {
    log_string(str: string): bool;
}
```

Interfaces are objects with defined methods to call (in C++, passed by `shared_ptr`). Djinni
produces code allowing an interface implemented in C++ to be transparently used from ObjC or
Java, and vice versa.

#### Special Methods for C++ Only
`+c` interfaces (implementable only in C++) can have methods flagged with the special keywords const and static which have special effects in C++:

   special_methods = interface +c {
       const accessor_method();
       static factory_method();
   }
   
- `const` methods will be declared as const in C++, though this cannot be enforced on callers in other languages, which lack this feature.
- `static` methods will become a static method of the C++ class, which can be called from other languages without an object.  This is often useful for factory methods to act as a cross-language constructor.

#### Exception Handling
When an interface implemented in C++ throws a `std::exception`, it will be translated to a
`java.lang.RuntimeException` in Java or an `NSException` in Objective-C. The `what()` message
will be translated as well.

#### Constants
Constants can be defined within interfaces and records. In Java and C++ they are part of the
generated class; and in Objective-C, constant names are globals with the name of the
interface/record prefixed. Example:

```
record_with_const = record +c +j +o {
    const const_value: i32 = 8;
}
```

will be `RecordWithConst::CONST_VALUE` in C++, `RecordWithConst.CONST_VALUE` in Java, and
`RecordWithConstConstValue` in Objective-C.