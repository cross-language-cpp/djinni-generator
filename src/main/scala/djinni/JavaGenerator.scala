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

import djinni.ast.Interface.RequiresType
import djinni.ast.Record.DerivingType
import djinni.ast._
import djinni.generatorTools._
import djinni.meta._
import djinni.writer.IndentWriter

import scala.collection.mutable

class JavaGenerator(spec: Spec) extends Generator(spec) {

  val javaAnnotationHeader: Option[String] =
    spec.javaAnnotation.map(pkg => '@' + pkg.split("\\.").last)
  val javaNullableAnnotation: Option[String] =
    spec.javaNullableAnnotation.map(pkg => '@' + pkg.split("\\.").last)
  val javaNonnullAnnotation: Option[String] =
    spec.javaNonnullAnnotation.map(pkg => '@' + pkg.split("\\.").last)
  val javaClassAccessModifierString: String =
    JavaAccessModifier.getCodeGenerationString(spec.javaClassAccessModifier)
  val marshal = new JavaMarshal(spec)

  class JavaRefs() {
    var java: mutable.TreeSet[String] = mutable.TreeSet[String]()

    spec.javaAnnotation.foreach(pkg => java.add(pkg))
    spec.javaNullableAnnotation.foreach(pkg => java.add(pkg))
    spec.javaNonnullAnnotation.foreach(pkg => java.add(pkg))

    def find(ty: TypeRef): Unit = { find(ty.resolved) }
    def find(tm: MExpr): Unit = {
      tm.args.foreach(find)
      find(tm.base)
    }
    def find(m: Meta): Unit = for (r <- marshal.references(m)) r match {
      case ImportRef(arg) => java.add(arg)
      case _              =>
    }
  }

  def writeJavaFile(
      ident: String,
      origin: String,
      refs: Iterable[String],
      f: IndentWriter => Unit
  ): Unit = {
    createFile(
      spec.javaOutFolder.get,
      idJava.ty(ident) + ".java",
      (w: IndentWriter) => {
        w.wl("// AUTOGENERATED FILE - DO NOT MODIFY!")
        w.wl("// This file was generated by Djinni from " + origin)
        w.wl
        spec.javaPackage.foreach(s => w.wl(s"package $s;").wl)
        if (refs.nonEmpty) {
          refs.foreach(s => w.wl(s"import $s;"))
          w.wl
        }
        f(w)
      }
    )
  }

  def writeDocAnnotations(w: IndentWriter, doc: Doc): Unit = {
    writeDeprecated(w, doc, "@Deprecated")
  }

  def generateJavaConstants(
      w: IndentWriter,
      consts: Seq[Const],
      forJavaInterface: Boolean
  ): Unit = {

    def writeJavaConst(w: IndentWriter, ty: TypeRef, v: Any): Unit = v match {
      case l: Long if marshal.fieldType(ty).equalsIgnoreCase("long") =>
        w.w(l.toString + "l")
      case l: Long => w.w(l.toString)
      case d: Double if marshal.fieldType(ty).equalsIgnoreCase("float") =>
        w.w(d.toString + "f")
      case d: Double    => w.w(d.toString)
      case b: Boolean   => w.w(if (b) "true" else "false")
      case s: String    => w.w(s)
      case e: EnumValue => w.w(s"${marshal.typename(ty)}.${idJava.enum(e)}")
      case v: ConstRef  => w.w(idJava.const(v))
      case z: Map[_, _] => { // Value is record
        val recordMdef = ty.resolved.base.asInstanceOf[MDef]
        val record = recordMdef.body.asInstanceOf[Record]
        val vMap = z.asInstanceOf[Map[String, Any]]
        w.wl(s"new ${marshal.typename(ty)}(")
        w.increase()
        // Use exact sequence
        val skipFirst = SkipFirst()
        for (f <- record.fields) {
          skipFirst { w.wl(",") }
          writeJavaConst(w, f.ty, vMap.apply(f.ident.name))
          w.w(" /* " + idJava.field(f.ident) + " */ ")
        }
        w.w(")")
        w.decrease()
      }
    }

    for (c <- consts) {
      writeDoc(w, c.doc)
      writeDocAnnotations(w, c.doc)
      javaAnnotationHeader.foreach(w.wl)
      marshal.nullityAnnotation(c.ty).foreach(w.wl)

      // If the constants are part of a Java interface, omit the "public," "static,"
      // and "final" specifiers.
      val publicStaticFinalString =
        if (forJavaInterface) "" else "public static final "
      w.w(
        s"$publicStaticFinalString${marshal.fieldType(c.ty)} ${idJava.const(c.ident)} = "
      )
      writeJavaConst(w, c.ty, c.value)
      w.wl(";")
      w.wl
    }
  }

  override def generateEnum(
      origin: String,
      ident: Ident,
      doc: Doc,
      e: Enum
  ): Unit = {
    val refs = new JavaRefs()

    writeJavaFile(
      ident,
      origin,
      refs.java,
      w => {
        writeDoc(w, doc)
        writeDocAnnotations(w, doc)
        javaAnnotationHeader.foreach(w.wl)
        w.w(
          s"${javaClassAccessModifierString}enum ${marshal.typename(ident, e)}"
        ).braced {
          for (o <- normalEnumOptions(e)) {
            writeDoc(w, o.doc)
            writeDocAnnotations(w, o.doc)
            w.wl(idJava.enum(o.ident) + ",")
          }
          w.wl(";")
        }
      }
    )
  }

  override def generateInterface(
      origin: String,
      ident: Ident,
      doc: Doc,
      typeParams: Seq[TypeParam],
      i: Interface
  ): Unit = {
    val refs = new JavaRefs()

    i.methods.map(m => {
      m.params.map(p => refs.find(p.ty))
      m.ret.foreach(refs.find)
    })
    i.consts.map(c => {
      refs.find(c.ty)
    })
    if (i.ext.cpp) {
      refs.java.add("java.util.concurrent.atomic.AtomicBoolean")
    }

    writeJavaFile(
      ident,
      origin,
      refs.java,
      w => {
        val javaClass = marshal.typename(ident, i)
        val typeParamList = javaTypeParams(typeParams)
        writeDoc(w, doc)
        writeDocAnnotations(w, doc)

        javaAnnotationHeader.foreach(w.wl)

        val interfaces = scala.collection.mutable.ArrayBuffer[String]()
        if (i.requiresTypes.contains(RequiresType.Ord))
          interfaces += s"Comparable<$javaClass>"
        val implementsSection =
          if (interfaces.isEmpty) ""
          else " implements " + interfaces.mkString(", ")

        // Generate an interface or an abstract class depending on whether the use
        // of Java interfaces was requested.
        val classPrefix =
          if (spec.javaGenerateInterfaces) "interface" else "abstract class"
        val methodPrefix = if (spec.javaGenerateInterfaces) "" else "abstract "
        val extendsKeyword =
          if (spec.javaGenerateInterfaces) "implements" else "extends"
        val innerClassAccessibility =
          if (spec.javaGenerateInterfaces) "" else "private "
        w.w(
          s"${javaClassAccessModifierString}$classPrefix $javaClass$typeParamList$implementsSection"
        ).braced {
          val skipFirst = SkipFirst()
          generateJavaConstants(w, i.consts, spec.javaGenerateInterfaces)

          val throwException = spec.javaCppException.fold("")(" throws " + _)
          for (m <- i.methods if !m.static) {
            skipFirst { w.wl }
            writeMethodDoc(w, m, idJava.local)
            writeDocAnnotations(w, m.doc)
            val ret = marshal.returnType(m.ret)
            val params = m.params.map(p => {
              val nullityAnnotation =
                marshal.nullityAnnotation(p.ty).map(_ + " ").getOrElse("")
              nullityAnnotation + marshal.paramType(p.ty) + " " + idJava
                .local(p.ident)
            })
            marshal.nullityAnnotation(m.ret).foreach(w.wl)
            w.wl(
              s"public $methodPrefix" + ret + " " + idJava.method(
                m.ident
              ) + params.mkString("(", ", ", ")") + throwException + ";"
            )
          }

          // Implement the interface's static methods as calls to CppProxy's corresponding methods.
          for (m <- i.methods if m.static) {
            skipFirst { w.wl }
            writeMethodDoc(w, m, idJava.local)
            writeDocAnnotations(w, m.doc)
            val ret = marshal.returnType(m.ret)
            val returnPrefix = if (ret == "void") "" else "return "
            val params = m.params.map(p => {
              val nullityAnnotation =
                marshal.nullityAnnotation(p.ty).map(_ + " ").getOrElse("")
              nullityAnnotation + marshal.paramType(p.ty) + " " + idJava
                .local(p.ident)
            })

            val meth = idJava.method(m.ident)
            marshal.nullityAnnotation(m.ret).foreach(w.wl)
            w.wl(
              "public static " + ret + " " + idJava
                .method(m.ident) + params.mkString("(", ", ", ")")
            ).braced {
              writeAlignedCall(
                w,
                s"${returnPrefix}CppProxy.${meth}(",
                m.params,
                ");",
                p => idJava.local(p.ident)
              )
              w.wl
            }
          }

          if (i.ext.cpp) {
            w.wl
            javaAnnotationHeader.foreach(w.wl)
            w.wl(
              s"${innerClassAccessibility}static final class CppProxy$typeParamList $extendsKeyword $javaClass$typeParamList"
            ).braced {
              w.wl("private final long nativeRef;")
              w.wl(
                "private final AtomicBoolean destroyed = new AtomicBoolean(false);"
              )
              w.wl
              w.wl(s"private CppProxy(long nativeRef)").braced {
                w.wl(
                  "if (nativeRef == 0) throw new RuntimeException(\"nativeRef is zero\");"
                )
                w.wl(s"this.nativeRef = nativeRef;")
              }
              w.wl
              w.wl("private native void nativeDestroy(long nativeRef);")
              w.wl("public void _djinni_private_destroy()").braced {
                w.wl("boolean destroyed = this.destroyed.getAndSet(true);")
                w.wl("if (!destroyed) nativeDestroy(this.nativeRef);")
              }
              w.wl("@SuppressWarnings(\"deprecation\")")
              w.wl("protected void finalize() throws java.lang.Throwable")
                .braced {
                  w.wl("_djinni_private_destroy();")
                  w.wl("super.finalize();")
                }

              // Implement the interface's non-static methods.
              for (m <- i.methods if !m.static) {
                val ret = marshal.returnType(m.ret)
                val returnStmt = m.ret.fold("")(_ => "return ")
                val params = m.params
                  .map(p =>
                    marshal.paramType(p.ty) + " " + idJava.local(p.ident)
                  )
                  .mkString(", ")
                val args =
                  m.params.map(p => idJava.local(p.ident)).mkString(", ")
                val meth = idJava.method(m.ident)
                w.wl
                w.wl("@Override")
                w.wl(s"public $ret $meth($params)$throwException").braced {
                  w.wl(
                    "assert !this.destroyed.get() : \"trying to use a destroyed object\";"
                  )
                  w.wl(
                    s"${returnStmt}native_$meth(this.nativeRef${preComma(args)});"
                  )
                }
                w.wl(
                  s"private native $ret native_$meth(long _nativeRef${preComma(params)});"
                )
              }

              if (i.requiresTypes.contains(RequiresType.Eq)) {
                // equals() override
                w.wl
                w.wl("@Override")
                val nullableAnnotation =
                  javaNullableAnnotation.map(_ + " ").getOrElse("")
                w.w(s"public boolean equals(${nullableAnnotation}Object obj)")
                  .braced {
                    w.wl(
                      "assert !this.destroyed.get() : \"trying to use a destroyed object\";"
                    )
                    w.wl
                    w.w(s"if (!(obj instanceof $javaClass))").braced {
                      w.wl("return false;")
                    }
                    w.wl
                    w.wl(
                      s"return native_operator_equals(this.nativeRef, ($javaClass)obj);"
                    )
                  }
                w.wl(
                  s"private native boolean native_operator_equals(long _nativeRef, $javaClass other);"
                )

                // hashCode() override
                w.wl
                w.wl("@Override")
                w.w("public int hashCode()").braced {
                  w.wl(
                    "assert !this.destroyed.get() : \"trying to use a destroyed object\";"
                  )
                  w.wl(
                    s"return native_hash_code(this.nativeRef);"
                  )
                }
                w.wl(
                  s"private native int native_hash_code(long _nativeRef);"
                )
              }

              // Declare a native method for each of the interface's static methods.
              for (m <- i.methods if m.static) {
                skipFirst { w.wl }
                val ret = marshal.returnType(m.ret)
                val params = m.params.map(p => {
                  val nullityAnnotation =
                    marshal.nullityAnnotation(p.ty).map(_ + " ").getOrElse("")
                  nullityAnnotation + marshal.paramType(p.ty) + " " + idJava
                    .local(p.ident)
                })
                marshal.nullityAnnotation(m.ret).foreach(w.wl)
                w.wl(
                  "public static native " + ret + " " + idJava
                    .method(m.ident) + params.mkString("(", ", ", ")") + ";"
                )
              }
            }
          }
        }
      }
    )
  }

  override def generateRecord(
      origin: String,
      ident: Ident,
      doc: Doc,
      params: Seq[TypeParam],
      r: Record
  ): Unit = {
    val refs = new JavaRefs()
    r.fields.foreach(f => refs.find(f.ty))

    val javaName = if (r.ext.java) (ident.name + "_base") else ident.name
    val javaFinal =
      if (!r.ext.java && spec.javaUseFinalForRecord) "final " else ""

    def recordContainsSets(r: Record): Boolean = {
      for (f <- r.fields) {
        f.ty.resolved.base match {
          case MSet => return true
          case MOptional =>
            f.ty.resolved.args.head.base match {
              case MSet => return true
              case _    =>
            }
          case _ =>
        }
      }
      return false
    }

    def recordContainsLists(r: Record): Boolean = {
      for (f <- r.fields) {
        f.ty.resolved.base match {
          case MList => return true
          case MOptional =>
            f.ty.resolved.args.head.base match {
              case MList => return true
              case _     =>
            }
          case _ =>
        }
      }
      return false
    }

    if (
      spec.javaImplementAndroidOsParcelable && r.derivingTypes.contains(
        DerivingType.AndroidParcelable
      )
      && recordContainsSets(r) && !recordContainsLists(r)
    ) {
      // If the record is parcelable, doesn't contain any List but it contains a Set,
      // we need to manually import 'java.util.ArrayList'
      // because it's used by the parcelable
      // check https://github.com/dropbox/djinni/issues/408 for more info
      refs.java += "java.util.ArrayList"
    }

    writeJavaFile(
      javaName,
      origin,
      refs.java,
      w => {
        writeDoc(w, doc)
        writeDocAnnotations(w, doc)
        javaAnnotationHeader.foreach(w.wl)
        val self = marshal.typename(javaName, r)

        val interfaces = scala.collection.mutable.ArrayBuffer[String]()
        if (r.derivingTypes.contains(DerivingType.Ord))
          interfaces += s"Comparable<$self>"
        if (
          spec.javaImplementAndroidOsParcelable && r.derivingTypes
            .contains(DerivingType.AndroidParcelable)
        )
          interfaces += "android.os.Parcelable"
        val implementsSection =
          if (interfaces.isEmpty) ""
          else " implements " + interfaces.mkString(", ")
        w.w(
          s"${javaClassAccessModifierString}${javaFinal}class ${self + javaTypeParams(params)}$implementsSection"
        ).braced {
          w.wl
          generateJavaConstants(w, r.consts, false)
          // Field definitions.
          for (f <- r.fields) {
            w.wl
            w.wl(
              s"/*package*/ final ${marshal.fieldType(f.ty)} ${idJava.field(f.ident)};"
            )
          }

          // Constructor.
          w.wl
          w.wl(s"public $self(").nestedN(2) {
            val skipFirst = SkipFirst()
            for (f <- r.fields) {
              skipFirst { w.wl(",") }
              marshal
                .nullityAnnotation(f.ty)
                .map(annotation => w.w(annotation + " "))
              w.w(marshal.paramType(f.ty) + " " + idJava.local(f.ident))
            }
            w.wl(") {")
          }
          w.nested {
            for (f <- r.fields) {
              w.wl(s"this.${idJava.field(f.ident)} = ${idJava.local(f.ident)};")
            }
          }
          w.wl("}")

          // Accessors
          for (f <- r.fields) {
            w.wl
            writeDoc(w, f.doc)
            writeDocAnnotations(w, f.doc)
            marshal.nullityAnnotation(f.ty).foreach(w.wl)
            w.w(
              "public " + marshal.returnType(Some(f.ty)) + " " + idJava
                .method("get_" + f.ident.name) + "()"
            ).braced {
              w.wl("return " + idJava.field(f.ident) + ";")
            }
          }

          if (r.derivingTypes.contains(DerivingType.Eq)) {
            w.wl
            w.wl("@Override")
            val nullableAnnotation =
              javaNullableAnnotation.map(_ + " ").getOrElse("")
            w.w(s"public boolean equals(${nullableAnnotation}Object obj)")
              .braced {
                w.w(s"if (!(obj instanceof $self))").braced {
                  w.wl("return false;")
                }
                w.wl(s"$self other = ($self) obj;")
                w.w(s"return ").nestedN(2) {
                  val skipFirst = SkipFirst()
                  for (f <- r.fields) {
                    skipFirst { w.wl(" &&") }
                    f.ty.resolved.base match {
                      case MBinary =>
                        w.w(s"java.util.Arrays.equals(${idJava
                            .field(f.ident)}, other.${idJava.field(f.ident)})")
                      case MList | MSet | MMap | MString | MDate =>
                        w.w(
                          s"this.${idJava.field(f.ident)}.equals(other.${idJava
                              .field(f.ident)})"
                        )
                      case MOptional =>
                        w.w(
                          s"((this.${idJava.field(f.ident)} == null && other.${idJava
                              .field(f.ident)} == null) || "
                        )
                        w.w(s"(this.${idJava.field(f.ident)} != null && this.${idJava
                            .field(f.ident)}.equals(other.${idJava.field(f.ident)})))")
                      case _: MPrimitive =>
                        w.w(s"this.${idJava.field(f.ident)} == other.${idJava
                            .field(f.ident)}")
                      case df: MDef =>
                        df.defType match {
                          case DRecord =>
                            w.w(s"this.${idJava.field(f.ident)}.equals(other.${idJava
                                .field(f.ident)})")
                          case DEnum =>
                            w.w(
                              s"this.${idJava.field(f.ident)} == other.${idJava
                                  .field(f.ident)}"
                            )
                          case _ => throw new AssertionError("Unreachable")
                        }
                      case e: MExtern =>
                        e.defType match {
                          case DRecord =>
                            if (e.java.reference.get) {
                              w.w(
                                s"this.${idJava.field(f.ident)}.equals(other.${idJava
                                    .field(f.ident)})"
                              )
                            } else {
                              w.w(s"this.${idJava.field(f.ident)} == other.${idJava
                                  .field(f.ident)}")
                            }
                          case DEnum =>
                            w.w(
                              s"this.${idJava.field(f.ident)} == other.${idJava
                                  .field(f.ident)}"
                            )
                          case _ => throw new AssertionError("Unreachable")
                        }
                      case _ => throw new AssertionError("Unreachable")
                    }
                  }
                }
                w.wl(";")
              }
            // Also generate a hashCode function, since you shouldn't override one without the other.
            // This hashcode implementation is based off of the apache commons-lang implementation of
            // HashCodeBuilder (excluding support for Java arrays) which is in turn based off of the
            // the recommendataions made in Effective Java.
            w.wl
            w.wl("@Override")
            w.w("public int hashCode()").braced {
              w.wl("// Pick an arbitrary non-zero starting value")
              w.wl("int hashCode = 17;")
              // Also pick an arbitrary prime to use as the multiplier.
              val multiplier = "31"
              for (f <- r.fields) {
                val fieldHashCode = f.ty.resolved.base match {
                  case MBinary =>
                    s"java.util.Arrays.hashCode(${idJava.field(f.ident)})"
                  case MList | MSet | MMap | MString | MDate =>
                    s"${idJava.field(f.ident)}.hashCode()"
                  // Need to repeat this case for MDef
                  case _: MDef => s"${idJava.field(f.ident)}.hashCode()"
                  case MOptional =>
                    s"(${idJava.field(f.ident)} == null ? 0 : ${idJava.field(f.ident)}.hashCode())"
                  case t: MPrimitive =>
                    t.jName match {
                      case "byte" | "short" | "int" => idJava.field(f.ident)
                      case "long" =>
                        s"((int) (${idJava.field(f.ident)} ^ (${idJava.field(f.ident)} >>> 32)))"
                      case "float" =>
                        s"Float.floatToIntBits(${idJava.field(f.ident)})"
                      case "double" =>
                        s"((int) (Double.doubleToLongBits(${idJava.field(f.ident)}) ^ (Double.doubleToLongBits(${idJava
                            .field(f.ident)}) >>> 32)))"
                      case "boolean" => s"(${idJava.field(f.ident)} ? 1 : 0)"
                      case _         => throw new AssertionError("Unreachable")
                    }
                  case e: MExtern =>
                    e.defType match {
                      case DRecord =>
                        "(" + e.java.hash.get
                          .format(idJava.field(f.ident)) + ")"
                      case DEnum => s"${idJava.field(f.ident)}.hashCode()"
                      case _     => throw new AssertionError("Unreachable")
                    }
                  case _ => throw new AssertionError("Unreachable")
                }
                w.wl(s"hashCode = hashCode * $multiplier + $fieldHashCode;")
              }
              w.wl(s"return hashCode;")
            }

          }

          w.wl
          w.wl("@Override")
          w.w("public String toString()").braced {
            w.w(s"return ").nestedN(2) {
              w.wl(s""""${self}{" +""")
              for (i <- 0 to r.fields.length - 1) {
                val name = idJava.field(r.fields(i).ident)
                val comma = if (i > 0) """"," + """ else ""
                w.wl(s"""${comma}"${name}=" + ${name} +""")
              }
            }
            w.wl(s""""}";""")
          }
          w.wl

          if (
            spec.javaImplementAndroidOsParcelable && r.derivingTypes
              .contains(DerivingType.AndroidParcelable)
          )
            writeParcelable(w, self, r)

          if (r.derivingTypes.contains(DerivingType.Ord)) {
            def primitiveCompare(ident: Ident): Unit = {
              w.wl(
                s"if (this.${idJava.field(ident)} < other.${idJava.field(ident)}) {"
              ).nested {
                w.wl(s"tempResult = -1;")
              }
              w.wl(
                s"} else if (this.${idJava.field(ident)} > other.${idJava.field(ident)}) {"
              ).nested {
                w.wl(s"tempResult = 1;")
              }
              w.wl(s"} else {").nested {
                w.wl(s"tempResult = 0;")
              }
              w.wl("}")
            }
            w.wl
            w.wl("@Override")
            val nonnullAnnotation =
              javaNonnullAnnotation.map(_ + " ").getOrElse("")
            w.w(s"public int compareTo($nonnullAnnotation$self other) ")
              .braced {
                w.wl("int tempResult;")
                for (f <- r.fields) {
                  f.ty.resolved.base match {
                    case MString | MDate =>
                      w.wl(s"tempResult = this.${idJava.field(f.ident)}.compareTo(other.${idJava
                          .field(f.ident)});")
                    case _: MPrimitive => primitiveCompare(f.ident)
                    case df: MDef =>
                      df.defType match {
                        case DRecord =>
                          w.wl(s"tempResult = this.${idJava.field(f.ident)}.compareTo(other.${idJava
                              .field(f.ident)});")
                        case DEnum =>
                          w.w(s"tempResult = this.${idJava.field(f.ident)}.compareTo(other.${idJava
                              .field(f.ident)});")
                        case _ => throw new AssertionError("Unreachable")
                      }
                    case e: MExtern =>
                      e.defType match {
                        case DRecord =>
                          if (e.java.reference.get)
                            w.wl(s"tempResult = this.${idJava.field(f.ident)}.compareTo(other.${idJava
                                .field(f.ident)});")
                          else primitiveCompare(f.ident)
                        case DEnum =>
                          w.w(s"tempResult = this.${idJava.field(f.ident)}.compareTo(other.${idJava
                              .field(f.ident)});")
                        case _ => throw new AssertionError("Unreachable")
                      }
                    case _ => throw new AssertionError("Unreachable")
                  }
                  w.w("if (tempResult != 0)").braced {
                    w.wl("return tempResult;")
                  }
                }
                w.wl("return 0;")
              }
          }

        }
      }
    )
  }

  def javaTypeParams(params: Seq[TypeParam]): String =
    if (params.isEmpty) ""
    else params.map(p => idJava.typeParam(p.ident)).mkString("<", ", ", ">")

  def writeParcelable(
      w: IndentWriter,
      self: String,
      r: Record
  ): IndentWriter = {
    // Generates the methods and the constructor to implement the interface android.os.Parcelable

    // CREATOR
    w.wl
    w.wl(s"public static final android.os.Parcelable.Creator<$self> CREATOR")
    w.w(s"    = new android.os.Parcelable.Creator<$self>()").bracedSemi {
      w.wl("@Override")
      w.w(s"public $self createFromParcel(android.os.Parcel in)").braced {
        w.wl(s"return new $self(in);")
      }
      w.wl
      w.wl("@Override")
      w.w(s"public $self[] newArray(int size)").braced {
        w.wl(s"return new $self[size];")
      }
    }

    // constructor (Parcel)
    def deserializeField(f: Field, m: Meta, inOptional: Boolean): Unit = {
      m match {
        case MString =>
          w.wl(s"this.${idJava.field(f.ident)} = in.readString();")
        case MBinary => {
          w.wl(s"this.${idJava.field(f.ident)} = in.createByteArray();")
        }
        case MDate =>
          w.wl(
            s"this.${idJava.field(f.ident)} = new ${marshal.typename(f.ty)}(in.readLong());"
          )
        case t: MPrimitive =>
          t.jName match {
            case "short" =>
              w.wl(s"this.${idJava.field(f.ident)} = (short)in.readInt();")
            case "int" => w.wl(s"this.${idJava.field(f.ident)} = in.readInt();")
            case "long" =>
              w.wl(s"this.${idJava.field(f.ident)} = in.readLong();")
            case "byte" =>
              w.wl(s"this.${idJava.field(f.ident)} = in.readByte();")
            case "boolean" =>
              w.wl(s"this.${idJava.field(f.ident)} = in.readByte() != 0;")
            case "float" =>
              w.wl(s"this.${idJava.field(f.ident)} = in.readFloat();")
            case "double" =>
              w.wl(s"this.${idJava.field(f.ident)} = in.readDouble();")
            case _ => throw new AssertionError("Unreachable")
          }
        case df: MDef =>
          df.defType match {
            case DRecord =>
              w.wl(
                s"this.${idJava.field(f.ident)} = new ${marshal.typename(f.ty)}(in);"
              )
            case DEnum => {
              if (marshal.isEnumFlags(m)) {
                w.wl(
                  s"this.${idJava.field(f.ident)} = (EnumSet<${marshal.typename(f.ty)}>) in.readSerializable();"
                )
              } else {
                w.wl(
                  s"this.${idJava.field(f.ident)} = ${marshal.typename(f.ty)}.values()[in.readInt()];"
                )
              }
            }
            case _ => throw new AssertionError("Unreachable")
          }
        case e: MExtern =>
          e.defType match {
            case DRecord =>
              w.wl(s"this.${idJava.field(f.ident)} = ${e.java.readFromParcel
                  .format(marshal.typename(f.ty))};")
            case DEnum => {
              if (marshal.isEnumFlags(m)) {
                w.wl(
                  s"this.${idJava.field(f.ident)} = (EnumSet<${marshal.typename(f.ty)}>) in.readSerializable();"
                )
              } else {
                w.wl(
                  s"this.${idJava.field(f.ident)} = ${marshal.typename(f.ty)}.values()[in.readInt()];"
                )
              }
            }
            case _ => throw new AssertionError("Unreachable")
          }
        case MList => {
          w.wl(
            s"this.${idJava.field(f.ident)} = new ${marshal.typename(f.ty)}();"
          )
          w.wl(
            s"in.readList(this.${idJava.field(f.ident)}, getClass().getClassLoader());"
          )
        }
        case MSet => {
          val collectionTypeName =
            marshal.typename(f.ty).replaceFirst("HashSet<(.*)>", "$1")
          w.wl(
            s"ArrayList<${collectionTypeName}> ${idJava.field(f.ident)}Temp = new ArrayList<${collectionTypeName}>();"
          )
          w.wl(
            s"in.readList(${idJava.field(f.ident)}Temp, getClass().getClassLoader());"
          )
          w.wl(s"this.${idJava.field(f.ident)} = new ${marshal
              .typename(f.ty)}(${idJava.field(f.ident)}Temp);")
        }
        case MMap => {
          w.wl(
            s"this.${idJava.field(f.ident)} = new ${marshal.typename(f.ty)}();"
          )
          w.wl(
            s"in.readMap(this.${idJava.field(f.ident)}, getClass().getClassLoader());"
          )
        }
        case MOptional => {
          if (inOptional)
            throw new AssertionError("nested optional?")
          w.wl("if (in.readByte() == 0) {").nested {
            w.wl(s"this.${idJava.field(f.ident)} = null;")
          }
          w.wl("} else {").nested {
            deserializeField(f, f.ty.resolved.args.head.base, inOptional = true)
          }
          w.wl("}")
        }
        case _ => throw new AssertionError("Unreachable")
      }
    }
    w.wl
    w.w(s"public $self(android.os.Parcel in)").braced {
      for (f <- r.fields)
        deserializeField(f, f.ty.resolved.base, inOptional = false)
    }

    // describeContents
    w.wl
    w.wl("@Override")
    w.w("public int describeContents()").braced {
      w.wl("return 0;")
    }

    // writeToParcel
    def serializeField(f: Field, m: Meta, inOptional: Boolean): Unit = {
      m match {
        case MString => w.wl(s"out.writeString(this.${idJava.field(f.ident)});")
        case MBinary => {
          w.wl(s"out.writeByteArray(this.${idJava.field(f.ident)});")
        }
        case MDate =>
          w.wl(s"out.writeLong(this.${idJava.field(f.ident)}.getTime());")
        case t: MPrimitive =>
          t.jName match {
            case "short" | "int" =>
              w.wl(s"out.writeInt(this.${idJava.field(f.ident)});")
            case "long" =>
              w.wl(s"out.writeLong(this.${idJava.field(f.ident)});")
            case "byte" =>
              w.wl(s"out.writeByte(this.${idJava.field(f.ident)});")
            case "boolean" =>
              w.wl(
                s"out.writeByte(this.${idJava.field(f.ident)} ? (byte)1 : 0);"
              )
            case "float" =>
              w.wl(s"out.writeFloat(this.${idJava.field(f.ident)});")
            case "double" =>
              w.wl(s"out.writeDouble(this.${idJava.field(f.ident)});")
            case _ => throw new AssertionError("Unreachable")
          }
        case df: MDef =>
          df.defType match {
            case DRecord =>
              w.wl(s"this.${idJava.field(f.ident)}.writeToParcel(out, flags);")
            case DEnum => {
              if (marshal.isEnumFlags(m)) {
                w.wl(s"out.writeSerializable(this.${idJava.field(f.ident)});")
              } else {
                w.wl(s"out.writeInt(this.${idJava.field(f.ident)}.ordinal());")
              }
            }
            case _ => throw new AssertionError("Unreachable")
          }
        case e: MExtern =>
          e.defType match {
            case DRecord =>
              w.wl(e.java.writeToParcel.format(idJava.field(f.ident)) + ";")
            case DEnum => {
              if (marshal.isEnumFlags(m)) {
                w.wl(s"out.writeSerializable(this.${idJava.field(f.ident)});")
              } else {
                w.wl(s"out.writeInt(this.${idJava.field(f.ident)}.ordinal());")
              }
            }
            case _ => throw new AssertionError("Unreachable")
          }
        case MList => {
          w.wl(s"out.writeList(this.${idJava.field(f.ident)});")
        }
        case MSet => {
          val collectionTypeName =
            marshal.typename(f.ty).replaceFirst("HashSet<(.*)>", "$1")
          w.wl(
            s"out.writeList(new ArrayList<${collectionTypeName}>(this.${idJava
                .field(f.ident)}));"
          )
        }
        case MMap => w.wl(s"out.writeMap(this.${idJava.field(f.ident)});")
        case MOptional => {
          if (inOptional)
            throw new AssertionError("nested optional?")
          w.wl(s"if (this.${idJava.field(f.ident)} != null) {").nested {
            w.wl("out.writeByte((byte)1);")
            serializeField(f, f.ty.resolved.args.head.base, inOptional = true)
          }
          w.wl("} else {").nested {
            w.wl("out.writeByte((byte)0);")
          }
          w.wl("}")
        }
        case _ => throw new AssertionError("Unreachable")
      }
    }

    w.wl
    w.wl("@Override")
    w.w("public void writeToParcel(android.os.Parcel out, int flags)").braced {
      for (f <- r.fields)
        serializeField(f, f.ty.resolved.base, false)
    }
    w.wl
  }

}
