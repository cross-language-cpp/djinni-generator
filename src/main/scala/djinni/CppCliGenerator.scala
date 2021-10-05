/** Copyright 2021 cross-language-cpp
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

import djinni.ast.Record.DerivingType
import djinni.ast._
import djinni.generatorTools._
import djinni.meta._
import djinni.writer.IndentWriter

import scala.collection.mutable

class CppCliGenerator(spec: Spec) extends Generator(spec) {
  final val cppCliBaseLibIncludePrefix = "djinni/cppcli/"

  val marshal = new CppCliMarshal(spec)
  val cppMarshal = new CppMarshal(spec)

  class CppCliRefs(name: String) {
    val hpp = new mutable.TreeSet[String]()
    val hppFwds = new mutable.TreeSet[String]()
    val cpp = new mutable.TreeSet[String]()
    val cppPrefix: String = spec.cppCliIncludeCppPrefix

    hpp.add(
      "#include " + q(
        cppPrefix + spec.cppFileIdentStyle(name) + "." + spec.cppHeaderExt
      )
    )

    def find(ty: TypeRef) { find(ty.resolved) }
    def find(tm: MExpr) {
      tm.args.foreach(find)
      find(tm.base)
    }
    def find(m: Meta): Unit = for (r <- marshal.references(m, name)) addRefs(r)

    private def addRefs(r: SymbolReference) = r match {
      case ImportRef(arg) => hpp.add("#include " + arg) // TODO add to `cpp`
      case DeclRef(_, _)  =>
    }
  }

  def withCppCliNs(namespace: String, t: String): String =
    withNs(Some(namespace), t)

  val writeCppCliCppFile = writeCppFileGeneric(
    spec.cppCliOutFolder.get,
    spec.cppCliNamespace,
    spec.cppCliIdentStyle.file,
    ""
  ) _

  def writeCppCliHppFile(
      name: String,
      origin: String,
      includes: Iterable[String],
      fwds: Iterable[String],
      f: IndentWriter => Unit,
      f2: IndentWriter => Unit = w => {}
  ) =
    writeHppFileGeneric(
      spec.cppCliOutFolder.get,
      spec.cppCliNamespace,
      spec.cppCliIdentStyle.file
    )(name, origin, includes, fwds, f, f2)

  def generateConstants(
      w: IndentWriter,
      ident: Ident,
      r: Record,
      consts: Seq[Const]
  ): Unit = {
    def writeCppCliLiteral(ty: TypeRef, v: Any) = {
      v match {
        case l: Long if List("long", "long?").contains(marshal.fieldType(ty)) =>
          w.w(l.toString + "L")
        case l: Long => w.w(l.toString)
        case d: Double if marshal.fieldType(ty).equals("float") =>
          w.w(d.toString + "F")
        case d: Double if marshal.fieldType(ty).equals("float?") =>
          w.w(d.toString + "F")
        case d: Double    => w.w(d.toString)
        case b: Boolean   => w.w(if (b) "true" else "false")
        case s: String    => w.w(s)
        case e: EnumValue => w.w(s"${marshal.typename(ty)}::${idCs.enum(e)}")
        case v: ConstRef  => w.w(idCs.const(v))
        case z: Map[_, _] => { // Value is record
          val recordMdef = ty.resolved.base.asInstanceOf[MDef]
          val record = recordMdef.body.asInstanceOf[Record]
          val vMap = z.asInstanceOf[Map[String, Any]]
          w.wl(s"gcnew ${marshal.typename(ty, needsHandle = false)}(")
          w.increase()
          // Use exact sequence
          val skipFirst = SkipFirst()
          for (f <- record.fields) {
            skipFirst {
              w.wl(",")
            }
            writeCppCliConst(w, f.ty, vMap.apply(f.ident.name))
            w.w(" /* " + idCs.field(f.ident) + " */ ")
          }
          w.w(")")
          w.decrease()
        }
      }
    }
    def writeCppCliConst(w: IndentWriter, ty: TypeRef, v: Any): Unit =
      ty.resolved.base match {
        case MOptional if ty.resolved.args.head.base != MString =>
          w.w(marshal.typename(ty, needsHandle = false) + "(")
          writeCppCliLiteral(ty, v)
          w.w(")")
        case _ => writeCppCliLiteral(ty, v)
      }
    def isInitOnly(c: Const) = c.ty.resolved.base match {
      case _: MPrimitive => false
      case _             => true
    }

    for (c <- consts) {
      w.wl
      writeDoc(w, c.doc)
      val fieldType = marshal.fieldType(c.ty)
      val fieldIdent = idCs.const(c.ident)
      if (isInitOnly(c)) {
        w.wl(s"static initonly $fieldType $fieldIdent;")
      } else {
        w.w(s"literal $fieldType $fieldIdent = ")
        writeCppCliConst(w, c.ty, c.value)
        w.wl(";")
      }
    }

    val initOnlyConsts = consts.filter(c => isInitOnly(c))
    if (initOnlyConsts.nonEmpty) {
      w.wl
      val self = marshal.typename(ident, r)
      w.w(s"static $self()").braced {
        initOnlyConsts.foreach(c => {
          w.w(s"${idCs.const(c.ident)} = ")
          writeCppCliConst(w, c.ty, c.value)
          w.wl(";")
        })
      }
    }
  }

  override def generateEnum(origin: String, ident: Ident, doc: Doc, e: Enum) {
    val refs = new CppCliRefs(ident.name)

    writeCppCliHppFile(
      ident,
      origin,
      refs.hpp,
      refs.hppFwds,
      w => {
        writeDoc(w, doc)
        if (e.flags) w.wl("[System::Flags]")
        w.w(s"public enum class ${marshal.typename(ident, e)}").bracedSemi {
          writeEnumOptionNone(w, e, idCs.enum(_))
          writeEnumOptions(w, e, idCs.enum(_))
          writeEnumOptionAll(w, e, idCs.enum(_))
        }
      }
    )

    writeCppCliCppFile(
      ident,
      origin,
      refs.hpp,
      w => {
        w.wl("// Empty... making sure the symbols get included in the build.")
      }
    )
  }

  override def generateRecord(
      origin: String,
      ident: Ident,
      doc: Doc,
      params: Seq[TypeParam],
      r: Record
  ) {
    val refs = new CppCliRefs(ident.name)
    refs.find(MString) // for: String^ ToString();
    r.fields.foreach(f => refs.find(f.ty))
    r.consts.foreach(c => refs.find(c.ty))

    def call(f: Field) = {
      f.ty.resolved.base match {
        case p: MPrimitive => "."
        case MOptional     => "."
        case MDate         => "."
        case e: MExtern    => if (e.cs.reference.get) "->" else "."
        case _             => "->"
      }
    }

    val self = marshal.typename(ident, r)
    val cppSelf = cppMarshal.fqTypename(ident, r)

    val fieldNamesInScope = r.fields.map(f => idCs.property(f.ident))

    writeCppCliHppFile(
      ident,
      origin,
      refs.hpp,
      refs.hppFwds,
      w => {
        writeDoc(w, doc)

        val interfaces = scala.collection.mutable.ArrayBuffer[String]()
        if (r.derivingTypes.contains(DerivingType.Ord))
          interfaces += s"System::IComparable<$self^>"
        if (r.derivingTypes.contains(DerivingType.Eq))
          interfaces += s"System::IEquatable<$self^>"
        val inheritanceList =
          if (interfaces.isEmpty) "" else interfaces.mkString(" : ", ", ", "")

        w.wl("[System::Serializable]")
        w.w(s"public ref class $self$inheritanceList").bracedSemi {
          w.wlOutdent("public:")

          generateConstants(w, ident, r, r.consts)

          // Properties.
          for (f <- r.fields) {
            w.wl
            val retType =
              s"${marshal.fqFieldType(f.ty.resolved, fieldNamesInScope)}"
            w.wl(s"property $retType ${idCs.property(f.ident)}").braced {
              w.wl(s"$retType get();")
            }
          }

          // Constructor.
          if (r.fields.nonEmpty) {
            w.wl
            writeAlignedCall(
              w,
              self + "(",
              r.fields,
              ")",
              f =>
                marshal.fqFieldType(
                  f.ty.resolved,
                  fieldNamesInScope
                ) + " " + idCs.local(f.ident)
            )
            w.wl(";")
          }

          w.wl
          w.wl(s"System::String^ ToString() override;")

          if (r.derivingTypes.contains(DerivingType.Eq)) {
            w.wl
            w.wl(s"virtual bool Equals($self^ other);")
            w.wl("bool Equals(System::Object^ obj) override;")
            w.wl("int GetHashCode() override;")
          }

          if (r.derivingTypes.contains(DerivingType.Ord)) {
            w.wl
            w.w(s"virtual int CompareTo($self^ other);")
          }

          w.wl
          w.wlOutdent("internal:")
          w.wl(s"using CppType = $cppSelf;")
          w.wl(s"using CsType = $self^;")
          w.wl
          w.wl(s"static CppType ToCpp(CsType cs);")
          w.wl(s"static CsType FromCpp(const CppType& cpp);")

          w.wl
          w.wlOutdent("private:")

          // Field definitions.
          for (f <- r.fields) {
            writeDoc(w, f.doc)
            w.wl(
              s"${marshal.fqFieldType(f.ty.resolved, fieldNamesInScope)} ${idCs
                .field(f.ident)};"
            )
          }
        }
      }
    )

    refs.cpp.add("#include " + q(cppCliBaseLibIncludePrefix + "Marshal.hpp"))
    refs.cpp.add("#include <memory>")

    writeCppCliCppFile(
      ident,
      origin,
      refs.cpp,
      w => {
        // Constructor.
        if (r.fields.nonEmpty) {
          writeAlignedCall(
            w,
            self + "::" + self + "(",
            r.fields,
            ")",
            f =>
              marshal.fqFieldType(f.ty.resolved, fieldNamesInScope) + " " + idCs
                .local(f.ident)
          )
          w.wl
          val init =
            (f: Field) => idCs.field(f.ident) + "(" + idCs.local(f.ident) + ")"
          w.wl(": " + init(r.fields.head))
          r.fields.tail.map(f => ", " + init(f)).foreach(w.wl)
          w.wl("{}")
        }

        // Properties.
        for (f <- r.fields) {
          w.wl
          val retType = s"${marshal.fieldType(f.ty)}"
          w.wl(s"$retType $self::${idCs.property(f.ident)}::get()").braced {
            w.wl(s"return ${idCs.field(f.ident)};")
          }
        }

        w.wl
        w.wl(s"System::String^ $self::ToString()").braced {
          val placeholders =
            r.fields.view.zipWithIndex.map { case (field, index) =>
              s"${idCs.property(field.ident)}{$index}"
            }
          val formatStr = placeholders.mkString(s"$self {{", ", ", "}}")
          val call = "return System::String::Format("
          w.w(s"""$call"$formatStr"""")
          r.fields.foreach(f => {
            w.wl(",")
            w.w(" " * call.length + idCs.property(f.ident))
          })
          w.wl(");")
        }

        if (r.derivingTypes.contains(DerivingType.Eq)) {
          w.wl
          w.w(s"bool $self::Equals($self^ other)").braced {
            w.wl("if (ReferenceEquals(nullptr, other)) return false;")
            w.wl("if (ReferenceEquals(this, other)) return true;")
            w.wl(
              r.fields
                .map(f => {
                  val property = idCs.property(f.ident)
                  f.ty.resolved.base match {
                    case p: MPrimitive => s"$property == other->$property"
                    case _ => s"$property${call(f)}Equals(other->$property)"
                  }
                })
                .mkString("return ", " && ", ";")
            )
          }

          w.wl
          w.w(s"bool $self::Equals(System::Object^ obj)").braced {
            w.wl("if (ReferenceEquals(nullptr, obj)) return false;")
            w.wl("if (ReferenceEquals(this, obj)) return true;")
            w.wl(s"return obj->GetType() == GetType() && Equals(($self^) obj);")
          }

          w.wl
          w.w(s"int $self::GetHashCode()").braced {
            w.wl(
              s"auto hashCode = ${idCs.property(r.fields.head.ident)}${call(r.fields.head)}GetHashCode();"
            )
            for (f <- r.fields.tail) {
              w.wl(
                s"hashCode = (hashCode * 397) ^ ${idCs.property(f.ident)}${call(f)}GetHashCode();"
              )
            }
            w.wl("return hashCode;")
          }
        }

        if (r.derivingTypes.contains(DerivingType.Ord)) {
          def compare(f: Field): String = {
            val property = idCs.property(f.ident)
            f.ty.resolved.base match {
              case MString =>
                s"System::String::Compare($property, other->$property, System::StringComparison::Ordinal)"
              case _ => s"$property${call(f)}CompareTo(other->$property)"
            }
          }
          w.wl
          w.w(s"int $self::CompareTo($self^ other)").braced {
            w.wl("if (ReferenceEquals(this, other)) return 0;")
            w.wl("if (ReferenceEquals(nullptr, other)) return 1;")
            for (f <- r.fields.dropRight(1)) {
              val local = idCs.local(f.ident) + "Comparison"
              w.wl(s"auto $local = ${compare(f)};")
              w.wl(s"if ($local != 0) return $local;")
            }
            w.wl(s"return ${compare(r.fields.last)};")
          }
        }

        // To/From C++
        val CppType = s"$self::CppType"
        val CsType = s"$self::CsType"
        w.wl
        w.wl(s"$CppType $self::ToCpp($CsType cs)").braced {
          w.wl("ASSERT(cs != nullptr);")
          writeAlignedCall(
            w,
            "return {",
            r.fields,
            "}",
            f => marshal.toCpp(f.ty, "cs->" + idCs.property(f.ident))
          )
          w.wl(";")
        }
        w.wl
        w.wl(s"$CsType $self::FromCpp(const $CppType& cpp)").braced {
          writeAlignedCall(
            w,
            s"return gcnew $self(",
            r.fields,
            ")",
            f => marshal.fromCpp(f.ty, "cpp." + idCpp.field(f.ident))
          )
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
  ) {
    val refs = new CppCliRefs(ident.name)
    i.methods.foreach(m => {
      m.params.foreach(p => refs.find(p.ty))
      m.ret.foreach(x => refs.find(x))
    })

    val self = marshal.typename(ident, i)
    val cppSelf = cppMarshal.fqTypename(ident, i)

    refs.hpp.add("#include <memory>")

    val methodNamesInScope = i.methods.map(m => idCs.method(m.ident))

    writeCppCliHppFile(
      ident,
      origin,
      refs.hpp,
      refs.hppFwds,
      w => {
        writeDoc(w, doc)
        w.w(s"public ref class $self abstract").bracedSemi {
          w.wlOutdent("public:")
          val skipFirst = new SkipFirst
          i.methods.foreach(m => {
            skipFirst { w.wl }
            writeDoc(w, m.doc)
            val staticVirtual = if (m.static) "static " else "virtual "
            val params = m.params.map(p => {
              marshal.paramType(p.ty, methodNamesInScope) + " " + idCs
                .local(p.ident)
            })
            val abstrct = if (!m.static) " abstract" else ""
            // Protect against conflicts with the class name.
            val origMethodName = idCs.method(m.ident)
            val methodName =
              if (self == origMethodName) "Do" + origMethodName
              else origMethodName
            w.wl(
              staticVirtual + marshal.returnType(
                m.ret
              ) + " " + methodName + params.mkString("(", ", ", s")$abstrct;")
            )
          })

          w.wl
          w.wlOutdent("internal:")
          spec.cppNnType match {
            case Some(nnPtr) =>
              w.wl(s"using CppType = $nnPtr<$cppSelf>;")
              w.wl(s"using CppOptType = std::shared_ptr<$cppSelf>;")
            case _ =>
              w.wl(s"using CppType = std::shared_ptr<$cppSelf>;")
              w.wl(s"using CppOptType = std::shared_ptr<$cppSelf>;")
          }
          w.wl(s"using CsType = $self^;")
          w.wl
          w.wl(s"static CppType ToCpp(CsType cs);")
          w.wl(s"static CsType FromCppOpt(const CppOptType& cpp);")
          w.wl(
            s"static CsType FromCpp(const CppType& cpp) { return FromCppOpt(cpp); }"
          )
        }
      }
    )

    refs.cpp.add("#include " + q(cppCliBaseLibIncludePrefix + "Marshal.hpp"))
    refs.cpp.add("#include " + q(cppCliBaseLibIncludePrefix + "Error.hpp"))
    refs.cpp.add(
      "#include " + q(cppCliBaseLibIncludePrefix + "WrapperCache.hpp")
    )
    i.methods.foreach(m => {
      m.params.foreach(p => {
        def include(tm: MExpr): Unit = tm.base match {
          case MMap =>
            tm.args.foreach(a => include(a))
          case d: MDef =>
            refs.cpp.add(
              "#include " + q(
                spec.cppIncludePrefix + spec.cppFileIdentStyle(
                  d.name
                ) + "." + spec.cppHeaderExt
              )
            )
          case _ =>
        }
        include(p.ty.resolved)
      })
    })

    writeCppCliCppFile(
      ident,
      origin,
      refs.cpp,
      w => {
        i.methods
          .filter(m => m.static)
          .foreach(m => {
            val params = m.params.map(p => {
              marshal.paramType(p.ty, methodNamesInScope) + " " + idCs
                .local(p.ident)
            })
            // Protect against conflicts with the class name.
            val origMethodName = idCs.method(m.ident)
            val methodName =
              if (self == origMethodName) "Do" + origMethodName
              else origMethodName
            w.w(
              marshal.returnType(m.ret) + s" $self::$methodName" + params
                .mkString("(", ", ", ")")
            ).braced {
              w.w("try").bracedEnd(" DJINNI_TRANSLATE_EXCEPTIONS()") {
                // TODO Check non-optional params for null
                val ret = m.ret.fold("")(_ => "auto cs_result = ")
                val call = ret + cppSelf + "::" + idCpp.method(m.ident) + "("
                writeAlignedCall(
                  w,
                  call,
                  m.params,
                  ")",
                  p => marshal.toCpp(p.ty, idCs.local(p.ident.name))
                )

                w.wl(";")
                m.ret.fold()(r =>
                  w.wl(s"return ${marshal.fromCpp(r, "cs_result")};")
                )
              }
              m.ret.fold()(r =>
                w.wl(
                  s"return ${dummyConstant(r)}; // Unreachable! (Silencing compiler warnings.)"
                )
              )
            }
            w.wl
          })

        val cppProxySelf = self + "CppProxy"
        val csProxySelf = self + "CsProxy"

        if (i.ext.cpp) {
          w.w(s"ref class $cppProxySelf : public $self").bracedSemi {
            w.wl(s"using CppType = std::shared_ptr<$cppSelf>;")
            w.wl("using HandleType = ::djinni::CppProxyCache::Handle<CppType>;")
            w.wlOutdent("public:")
            w.wl(
              s"$cppProxySelf(const CppType& cppRef) : _cppRefHandle(new HandleType(cppRef)) {}"
            )
            i.methods
              .filter(m => !m.static)
              .foreach(m => {
                w.wl
                val ret = marshal.returnType(m.ret, methodNamesInScope)
                val params = m.params.map(p => {
                  marshal.paramType(p.ty, methodNamesInScope) + " " + idCs
                    .local(p.ident)
                })
                // Protect against conflicts with the class name.
                val origMethodName = idCs.method(m.ident)
                val methodName =
                  if (self == origMethodName) "Do" + origMethodName
                  else origMethodName
                w.w(
                  s"$ret $methodName" + params.mkString("(", ", ", ") override")
                ).braced {
                  w.w("try").bracedEnd(" DJINNI_TRANSLATE_EXCEPTIONS()") {
                    // TODO Check non-optional params for null
                    val ret = m.ret.fold("")(_ => "auto cs_result = ")
                    val call = ret + "_cppRefHandle->get()->" + idCpp
                      .method(m.ident) + "("
                    writeAlignedCall(
                      w,
                      call,
                      m.params,
                      ")",
                      p => marshal.toCpp(p.ty, idCs.local(p.ident.name))
                    )

                    w.wl(";")
                    m.ret.fold()(r =>
                      w.wl(s"return ${marshal.fromCpp(r, "cs_result")};")
                    )
                  }
                  m.ret.fold()(r =>
                    w.wl(
                      s"return ${dummyConstant(r)}; // Unreachable! (Silencing compiler warnings.)"
                    )
                  )
                }
              })
            w.wl
            w.w("CppType djinni_private_get_proxied_cpp_object()").braced {
              w.wl("return _cppRefHandle->get();")
            }
            w.wl
            w.wlOutdent("private:")
            w.wl(s"AutoPtr<HandleType> _cppRefHandle;")
          }
          w.wl
        }

        if (i.ext.cppcli) {
          w.w(s"class $csProxySelf : public $cppSelf").bracedSemi {
            w.wl(
              s"using CsType = ${withCppCliNs(spec.cppCliNamespace, self)}^;"
            )
            w.wl(s"using CsRefType = ::djinni::CsRef<CsType>;")
            w.wl(
              "using HandleType = ::djinni::CsProxyCache::Handle<::djinni::CsRef<CsType>>;"
            )
            w.wlOutdent("public:")
            w.wl(
              s"$csProxySelf(CsRefType cs) : m_djinni_private_proxy_handle(std::move(cs)) {}"
            )
            w.wl(
              s"$csProxySelf(const ::djinni::CsRef<System::Object^>& ptr) : $csProxySelf(CsRefType(dynamic_cast<CsType>(ptr.get()))) {}"
            )
            for (m <- i.methods) {
              w.wl
              val ret = cppMarshal.fqReturnType(m.ret)
              val params = m.params.map(p =>
                cppMarshal.fqParamType(p.ty) + " " + idCpp.local(p.ident)
              )
              w.wl(
                s"$ret ${idCpp.method(m.ident)}${params.mkString("(", ", ", ")")} override"
              ).braced {
                val ret = m.ret.fold("")(_ => "auto cs_result = ")
                val call =
                  s"djinni_private_get_proxied_cs_object()->${idCs.method(m.ident)}("
                writeAlignedCall(
                  w,
                  ret + call,
                  m.params,
                  ")",
                  p => s"${marshal.fromCpp(p.ty, idCpp.local(p.ident))}"
                )
                w.wl(";")
                m.ret.fold()(ty => {
                  w.wl("// TODO check cs_result for null")
                  w.wl(s"return ${marshal.toCpp(ty, "cs_result")};")
                })
              }
            }
            w.wl
            w.w("CsType djinni_private_get_proxied_cs_object()").braced {
              w.wl("return m_djinni_private_proxy_handle.get().get();")
            }
            w.wl
            w.wlOutdent("private:")
            w.wl("HandleType m_djinni_private_proxy_handle;")
          }
          w.wl
        }

        // To/From C++
        val CppType = s"$self::CppType"
        val CppOptType = s"$self::CppOptType"
        val CsType = s"$self::CsType"
        w.wl(s"$CppType $self::ToCpp($CsType cs)").braced {
          w.w("if (!cs)").braced {
            w.wl("return nullptr;")
          }
          if (i.ext.cpp && !i.ext.cppcli) {
            // C++ only. In this case we *must* unwrap a proxy object - the dynamic_cast will
            // throw bad_cast if we gave it something of the wrong type.
            w.wl(
              s"return dynamic_cast<$cppProxySelf^>(cs)->djinni_private_get_proxied_cpp_object();"
            )
          } else if (i.ext.cpp || i.ext.cppcli) {
            // C# only, or C# and C++.
            if (i.ext.cpp) {
              // If it could be implemented in C++, we might have to unwrap a proxy object.
              w.wl(s"if (auto cs_ref = dynamic_cast<$cppProxySelf^>(cs))")
                .braced {
                  w.wl(
                    "return cs_ref->djinni_private_get_proxied_cpp_object();"
                  )
                }
            }
            w.wl(s"return ::djinni::get_cs_proxy<$csProxySelf>(cs);")
          } else {
            // Neither C# nor C++.  Unusable, but generate compilable code.
            w.wl(
              "DJINNI_UNIMPLEMENTED(\"Interface not implementable in any language.\");"
            )
          }
        }
        w.wl
        w.wl(s"$CsType $self::FromCppOpt(const $CppOptType& cpp)").braced {
          w.w("if (!cpp)").braced {
            w.wl("return nullptr;")
          }
          if (i.ext.cppcli && !i.ext.cpp) {
            // C# only. In this case we *must* unwrap a proxy object - the dynamic_cast will
            // throw bad_cast if we gave it something of the wrong type.
            w.wl(
              s"return dynamic_cast<$csProxySelf*>(cpp.get())->djinni_private_get_proxied_cs_object();"
            )
          } else if (i.ext.cppcli || i.ext.cpp) {
            // C++ only, or C++ and C#.
            if (i.ext.cppcli) {
              // If it could be implemented in C#, we might have to unwrap a proxy object.
              w.w(s"if (auto cpp_ptr = dynamic_cast<$csProxySelf*>(cpp.get()))")
                .braced {
                  w.wl(
                    "return cpp_ptr->djinni_private_get_proxied_cs_object();"
                  )
                }
            }
            w.wl(s"return ::djinni::get_cpp_proxy<$cppProxySelf^>(cpp);")
          } else {
            // Neither C# nor C++.  Unusable, but generate compilable code.
            w.wl(
              "DJINNI_UNIMPLEMENTED(\"Interface not implementable in any language.\");"
            )
          }
        }
      }
    )
  }

  private def dummyConstant(ret: TypeRef): String = {
    val typeStr = marshal.typename(ret)
    ret.resolved.base match {
      case p: MPrimitive =>
        p.cppCliName match {
          case "double" => "0.0"
          case _        => "0"
        }
      case d: MDef =>
        d.defType match {
          case DEnum => s"($typeStr)0"
          case _     => "nullptr"
        }
      case MDate => typeStr + "()"
      case MOptional =>
        ret.resolved.args.head.base match {
          case _: MPrimitive => typeStr + "()"
          case MDate         => typeStr + "()"
          case d: MDef =>
            d.defType match {
              case DEnum => typeStr + "()"
              case _     => "nullptr"
            }
          case e: MExtern =>
            if (e.cs.reference.get) "nullptr" else typeStr + "()"
          case _ => "nullptr"
        }
      case e: MExtern =>
        if (e.cs.reference.get) "nullptr" else typeStr + "()"
      case _ => "nullptr"
    }
  }
}
