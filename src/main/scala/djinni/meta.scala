/** Copyright 2014 Dropbox, Inc.
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
  * use this file except in compliance with the License. You may obtain a copy
  * of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  * License for the specific language governing permissions and limitations
  * under the License.
  */

package djinni

import djinni.ast.TypeDef
import scala.collection.immutable

package object meta {

  case class MExpr(base: Meta, args: Seq[MExpr])

  abstract sealed class Meta {
    val numParams: Int
  }

  case class MParam(name: String) extends Meta { val numParams = 0 }
  case class MDef(
      name: String,
      override val numParams: Int,
      defType: DefType,
      body: TypeDef
  ) extends Meta
  case class MExtern(
      name: String,
      override val numParams: Int,
      defType: DefType,
      body: TypeDef,
      cpp: MExtern.Cpp,
      objc: MExtern.Objc,
      objcpp: MExtern.Objcpp,
      java: MExtern.Java,
      jni: MExtern.Jni,
      cs: MExtern.Cs
  ) extends Meta
  object MExtern {
    // These hold the information marshals need to interface with existing types correctly
    // All include paths are complete including quotation marks "a/b/c" or angle brackets <a/b/c>.
    // All typenames are fully qualified in their respective language.
    // TODO: names of enum values and record fields as written in code for use in constants (do not use IdentStyle)
    case class Cpp(
        typename: Option[String],
        header: Option[String],
        byValue: Option[
          Boolean
        ] // Whether to pass struct by value in C++ (e.g. std::chrono::duration). Only used for "record" types.
    )
    case class Objc(
        typename: Option[String],
        header: Option[String],
        boxed: Option[String], // Fully qualified Objective-C typename, must be an object. Only used for "record" types.
        pointer: Option[
          Boolean
        ], // True to construct pointer types and make it eligible for "nonnull" qualifier. Only used for "record" types.
        hash: Option[String] // A well-formed expression to get the hash value. Must be a format string with a single "%s" placeholder. Only used for "record" types with "eq" deriving when needed.
    )
    case class Objcpp(
        translator: Option[
          String
        ], // C++ typename containing toCpp/fromCpp methods
        header: Option[String] // Where to find the translator class
    )
    case class Java(
        typename: Option[String],
        boxed: Option[
          String
        ], // Java typename used if boxing is required, must be an object.
        reference: Option[
          Boolean
        ], // True if the unboxed type is an object reference and qualifies for any kind of "nonnull" annotation in Java. Only used for "record" types.
        generic: Option[
          Boolean
        ], // Set to false to exclude type arguments from the Java class. This is should be true by default. Useful if template arguments are only used in C++.
        hash: Option[String], // A well-formed expression to get the hash value. Must be a format string with a single "%s" placeholder. Only used for "record" types types with "eq" deriving when needed.
        writeToParcel: String, // A well-formed expression to write value into android.os.Parcel. Must be a format string with a single "%s" placeholder. Only used for "record" types types
        readFromParcel: String // A well-formed expression to read value from android.os.Parcel. Must be a format string with a single "%s" placeholder. Only used for "record" types types
    )
    case class Jni(
        translator: Option[
          String
        ], // C++ typename containing toCpp/fromCpp methods
        header: Option[String], // Where to find the translator class
        typename: Option[String], // The JNI type to use (e.g. jobject, jstring)
        typeSignature: Option[
          String
        ] // The mangled Java type signature (e.g. "Ljava/lang/String;")
    )
    case class Cs(
        translator: Option[
          String
        ], // C++ typename containing ToCpp/FromCpp methods
        header: Option[String], // Where to find the translator class
        typename: Option[String],
        reference: Option[Boolean]
    )
  }

  abstract sealed class MOpaque extends Meta { val idlName: String }

  abstract sealed class DefType
  case object DEnum extends DefType
  case object DInterface extends DefType
  case object DRecord extends DefType

  case class MPrimitive(
      _idlName: String,
      jName: String,
      jniName: String,
      cName: String,
      jBoxed: String,
      jSig: String,
      objcName: String,
      objcBoxed: String,
      csName: String,
      cppCliName: String
  ) extends MOpaque { val numParams = 0; val idlName = _idlName }
  case object MString extends MOpaque {
    val numParams = 0; val idlName = "string"
  }
  case object MDate extends MOpaque { val numParams = 0; val idlName = "date" }
  case object MBinary extends MOpaque {
    val numParams = 0; val idlName = "binary"
  }
  case object MOptional extends MOpaque {
    val numParams = 1; val idlName = "optional"
  }
  case object MList extends MOpaque { val numParams = 1; val idlName = "list" }
  case object MSet extends MOpaque { val numParams = 1; val idlName = "set" }
  case object MMap extends MOpaque { val numParams = 2; val idlName = "map" }

  val defaults: Map[String, MOpaque] = immutable.HashMap(
    (
      "i8",
      MPrimitive(
        "i8",
        "byte",
        "jbyte",
        "int8_t",
        "Byte",
        "B",
        "int8_t",
        "NSNumber",
        "sbyte",
        "char"
      )
    ),
    (
      "i16",
      MPrimitive(
        "i16",
        "short",
        "jshort",
        "int16_t",
        "Short",
        "S",
        "int16_t",
        "NSNumber",
        "short",
        "short"
      )
    ),
    (
      "i32",
      MPrimitive(
        "i32",
        "int",
        "jint",
        "int32_t",
        "Integer",
        "I",
        "int32_t",
        "NSNumber",
        "int",
        "int"
      )
    ),
    (
      "i64",
      MPrimitive(
        "i64",
        "long",
        "jlong",
        "int64_t",
        "Long",
        "J",
        "int64_t",
        "NSNumber",
        "long",
        "__int64"
      )
    ),
    (
      "f32",
      MPrimitive(
        "f32",
        "float",
        "jfloat",
        "float",
        "Float",
        "F",
        "float",
        "NSNumber",
        "float",
        "float"
      )
    ),
    (
      "f64",
      MPrimitive(
        "f64",
        "double",
        "jdouble",
        "double",
        "Double",
        "D",
        "double",
        "NSNumber",
        "double",
        "double"
      )
    ),
    (
      "bool",
      MPrimitive(
        "bool",
        "boolean",
        "jboolean",
        "bool",
        "Boolean",
        "Z",
        "BOOL",
        "NSNumber",
        "bool",
        "bool"
      )
    ),
    ("string", MString),
    ("binary", MBinary),
    ("optional", MOptional),
    ("date", MDate),
    ("list", MList),
    ("set", MSet),
    ("map", MMap)
  )

  def isInterface(ty: MExpr): Boolean = {
    ty.base match {
      case d: MDef    => d.defType == DInterface
      case e: MExtern => e.defType == DInterface
      case _          => false
    }
  }

  def isOptionalInterface(ty: MExpr): Boolean = {
    ty.base == MOptional && ty.args.length == 1 && isInterface(ty.args.head)
  }
}
