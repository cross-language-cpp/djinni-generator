package djinni

import java.io.File

import djinni.ast._
import djinni.generatorTools.{IdentStyle, ImportRef, Spec}
import djinni.meta.{MExpr, Meta}
import djinni.writer.IndentWriter

import scala.collection.mutable

class ScalaImplicitsGenerator(spec: Spec) extends Generator(spec) {

  override def generateEnum(origin: String, ident: Ident, doc: Doc, e: ast.Enum): Unit = ???
  override def generateRecord(origin: String, ident: Ident, doc: Doc, params: Seq[TypeParam], r: Record): Unit = ???
  override def generateInterface(origin: String, ident: Ident, doc: Doc, typeParams: Seq[TypeParam], i: Interface): Unit = ???
  val marshal = new JavaMarshal(spec)


  class JavaRefs() {
    var java = mutable.TreeSet[String]()

    spec.javaAnnotation.foreach(pkg => java.add(pkg))
    spec.javaNullableAnnotation.foreach(pkg => java.add(pkg))
    spec.javaNonnullAnnotation.foreach(pkg => java.add(pkg))

    def find(ty: TypeRef) { find(ty.resolved) }
    def find(tm: MExpr) {
      tm.args.foreach(find)
      find(tm.base)
    }
    def find(m: Meta) = for(r <- marshal.references(m)) r match {
      case ImportRef(arg) => java.add(arg)
      case _ =>
    }
  }

  override def generate(idl: Seq[TypeDecl]): Unit = {
    val refs = new JavaRefs

    // Resolve all references
    for (td <- idl.collect { case itd: InternTypeDecl => itd }) td.body match {
      case i: Interface => resolveReferences(i, td, refs)
      case others => // Do nothing
    }
    refs.java.add("scala.concurrent.{Future, Promise}")
    refs.java.add("scala.collection.JavaConverters._")
    // Write file
    createScalaPackageFile(refs.java, {(w) =>
      w.w("package object implicits")
      w.braced {
        w.wl("class LedgerCoreWrappedException(val code: ErrorCode, message: String) extends Exception(message)")
        for (td <- idl.collect { case itd: InternTypeDecl => itd }) td.body match {
          case e: Enum =>
            if (td.ident.name == "ErrorCode") {
              for (v <- e.options) {
                w.wl(s"class ${IdentStyle.camelUpper(v.ident.name)}Exception(message: String) extends LedgerCoreWrappedException(ErrorCode.${IdentStyle.underCaps(v.ident.name)}, message)")
              }
            }
          case others => // Do nothing
        }
        w.w(s"private def wrapLedgerCoreError(error: ${spec.javaPackage.getOrElse("ledger")}.Error): LedgerCoreWrappedException =").braced {
          w.w("error.getCode match").braced {
            for (td <- idl.collect { case itd: InternTypeDecl => itd }) td.body match {
              case e: Enum =>
                if (td.ident.name == "ErrorCode") {
                  for (v <- e.options) {
                    w.wl(s"case ErrorCode.${IdentStyle.underCaps(v.ident.name)} => new ${IdentStyle.camelUpper(v.ident.name)}Exception(error.getMessage)")
                  }
                }
              case others => // Do nothing
            }
          }
        }
        w.wl("private def arrayList2Array[T](a: Array[T]): java.util.ArrayList[T] = new java.util.ArrayList[T](a.toList.asJava.asInstanceOf[java.util.Collection[T]])")
        for (td <- idl.collect { case itd: InternTypeDecl => itd }) td.body match {
          case i: Interface => generateRichInterface(td.origin, td.ident, td.doc, td.params, i, w)
          case others => // Do nothing
        }
      }
    })
  }

  private def generateRichInterface(origin: String, ident: Ident, doc: Doc, typeParams: Seq[TypeParam], i: Interface, w: IndentWriter): Unit = {
    val className = marshal.typename(ident, i)
    w.w(s"implicit class Rich$className(val self: $className)").braced {
      for (m <- i.methods) {
        wrapCallbackWithFuture(m, w)
      }
    }
  }

  private def paramToString(param: Field): String = {
      s"${param.ident.name}: ${javaToScalaType(marshal.typename(param.ty))}"
  }

  val ListCallbackPattern = "([a-zA-Z0-9]+)ListCallback".r
  val CallbackPattern = "([a-zA-Z0-9]+)Callback".r
  private def callbackToUnderlyingType(param: Field): String = {
    param.ty.expr.ident.name match {
      case "BoolCallback" => "Boolean"
      case "BoolListCallback" => "ArrayList[Boolean]"
      case ListCallbackPattern(t) => s"ArrayList[${javaToScalaType(t)}]"
      case CallbackPattern(t) => javaToScalaType(t)
    }
  }

  private def javaToScalaType(t: String): String = {
    t match {
      case "int" => "Int"
      case "float" => "Float"
      case "byte" => "Byte"
      case "byte[]" => "Array[Byte]"
      case "double" => "Double"
      case "long" => "Long"
      case "bool" => "Boolean"
      case "boolean" => "Boolean"
      case "I32" => "Int"
      case "short" => "Short"
      case "I64" => "Long"
      case "I8" => "Byte"
      case "I16" => "Short"
      case "Binary" => "Array[Byte]"
      case "ArrayList" => "Array"
      case others =>
        if (others.contains("<"))
          javaToScalaType(t.takeWhile(_ != '<')) + "[" + javaToScalaType(t.drop(t.indexOf('<') + 1).takeWhile(_ != '>')) + "]"
        else
          others
    }
  }

  private def scalarToClassType(t: String): String = {
    t match {
      case "Int" => "java.lang.Integer"
      case "Float" => "java.lang.Float"
      case "Byte" => "java.lang.Byte"
      case "Double" => "java.lang.Double"
      case "Long" => "java.lang.Long"
      case "Boolean" => "java.lang.Boolean"
      case "Short" => "java.lang.Short"
      case "Array" => "Array"
      case others =>
        if (others.contains("[") && false)
          t.takeWhile(_ != '<') + "[" + scalarToClassType(t.drop(t.indexOf('<') + 1).takeWhile(_ != '>')) + "]"
        else
          others
    }
  }

  private def callbackToFuture(param: Field): String = {
    s"Future[${callbackToUnderlyingType(param)}]"
  }

  private def callbackToPromise(param: Field): String = {
    s"Promise[${callbackToUnderlyingType(param)}]"
  }

  private def callbackToResultType(param: Field): String = {
    scalarToClassType(callbackToUnderlyingType(param))
  }

  private def wrapCallbackWithFuture(method: Interface.Method, w: IndentWriter): Unit = {
    method.params.lastOption foreach {(param) =>
      val typename = param.ty.expr.ident.name
      val isCallback = typename.endsWith("Callback")
      val methodParams = method.params.map(paramToString).dropRight(1).mkString(", ")
      if (isCallback) {
        w.w(s"def ${idJava.method(method.ident.name)}($methodParams): ${callbackToFuture(param)} =").braced {
          w.wl(s"val promise = ${callbackToPromise(param)}()")
          w.w(s"self.${idJava.method(method.ident.name)}(")
          w.w(method.params.map(p => {
            if (marshal.typename(p.ty).contains("ArrayList"))
              s"arrayList2Array(${p.ident.name}), "
            else
              p.ident.name + ", "
          }).dropRight(1).mkString(""))
          w.w(s"new ${marshal.typename(param.ty)}() {") nested {
            w.wl.w(s"override def onCallback(result: ${callbackToResultType(param)}, error: ${spec.javaPackage.get}.Error): Unit = ").braced {
              w.w("if (error != null)").braced {
                w.wl("promise.failure(wrapLedgerCoreError(error))")
              }
              w.w("else").braced {
                w.wl("promise.success(result)")
              }
            }
          }
          w.wl("})")
          w.wl("promise.future")
        }
      }
    }
  }

  private def resolveReferences(i: Interface, td: InternTypeDecl, refs: JavaRefs): Unit = {
    i.methods.map(m => {
      m.params.map(p => refs.find(p.ty))
      m.ret.foreach(refs.find)
    })
    i.consts.map(c => {
      refs.find(c.ty)
    })
    //val className = marshal.typename(td.ident, i)
    //refs.java.add(spec.javaPackage.get + "." + className)
  }

  private def createScalaPackageFile(refs: Iterable[String], f: IndentWriter => Unit) = {
    createFile(_outFolder, "package.scala", (w: IndentWriter) => {
      w.wl("// AUTOGENERATED FILE - DO NOT MODIFY!")
      w.wl
      spec.javaPackage.foreach(s => w.wl(s"package $s").wl)
      if (refs.nonEmpty) {
        refs.foreach(s => w.wl(s"import $s"))
        w.wl
      }
      f(w)
    })
  }

  private val _outFolder = new File(spec.javaOutFolder.get.getParentFile, "scala")
}
