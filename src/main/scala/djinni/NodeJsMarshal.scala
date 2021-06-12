package djinni

import djinni.ast._
import djinni.generatorTools._
import djinni.meta._
import djinni.writer.IndentWriter

import scala.collection.mutable.ListBuffer

class NodeJsMarshal(spec: Spec) extends CppMarshal(spec) {

  override def typename(tm: MExpr): String = toNodeType(tm, None, Seq())

  override def fqTypename(tm: MExpr): String = toNodeType(tm, Some(spec.cppNamespace), Seq())

  override def typename(name: String, ty: TypeDef): String = ty match {
    case e: Enum => idNode.enumType(name)
    case i: Interface => idNode.ty(name)
    case r: Record => idNode.ty(name)
  }

  override def paramType(tm: MExpr): String = toNodeParamType(tm)

  override def fqParamType(tm: MExpr): String = toNodeParamType(tm, Some(spec.cppNamespace))

  private def toNodeParamType(tm: MExpr, namespace: Option[String] = None, scopeSymbols: Seq[String] = Seq()): String = {
    toNodeType(tm, namespace, scopeSymbols)
  }

  private def toNodeType(tm: MExpr, namespace: Option[String], scopeSymbols: Seq[String]): String = {

    def base(m: Meta): String = m match {
      case p: MPrimitive => p.nodeJSName
      case MString => "String"
      case MDate => "Date"
      case MBinary => "String"
      case MOptional => "MaybeLocal"
      case MList => "Array"
      case MSet => "Set"
      case MMap => "Map"
      case d: MDef =>
        d.defType match {
          case DInterface => withNamespace(idNode.ty(d.name), namespace, scopeSymbols)
          case _ => super.toCppType(tm, namespace, scopeSymbols)
        }
      case p: MParam => idNode.typeParam(p.name)
      case _ => super.toCppType(tm, namespace, scopeSymbols)
    }

    def expr(tm: MExpr): String = {
      spec.cppNnType match {
        case Some(nnType) =>
          // if we're using non-nullable pointers for interfaces, then special-case
          // both optional and non-optional interface types
          val args = if (tm.args.isEmpty) "" else tm.args.map(expr).mkString("<", ", ", ">")
          tm.base match {
            case d: MDef =>
              d.defType match {
                case DInterface => s"${nnType}<${withNamespace(idNode.ty(d.name), namespace, scopeSymbols)}>"
                case _ => base(tm.base) + args
              }
            case MOptional =>
              tm.args.head.base match {
                case d: MDef =>
                  d.defType match {
                    case DInterface => s"std::shared_ptr<${withNamespace(idCpp.ty(d.name), namespace, scopeSymbols)}>"
                    case _ => base(tm.base) + args
                  }
                case _ => base(tm.base) + args
              }
            case _ => base(tm.base) + args
          }
        case None =>
          if (isOptionalInterface(tm)) {
            // otherwise, interfaces are always plain old shared_ptr
            expr(tm.args.head)
          } else {
            base(tm.base)
          }
      }
    }

    expr(tm)
  }

  def toJSType(tm: MExpr): String = {
    def base(m: Meta): String = m match {
      case p: MPrimitive => p.jsName
      case MString => "string"
      case MList => s"Array<${toJSType(tm.args(0))}>"
      case MSet => s"Set<${toJSType(tm.args(0))}>"
      case MMap => s"Map<${toJSType(tm.args(0))}, ${toJSType(tm.args(1))}>"
      case MOptional => s"?${toJSType(tm.args(0))}"
      case _ => toNodeType(tm, None, Seq())
    }
    base(tm.base)
  }

  private def withNamespace(name: String, namespace: Option[String] = None, scopeSymbols: Seq[String] = Seq()): String = {

    val ns = namespace match {
      case Some(ns) => Some(ns)
      case None => if (scopeSymbols.contains(name)) Some(spec.cppNamespace) else None
    }
    withNs(ns, name)
  }

  override def returnType(ret: Option[TypeRef], scopeSymbols: Seq[String]): String = {
    ret.fold("void")(toNodeType(_, None, scopeSymbols))
  }

  override def returnType(ret: Option[TypeRef]): String = ret.fold("void")(toNodeType(_, None))

  private def toNodeType(ty: TypeRef, namespace: Option[String] = None, scopeSymbols: Seq[String] = Seq()): String =
    toNodeType(ty.resolved, namespace, scopeSymbols)

  override def fqReturnType(ret: Option[TypeRef]): String = {
    ret.fold("void")(toNodeType(_, Some(spec.cppNamespace)))
  }

  def hppReferences(m: Meta, exclude: String, forwardDeclareOnly: Boolean, nodeMode: Boolean, onlyNodeRef: Boolean = false): Seq[SymbolReference] = m match {
    case MOptional =>
      //If cppOptionalHeader is relative path, we have to concatenate with cpp include path
      var importRef = spec.cppOptionalHeader
      if(importRef.length > 0){
        importRef = importRef.slice(1, importRef.length - 1)
        importRef = if(importRef == '<') spec.cppOptionalHeader else s""""${spec.nodeIncludeCpp}/${importRef}""""
      }
      List(ImportRef(importRef))
    case d: MDef =>
      val nodeRecordImport = s"${spec.nodeIncludeCpp}/${d.name}"
      d.body match {
        case i: Interface =>
          val base = if (d.name != exclude) {

            var cppInterfaceImport = s""""${idNode.ty(d.name)}"""
            if (i.ext.cpp) {
              cppInterfaceImport = s"${cppInterfaceImport}Cpp"
            }

            cppInterfaceImport = s"""$cppInterfaceImport.${spec.cppHeaderExt}""""
            val nodeInterfaceImport = s""""${spec.nodeIncludeCpp}/${d.name}.${spec.cppHeaderExt}""""

            if (nodeMode && !onlyNodeRef) {
              List(ImportRef("<memory>"), ImportRef(cppInterfaceImport), ImportRef(nodeInterfaceImport))
            } else if(nodeMode && onlyNodeRef) {
              List(ImportRef(cppInterfaceImport))
            } else {
              List(ImportRef("<memory>"), ImportRef(cppInterfaceImport))
            }

          } else List(ImportRef("<memory>"))

          spec.cppNnHeader match {
            case Some(nnHdr) => ImportRef(nnHdr) :: base
            case _ => base
          }
        case r: Record =>
          if (d.name != exclude) {
            val localOnlyNodeRef = true
            var listOfReferences : Seq[SymbolReference] = List(ImportRef(include(nodeRecordImport, r.ext.cpp)))
            for (f <- r.fields) {
              val args = f.ty.resolved.args
              if(!args.isEmpty){
                args.foreach((arg)=> {
                  listOfReferences = listOfReferences ++ hppReferences(arg.base, exclude, forwardDeclareOnly, nodeMode, localOnlyNodeRef)
                })
              }
            }
            listOfReferences
          } else {
            List()
          }
        case e: Enum =>
          if (d.name != exclude) {
            List(ImportRef(include(nodeRecordImport)))
          } else {
            List()
          }
        case _ => super.hppReferences(m, exclude, forwardDeclareOnly)
      }
    case _ => super.hppReferences(m, exclude, forwardDeclareOnly)
  }

  override def include(ident: String, isExtendedRecord: Boolean = false): String = {
    val prefix = if (isExtendedRecord) spec.cppExtendedRecordIncludePrefix else spec.cppIncludePrefix
    q(prefix + spec.cppFileIdentStyle(ident) + "." + spec.cppHeaderExt)
  }

  override def toCpp(tm: MExpr, expr: String): String = throw new AssertionError("cpp to cpp conversion")

  override def fromCpp(tm: MExpr, expr: String): String = throw new AssertionError("cpp to cpp conversion")

  def toCppArgument(tm: MExpr, converted: String, converting: String, wr: IndentWriter): IndentWriter = {

    //Cast of List, Set and Map
    def toCppContainer(container: String, binary: Boolean = false): IndentWriter = {

      def toVector(cppTemplType: String, nodeTemplType: String): IndentWriter = {
        val containerName = s"${converted}_container"
        if (binary) {
          // From mow on, we will expect byte of arrays to be strings so let's check this
          wr.wl(s"if(!$converting->IsString())").braced {
            val error = s""""$converting should be a hexadecimal string.""""
            wr.wl(s"Nan::ThrowError($error);")
          }
          //Nan.Utf8String
          wr.wl(s"std::vector<uint8_t> $converted;")
          wr.wl(s"Nan::Utf8String str_$converted($converting);")
          wr.wl(s"std::string string_$converted(*str_$converted, str_$converted.length());")
          wr.wl(s"""if (string_$converted.rfind("0x", 0) == 0)""").braced {
            wr.wl(s"$converted = djinni::js::hex::toByteArray(string_$converted.substr(2));")
          }
          wr.wl("else").braced {
            wr.wl(s"$converted = std::vector<uint8_t>(string_$converted.cbegin(), string_$converted.cend());")
          }

        } else {
          wr.wl(s"vector<$cppTemplType> $converted;")
          wr.wl(s"Local<$container> $containerName = Local<$container>::Cast($converting);")
          wr.wl(s"for(uint32_t ${converted}_id = 0; ${converted}_id < $containerName->Length(); ${converted}_id++)").braced {
            wr.wl(s"if($containerName->Get(Nan::GetCurrentContext(), ${converted}_id).ToLocalChecked()->Is$nodeTemplType())").braced {
              //Cast to c++ types
              toCppArgument(tm.args(0), s"${converted}_elem", s"$containerName->Get(Nan::GetCurrentContext(), ${converted}_id).ToLocalChecked()", wr)
              //Append to resulting container
              wr.wl(s"$converted.emplace_back(${converted}_elem);")
            }
          }
        }
        wr.wl
      }

      if (!tm.args.isEmpty) {

        val cppTemplType = super.paramType(tm.args(0))
        val nodeTemplType = if(isInterface(tm.args(0)) || isRecord(tm.args(0))) "Object" else paramType(tm.args(0))

        if (container == "Map" && tm.args.length > 1) {

          val cppTemplValueType = super.paramType(tm.args(1))
          val nodeTemplValueType = if(isInterface(tm.args(1)) || isRecord(tm.args(0))) "Object" else paramType(tm.args(1))

          val containerName = s"${converted}_container"
          wr.wl(s"unordered_map<$cppTemplType, $cppTemplValueType> $converted;")
          wr.wl(s"Local<$container> $containerName = Local<$container>::Cast($converting);")

          //Get properties' names, loop over them and get their values
          val propretyNames = s"${converted}_prop_names"
          wr.wl(s"auto $propretyNames = $containerName->GetPropertyNames(Nan::GetCurrentContext()).ToLocalChecked();")
          wr.wl(s"for(uint32_t ${converted}_id = 0; ${converted}_id < $propretyNames->Length(); ${converted}_id++)").braced {
            wr.wl(s"auto key = $propretyNames->Get(Nan::GetCurrentContext(), ${converted}_id).ToLocalChecked();")
            //Check types before access
            wr.wl(s"auto ${converted}_key_ctx = $containerName->Get(Nan::GetCurrentContext(), key).ToLocalChecked();")
            wr.wl(s"if(key->Is$nodeTemplType() && ${converted}_key_ctx->Is$nodeTemplValueType())").braced {
              //Cast to c++ types
              toCppArgument(tm.args(0), s"${converted}_key", "key", wr)
              toCppArgument(tm.args(1), s"${converted}_value", s"${converted}_key_ctx", wr)
              //Append to resulting container
              wr.wl(s"$converted.emplace(${converted}_key,${converted}_value);")
            }
          }
          wr.wl
        } else {
          toVector(cppTemplType, nodeTemplType)
        }
      } else {
        if (binary) toVector("uint8_t", "Uint32") else wr.wl("//Type name not found !")
      }

    }

    def toSupportedCppNativeTypes(inputType: String): String = {
      inputType match {
        case "int8_t" | "int16_t" => "int32_t"
        case "float" => "double"
        case _ => inputType
      }
    }

    val cppType = super.paramType(tm)
    var interfaceName = tm.base match {
      case d: MDef => idCpp.ty(d.name)
      case _ => cppType
    }
    val nodeType = paramType(tm)

    def base(m: Meta): IndentWriter = m match {
      case p: MPrimitive => wr.wl(s"auto $converted = Nan::To<${toSupportedCppNativeTypes(p.cName)}>($converting).FromJust();")
      case MString =>
        wr.wl(s"Nan::Utf8String string_$converted($converting->ToString(Nan::GetCurrentContext()).ToLocalChecked());")
        wr.wl(s"auto $converted = std::string(*string_$converted);")
      case MDate => {
        wr.wl(s"auto time_$converted = Nan::To<int32_t>($converting).FromJust();")
        wr.wl(s"auto $converted = chrono::system_clock::time_point(chrono::milliseconds(time_$converted));")
      }
      case MBinary => toCppContainer("Array", binary = true)
      case MOptional => {

        val start = cppType.indexOf("<")
        val end = cppType.length - (cppType.reverse.indexOf(">") + 1)
        if(isInterface(tm.args(0))) {
          wr.wl(s"$cppType $converted = nullptr;")
        } else {
          wr.wl(s"auto $converted = ${spec.cppOptionalTemplate}<${cppType.substring(start + 1, end)}>();")
        }

        wr.wl(s"if(!$converting->IsNull() && !$converting->IsUndefined())").braced {
          toCppArgument(tm.args(0), s"opt_$converted", converting, wr)
          if(isInterface(tm.args(0))) {
            wr.wl(s"$converted = opt_$converted;")
          } else {
            wr.wl(s"$converted.emplace(opt_$converted);")
          }
        }
        wr.wl
      }
      case MList => toCppContainer("Array")
      case MSet => toCppContainer("Set")
      case MMap => toCppContainer("Map")
      case d: MDef =>
        d.body match {
          case e: Enum =>
            val castToEnumType = s"(${spec.cppNamespace}::${idCpp.enumType(d.name)})"
            wr.wl(s"auto $converted = ${castToEnumType}Nan::To<int>($converting).FromJust();")
          case r: Record =>
            // Field definitions.
            var listOfRecordArgs = new ListBuffer[String]()
            var count = 1
            for (f <- r.fields) {
              wr.wl
              val fieldName = idCpp.field(f.ident)
              val quotedFieldName = s""""$fieldName""""
              wr.wl(s"auto field_${converted}_$count = Nan::Get($converting->ToObject(Nan::GetCurrentContext()).ToLocalChecked(), Nan::New<String>($quotedFieldName).ToLocalChecked()).ToLocalChecked();")
              toCppArgument(f.ty.resolved, s"${converted}_$count", s"field_${converted}_$count", wr)
              listOfRecordArgs += s"${converted}_$count"
              count = count + 1
            }
            wr.wl(s"${idCpp.ty(d.name)} $converted${listOfRecordArgs.toList.mkString("(", ", ", ")")};")
            wr.wl
          case i: Interface =>
            if(d.name.contains("Callback")) {
              if(i.ext.nodeJS) {
                //Set promise if it is a callback
                wr.wl
                wr.wl("//Create promise and set it into Callback")
                wr.wl(s"auto ${converted}_resolver = v8::Promise::Resolver::New(Nan::GetCurrentContext()).ToLocalChecked();")
                wr.wl(s"$nodeType *njs_ptr_$converted = new $nodeType(${converted}_resolver);")
                wr.wl(s"std::shared_ptr<$nodeType> $converted(njs_ptr_$converted);")
              }
            } else {
              wr.wl(s"Local<Object> njs_$converted = $converting->ToObject(Nan::GetCurrentContext()).ToLocalChecked();")
              wr.wl(s"auto $converted = djinni::js::ObjectWrapper<${spec.cppNamespace}::$interfaceName>::Unwrap(njs_$converted);");

              if(i.ext.cpp){
                wr.wl(s"if(!$converted)").braced{
                  val error = s""""NodeJs Object to $nodeType failed""""
                  wr.wl(s"return Nan::ThrowError($error);")
                }
              }
            }
            wr.wl
        }
      case e: MExtern => e.defType match {
        case DInterface => wr.wl(s"std::shared_ptr<${e.cpp.typename.get}>")
        case _ => wr.wl(e.cpp.typename.get)
      }      
      case p: MParam => wr.wl(idNode.typeParam(p.name))
    }

    base(tm.base)
  }

  def fromCppArgument(tm: MExpr, converted: String, converting: String, wr: IndentWriter): IndentWriter = {

    //Cast of List, Set and Map
    def fromCppContainer(container: String, binary: Boolean = false): IndentWriter = {

      def fromVector(): IndentWriter = {
        if (binary) {
          wr.wl(s"""auto $converted = Nan::New<String>("0x" + djinni::js::hex::toString($converting)).ToLocalChecked();""")
        } else {
          wr.wl(s"Local<$container> $converted = Nan::New<$container>();")
          //Loop and cast elements of $converting
          wr.wl(s"for(size_t ${converted}_id = 0; ${converted}_id < $converting.size(); ${converted}_id++)").braced {
            //Cast
            if (!binary) {
              fromCppArgument(tm.args(0), s"${converted}_elem", s"$converting[${converted}_id]", wr)
            } else {
              wr.wl(s"auto ${converted}_elem = Nan::New<Uint32>($converting[${converted}_id]);")
            }
            wr.wl(s"Nan::Set($converted, (int)${converted}_id,${converted}_elem);")
          }
        }
        wr.wl
      }

      if (!tm.args.isEmpty) {

        if (container == "Map" && tm.args.length > 1) {
          wr.wl(s"Local<$container> $converted = Map::New((Nan::GetCurrentContext())->GetIsolate());")
          //Loop and cast elements of $converting
          wr.wl(s"for(auto const& ${converted}_elem : $converting)").braced {
            //Cast
            fromCppArgument(tm.args(0), s"${converted}_first", s"${converted}_elem.first", wr)
            fromCppArgument(tm.args(1), s"${converted}_second", s"${converted}_elem.second", wr)
            wr.wl(s"$converted->Set(Nan::GetCurrentContext(), ${converted}_first, ${converted}_second);")
          }
          wr.wl

        } else {
          fromVector()
        }
      } else {
        if (binary) fromVector() else wr.wl("//Type name not found !")
      }

    }

    def simpleCheckedCast(nodeType: String, toCheck: Boolean = true): String = {
      val cast = s"auto $converted = Nan::New<$nodeType>($converting)"
      if (toCheck) s"$cast.ToLocalChecked();" else s"$cast;"
    }
    def base(m: Meta): IndentWriter = m match {
      case p: MPrimitive => wr.wl(simpleCheckedCast(p.nodeJSName, false))
      case MString => wr.wl(simpleCheckedCast("String"))
      case MDate => {
        wr.wl(s"auto date_$converted = chrono::duration_cast<chrono::milliseconds>(${converting}.time_since_epoch()).count();")
        wr.wl(s"auto $converted = Nan::New<Date>(date_$converted).ToLocalChecked();")
      }
      case MBinary => fromCppContainer("Array", true)
      case MOptional => {
        if(!isInterface(tm.args(0))) {
          wr.wl(s"Local<Value> $converted;")
          wr.wl(s"if($converting)").braced {
            wr.wl(s"auto ${converted}_optional = ($converting).value();")
            fromCppArgument(tm.args(0), s"${converted}_tmp",  s"${converted}_optional", wr)
            wr.wl(s"$converted = ${converted}_tmp;")
          }
        } else {
          fromCppArgument(tm.args(0), converted, converting, wr)
        }
        wr.wl
      }
      case MList => fromCppContainer("Array")
      case MSet => fromCppContainer("Set")
      case MMap => fromCppContainer("Map")
      case d: MDef =>
        d.body match {
          case e: Enum => wr.wl(s"auto $converted = Nan::New<Integer>((int)$converting);")
          case r: Record =>
            // Field definitions.
            wr.wl(s"auto $converted = Nan::New<Object>();")
            var count = 1
            for (f <- r.fields) {
              val fieldName = idCpp.field(f.ident)
              fromCppArgument(f.ty.resolved, s"${converted}_$count", s"$converting.$fieldName", wr)
              val quotedFieldName = s""""$fieldName""""
              wr.wl(s"Nan::DefineOwnProperty($converted, Nan::New<String>($quotedFieldName).ToLocalChecked(), ${converted}_$count);")
              count = count + 1
            }
            wr.wl
          case i: Interface =>
            val nodeType = paramType(tm)
            val cppType = super.paramType(tm)
            //Use wrap methods
            wr.wl(s"auto ${converted} = ${idNode.ty(d.name)}::wrap($converting);")
            wr.wl
        }
      case e: MExtern => e.defType match {
        case DInterface =>
          wr.wl(s"auto ${converted} = ${idNode.ty(e.name)}::wrap($converting);")
        case _ => wr.wl(e.cpp.typename.get)
      }
      case p: MParam => wr.wl(simpleCheckedCast("Object"))
    }

    base(tm.base)
  }
}

