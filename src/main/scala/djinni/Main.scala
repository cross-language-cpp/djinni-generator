/**
  * Copyright 2014 Dropbox, Inc.
  * Copyright 2021 cross-language-cpp
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *    http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package djinni

import java.io.{BufferedWriter, File, FileNotFoundException, FileWriter, IOException}

import djinni.generatorTools._
import scopt.{OptionDef, OptionParser}

object Main {

  def main(args: Array[String]) {
    var idlFile: File = null
    var idlIncludePaths: List[String] = List("")
    var cppOutFolder: Option[File] = None
    var cppNamespace: String = ""
    var cppIncludePrefix: String = ""
    var cppExtendedRecordIncludePrefix: String = ""
    var cppFileIdentStyle: IdentConverter = IdentStyle.underLower
    var cppOptionalTemplate: String = "std::optional"
    var cppOptionalHeader: String = "<optional>"
    var cppEnumHashWorkaround: Boolean = true
    var cppNnHeader: Option[String] = None
    var cppNnType: Option[String] = None
    var cppNnCheckExpression: Option[String] = None
    var cppUseWideStrings: Boolean = false
    var cppOmitDefaultRecordCtor: Boolean = false
    var javaOutFolder: Option[File] = None
    var javaPackage: Option[String] = None
    var javaClassAccessModifier: JavaAccessModifier.Value = JavaAccessModifier.Public
    var javaCppException: Option[String] = None
    var javaAnnotation: Option[String] = None
    var javaGenerateInterfaces: Boolean = false
    var javaNullableAnnotation: Option[String] = None
    var javaNonnullAnnotation: Option[String] = None
    var javaImplementAndroidOsParcelable: Boolean = false
    var javaUseFinalForRecord: Boolean = true
    var jniOutFolder: Option[File] = None
    var jniHeaderOutFolderOptional: Option[File] = None
    var jniNamespace: String = "djinni_generated"
    var jniClassIdentStyleOptional: Option[IdentConverter] = None
    var jniIncludePrefix: String = ""
    var jniIncludeCppPrefix: String = ""
    var jniFileIdentStyleOptional: Option[IdentConverter] = None
    var jniBaseLibClassIdentStyleOptional: Option[IdentConverter] = None
    var jniGenerateMain: Boolean = true
    var cppHeaderOutFolderOptional: Option[File] = None
    var cppExt: String = "cpp"
    var cppHeaderExt: String = "hpp"
    var javaIdentStyle = IdentStyle.javaDefault
    var cppIdentStyle = IdentStyle.cppDefault
    var cppTypeEnumIdentStyle: IdentConverter = null
    var objcOutFolder: Option[File] = None
    var objcHeaderOutFolderOptional: Option[File] = None
    var objcppOutFolder: Option[File] = None
    var objcppHeaderOutFolderOptional: Option[File] = None
    var objcppExt: String = "mm"
    var objcHeaderExt: String = "h"
    var objcIdentStyle = IdentStyle.objcDefault
    var objcTypePrefix: String = ""
    var objcIncludePrefix: String = ""
    var objcExtendedRecordIncludePrefix: String = ""
    var objcSwiftBridgingHeaderName: Option[String] = None
    var objcClosedEnums: Boolean = false
    var objcppIncludePrefix: String = ""
    var objcppIncludeCppPrefix: String = ""
    var objcppIncludeObjcPrefixOptional: Option[String] = None
    var objcFileIdentStyleOptional: Option[IdentConverter] = None
    var objcppNamespace: String = "djinni_generated"
    var cppCliOutFolder: Option[File] = None
    var cppCliIdentStyle = IdentStyle.csDefault
    var cppCliNamespace: String = ""
    var cppCliIncludeCppPrefix: String = ""
    var inFileListPath: Option[File] = None
    var outFileListPath: Option[File] = None
    var skipGeneration: Boolean = false
    var yamlOutFolder: Option[File] = None
    var yamlOutFile: Option[String] = None
    var yamlPrefix: String = ""
    var pyOutFolder: Option[File] = None
    var pyIdentStyle = IdentStyle.pythonDefault
    var cWrapperOutFolder: Option[File] = None
    var cWrapperHeaderOutFolderOptional: Option[File] = None
    var cWrapperIncludePrefix: String = ""
    var cWrapperIncludeCppPrefix: String = ""
    var pycffiPackageName: String = ""
    var pycffiDynamicLibList: String = ""
    var pycffiOutFolder: Option[File] = None
    var pyImportPrefix: String = ""

    val argParser: OptionParser[Unit] = new scopt.OptionParser[Unit]("djinni") {

      def identStyle(optionName: String, defaultValue: String , update: IdentConverter => Unit): OptionDef[String, Unit] = {
        opt[String](optionName).valueName("...").text(s"(default: $defaultValue)").foreach(spec =>
          IdentStyle.infer(spec) match {
            case None => failure("invalid ident spec: \"" + spec + "\"")
            case Some(func) => update(func)
          }
        )
      }

      override def showUsageOnError = Some(false)

      head("djinni generator version", Main.getClass.getPackage.getImplementationVersion)

      note("General")
      help("help")
      version("version")

      opt[File]("idl").valueName("<in-file>").required().foreach(idlFile = _)
        .text("The IDL file with the type definitions, typically with extension \".djinni\".")
      opt[String]("idl-include-path").valueName("<path> ...").optional().unbounded().foreach(x => idlIncludePaths = idlIncludePaths :+ x)
        .text("An include path to search for Djinni @import directives. Can specify multiple paths.")

      note("\nJava")
      opt[File]("java-out").valueName("<out-folder>").foreach(x => javaOutFolder = Some(x))
        .text("The output for the Java files (Generator disabled if unspecified).")
      opt[String]("java-package").valueName("...").foreach(x => javaPackage = Some(x))
        .text("The package name to use for generated Java classes.")
      opt[JavaAccessModifier.Value]("java-class-access-modifier").valueName("<public/package>").foreach(x => javaClassAccessModifier = x)
        .text("The access modifier to use for generated Java classes (default: public).")
      opt[String]("java-cpp-exception").valueName("<exception-class>").foreach(x => javaCppException = Some(x))
        .text("The type for translated C++ exceptions in Java (default: java.lang.RuntimeException that is not checked)")
      opt[String]("java-annotation").valueName("<annotation-class>").foreach(x => javaAnnotation = Some(x))
        .text("Java annotation (@Foo) to place on all generated Java classes")
      opt[Boolean]("java-generate-interfaces").valueName("<true/false>").foreach(x => javaGenerateInterfaces = x)
        .text("Whether Java interfaces should be used instead of abstract classes where possible (default: false).")
      opt[String]("java-nullable-annotation").valueName("<nullable-annotation-class>").foreach(x => javaNullableAnnotation = Some(x))
        .text("Java annotation (@Nullable) to place on all fields and return values that are optional")
      opt[String]("java-nonnull-annotation").valueName("<nonnull-annotation-class>").foreach(x => javaNonnullAnnotation = Some(x))
        .text("Java annotation (@Nonnull) to place on all fields and return values that are not optional")
      opt[Boolean]("java-implement-android-os-parcelable").valueName("<true/false>").foreach(x => javaImplementAndroidOsParcelable = x)
        .text("all generated java classes will implement the interface android.os.Parcelable")
      opt[Boolean]("java-use-final-for-record").valueName("<use-final-for-record>").foreach(x => javaUseFinalForRecord = x)
        .text("Whether generated Java classes for records should be marked 'final' (default: true). ")

      note("\nC++")
      opt[File]("cpp-out").valueName("<out-folder>").foreach(x => cppOutFolder = Some(x))
        .text("The output folder for C++ files (Generator disabled if unspecified).")
      opt[File]("cpp-header-out").valueName("<out-folder>").foreach(x => cppHeaderOutFolderOptional = Some(x))
        .text("The output folder for C++ header files (default: the same as --cpp-out).")
      opt[String]("cpp-include-prefix").valueName("<prefix>").foreach(cppIncludePrefix = _)
        .text("The prefix for #includes of header files from C++ files.")
      opt[String]("cpp-namespace").valueName("...").foreach(x => cppNamespace = x)
        .text("The namespace name to use for generated C++ classes.")
      opt[String]("cpp-ext").valueName("<ext>").foreach(cppExt = _)
        .text("The filename extension for C++ files (default: \"cpp\").")
      opt[String]("hpp-ext").valueName("<ext>").foreach(cppHeaderExt = _)
        .text("The filename extension for C++ header files (default: \"hpp\").")
      opt[String]("cpp-optional-template").valueName("<template>").foreach(x => cppOptionalTemplate = x)
        .text("The template to use for optional values (default: \"std::optional\")")
      opt[String]("cpp-optional-header").valueName("<header>").foreach(x => cppOptionalHeader = x)
        .text("The header to use for optional values (default: \"<optional>\")")
      opt[Boolean]("cpp-enum-hash-workaround").valueName("<true/false>").foreach(x => cppEnumHashWorkaround = x)
        .text("Work around LWG-2148 by generating std::hash specializations for C++ enums (default: true)")
      opt[String]("cpp-nn-header").valueName("<header>").foreach(x => cppNnHeader = Some(x))
        .text("The header to use for non-nullable pointers")
      opt[String]("cpp-nn-type").valueName("<header>").foreach(x => cppNnType = Some(x))
        .text("The type to use for non-nullable pointers (as a substitute for std::shared_ptr)")
      opt[String]("cpp-nn-check-expression").valueName("<header>").foreach(x => cppNnCheckExpression = Some(x))
        .text("The expression to use for building non-nullable pointers")
      opt[Boolean]( "cpp-use-wide-strings").valueName("<true/false>").foreach(x => cppUseWideStrings = x)
        .text("Use wide strings in C++ code (default: false)")
      opt[Boolean]("cpp-omit-default-record-constructor").valueName("<true/false>").foreach(x => cppOmitDefaultRecordCtor = x)
        .text("Omit the default constructor for records in C++ code (default: `false`)")

      note("\nJNI")
      opt[File]("jni-out").valueName("<out-folder>").foreach(x => jniOutFolder = Some(x))
        .text("The folder for the JNI C++ output files (Generator disabled if unspecified).")
      opt[File]("jni-header-out").valueName("<out-folder>").foreach(x => jniHeaderOutFolderOptional = Some(x))
        .text("The folder for the JNI C++ header files (default: the same as --jni-out).")
      opt[String]("jni-include-prefix").valueName("<prefix>").foreach(jniIncludePrefix = _)
        .text("The prefix for #includes of JNI header files from JNI C++ files.")
      opt[String]("jni-include-cpp-prefix").valueName("<prefix>").foreach(jniIncludeCppPrefix = _)
        .text("The prefix for #includes of the main header files from JNI C++ files.")
      opt[String]("jni-namespace").valueName("...").foreach(x => jniNamespace = x)
        .text("The namespace name to use for generated JNI C++ classes.")
      opt[Boolean]("jni-generate-main").valueName("<true/false>").foreach(x => jniGenerateMain = x)
        .text("Generate a source file (djinni_jni_main.cpp) that includes the default JNI_OnLoad & JNI_OnUnload implementation from the djinni-support-lib. (default: true)")

      note("\nObjective-C")
      opt[File]("objc-out").valueName("<out-folder>").foreach(x => objcOutFolder = Some(x))
        .text("The output folder for Objective-C files (Generator disabled if unspecified).")
      opt[File]("objc-header-out").valueName("<out-folder>").foreach(x => objcHeaderOutFolderOptional = Some(x))
        .text("The folder for the Objective-C header files (default: the same as --objc-out).")
      opt[String]("objc-h-ext").valueName("<ext>").foreach(objcHeaderExt = _)
        .text("The filename extension for Objective-C[++] header files (default: \"h\")")
      opt[String]("objc-type-prefix").valueName("<pre>").foreach(objcTypePrefix = _)
        .text("The prefix for Objective-C data types (usually two or three letters)")
      opt[String]("objc-include-prefix").valueName("<prefix>").foreach(objcIncludePrefix = _)
        .text("The prefix for #import of header files from Objective-C files.")
      opt[String]("objc-swift-bridging-header").valueName("<name>").foreach(x => objcSwiftBridgingHeaderName = Some(x))
        .text("The name of Objective-C Bridging Header used in XCode's Swift projects. The output folder is --objc-header-out.")
      opt[Boolean]("objc-closed-enums").valueName("<true/false>").foreach(x => objcClosedEnums = x)
        .text("All generated Objective-C enums will be NS_CLOSED_ENUM (default: false). ")

      note("\nObjective-C++")
      opt[File]("objcpp-out").valueName("<out-folder>").foreach(x => objcppOutFolder = Some(x))
        .text("The output folder for private Objective-C++ files (Generator disabled if unspecified).")
      opt[File]("objcpp-header-out").valueName("<out-folder>").foreach(x => objcppHeaderOutFolderOptional = Some(x))
        .text("The folder for the Objective-C++ header files (default: the same as --objcpp-out).")
      opt[String]("objcpp-ext").valueName("<ext>").foreach(objcppExt = _)
        .text("The filename extension for Objective-C++ files (default: \"mm\")")
      opt[String]("objcpp-include-prefix").valueName("<prefix>").foreach(objcppIncludePrefix = _)
        .text("The prefix for #import of Objective-C++ header files from Objective-C++ files.")
      opt[String]("objcpp-include-cpp-prefix").valueName("<prefix>").foreach(objcppIncludeCppPrefix = _)
        .text("The prefix for #include of the main C++ header files from Objective-C++ files.")
      opt[String]("objcpp-include-objc-prefix").valueName("<prefix>").foreach(x => objcppIncludeObjcPrefixOptional = Some(x))
        .text("The prefix for #import of the Objective-C header files from Objective-C++ files (default: the same as --objcpp-include-prefix)")
      opt[String]("cpp-extended-record-include-prefix").valueName("<prefix>").foreach(cppExtendedRecordIncludePrefix = _)
        .text("The prefix path for #include of the extended record C++ header (.hpp) files")
      opt[String]("objc-extended-record-include-prefix").valueName("<prefix>").foreach(objcExtendedRecordIncludePrefix = _)
        .text("The prefix path for #import of the extended record Objective-C header (.h) files")
      opt[String]("objcpp-namespace").valueName("<prefix>").foreach(objcppNamespace = _)
        .text("The namespace name to use for generated Objective-C++ classes.")

      note("\nPython")
      opt[File]("py-out").valueName("<out-folder>").foreach(x => pyOutFolder = Some(x))
        .text("The output folder for Python files (Generator disabled if unspecified).")
      opt[File]("pycffi-out").valueName("<out-folder>").foreach(x => pycffiOutFolder = Some(x))
        .text("The output folder for PyCFFI files (Generator disabled if unspecified).")
      opt[String]("pycffi-package-name").valueName("...").foreach(x => pycffiPackageName= x)
        .text("The package name to use for the generated PyCFFI classes.")
      opt[String]("pycffi-dynamic-lib-list").valueName("...").foreach(x => pycffiDynamicLibList= x)
        .text("The names of the dynamic libraries to be linked with PyCFFI.")
      opt[String]("py-import-prefix").valueName("<import-prefix>").foreach(pyImportPrefix = _)
        .text("The import prefix used within python generated files (default: \"\")")

      note("\nC wrapper")
      opt[File]("c-wrapper-out").valueName("<out-folder>").foreach(x => cWrapperOutFolder = Some(x))
        .text("The output folder for C wrapper files (Generator disabled if unspecified).")
      opt[File]("c-wrapper-header-out").valueName("<out-folder>").foreach(x => cWrapperHeaderOutFolderOptional = Some(x))
        .text("The output folder for C wrapper header files (default: the same as --c-wrapper-out).")
      opt[String]("c-wrapper-include-prefix").valueName("<prefix>").foreach(x => cWrapperIncludePrefix = x)
        .text("The prefix for #includes of C wrapper header files from C wrapper C++ files.")
      opt[String]("c-wrapper-include-cpp-prefix").valueName("<prefix>").foreach(x => cWrapperIncludeCppPrefix = x)
        .text("The prefix for #includes of C++ header files from C wrapper C++ files.")

      note("\nC++/CLI")
      opt[File]("cppcli-out").valueName("<out-folder>").foreach(x => cppCliOutFolder = Some(x))
        .text("The output folder for C++/CLI files (Generator disabled if unspecified).")
      opt[String]("cppcli-namespace").valueName("...").foreach(cppCliNamespace = _)
        .text("The namespace name to use for generated C++/CLI classes.")
      opt[String]("cppcli-include-cpp-prefix").valueName("<prefix>").foreach(x => cppCliIncludeCppPrefix = x)
        .text("The prefix for #include of the main C++ header files from C++/CLI files.")

      note("\nYaml Generation")
      opt[File]("yaml-out").valueName("<out-folder>").foreach(x => yamlOutFolder = Some(x))
        .text("The output folder for YAML files (Generator disabled if unspecified).")
      opt[String]("yaml-out-file").valueName("<out-file>").foreach(x => yamlOutFile = Some(x))
        .text("If specified all types are merged into a single YAML file instead of generating one file per type (relative to --yaml-out).")
      opt[String]("yaml-prefix").valueName("<pre>").foreach(yamlPrefix = _)
        .text("The prefix to add to type names stored in YAML files (default: \"\").")

      note("\nOther")
      opt[File]("list-in-files").valueName("<list-in-files>").foreach(x => inFileListPath = Some(x))
        .text("Optional file in which to write the list of input files parsed.")
      opt[File]("list-out-files").valueName("<list-out-files>").foreach(x => outFileListPath = Some(x))
        .text("Optional file in which to write the list of output files produced.")
      opt[Boolean]("skip-generation").valueName("<true/false>").foreach(x => skipGeneration = x)
        .text("Way of specifying if file generation should be skipped (default: false)")

      note("\n\nIdentifier styles (ex: \"FooBar\", \"fooBar\", \"foo_bar\", \"FOO_BAR\", \"m_fooBar\")")

      note("\nC++ options:")
      identStyle("ident-cpp-enum",        "FOO_BAR",  c => { cppIdentStyle = cppIdentStyle.copy(enum = c) })
      identStyle("ident-cpp-field",       "foo_bar",  c => { cppIdentStyle = cppIdentStyle.copy(field = c) })
      identStyle("ident-cpp-method",      "foo_bar",  c => { cppIdentStyle = cppIdentStyle.copy(method = c) })
      identStyle("ident-cpp-type",        "FooBar",   c => { cppIdentStyle = cppIdentStyle.copy(ty = c) })
      identStyle("ident-cpp-enum-type",   "FooBar",   c => { cppTypeEnumIdentStyle = c })
      identStyle("ident-cpp-type-param",  "FooBar",   c => { cppIdentStyle = cppIdentStyle.copy(typeParam = c) })
      identStyle("ident-cpp-local",       "foo_bar",  c => { cppIdentStyle = cppIdentStyle.copy(local = c) })
      identStyle("ident-cpp-file",        "foo_bar",  c => { cppFileIdentStyle = c })

      note("\nJava and JNI options:")
      identStyle("ident-java-enum",    "FOO_BAR",  c => { javaIdentStyle = javaIdentStyle.copy(enum = c) })
      identStyle("ident-java-field",   "fooBar",   c => { javaIdentStyle = javaIdentStyle.copy(field = c) })
      identStyle("ident-java-type",    "FooBar",   c => { javaIdentStyle = javaIdentStyle.copy(ty = c) })
      identStyle("ident-jni-class",    "FooBar",   c => { jniClassIdentStyleOptional = Some(c)})
      identStyle("ident-jni-file",     "foo_bar",  c => { jniFileIdentStyleOptional = Some(c)})

      note("\nObjective-C options:")
      identStyle("ident-objc-enum",        "FooBar",  c => { objcIdentStyle = objcIdentStyle.copy(enum = c) })
      identStyle("ident-objc-field",       "fooBar",  c => { objcIdentStyle = objcIdentStyle.copy(field = c) })
      identStyle("ident-objc-method",      "fooBar",  c => { objcIdentStyle = objcIdentStyle.copy(method = c) })
      identStyle("ident-objc-type",        "FooBar",  c => { objcIdentStyle = objcIdentStyle.copy(ty = c) })
      identStyle("ident-objc-type-param",  "FooBar",  c => { objcIdentStyle = objcIdentStyle.copy(typeParam = c) })
      identStyle("ident-objc-local",       "fooBar",  c => { objcIdentStyle = objcIdentStyle.copy(local = c) })
      identStyle("ident-objc-file",        "FooBar",  c => { objcFileIdentStyleOptional = Some(c) })

      note("\nPython options:")
      identStyle("ident-py-type",        "foo_bar",  c => { pyIdentStyle = pyIdentStyle.copy(ty = c) })
      identStyle("ident-py-class-name",  "FooBar",   c => { pyIdentStyle = pyIdentStyle.copy(className = c) })
      identStyle("ident-py-type-param",  "foo_bar",  c => { pyIdentStyle = pyIdentStyle.copy(typeParam = c) })
      identStyle("ident-py-method",      "foo_bar",  c => { pyIdentStyle = pyIdentStyle.copy(method = c) })
      identStyle("ident-py-field",       "foo_bar",  c => { pyIdentStyle = pyIdentStyle.copy(field = c) })
      identStyle("ident-py-local",       "foo_bar",  c => { pyIdentStyle = pyIdentStyle.copy(local = c) })
      identStyle("ident-py-enum",        "Foo_Bar",  c => { pyIdentStyle = pyIdentStyle.copy(enum = c) })
      identStyle("ident-py-const",       "FOO_BAR",  c => { pyIdentStyle = pyIdentStyle.copy(const = c) })

      note("\nC++/CLI options:")
      identStyle("ident-cppcli-type", "FooBar", c => { cppCliIdentStyle = cppCliIdentStyle.copy(ty = c) })
      identStyle("ident-cppcli-type-param", "FooBar", c => { cppCliIdentStyle = cppCliIdentStyle.copy(typeParam = c) })
      identStyle("ident-cppcli-property", "FooBar", c => { cppCliIdentStyle = cppCliIdentStyle.copy(property = c) })
      identStyle("ident-cppcli-method", "FooBar", c => { cppCliIdentStyle = cppCliIdentStyle.copy(method = c) })
      identStyle("ident-cppcli-local", "fooBar", c => { cppCliIdentStyle = cppCliIdentStyle.copy(local = c) })
      identStyle("ident-cppcli-enum", "FooBar", c => { cppCliIdentStyle = cppCliIdentStyle.copy(enum = c) })
      identStyle("ident-cppcli-const", "FooBar", c => { cppCliIdentStyle = cppCliIdentStyle.copy(const = c) })
      identStyle("ident-cppcli-file", "FooBar", c => { cppCliIdentStyle = cppCliIdentStyle.copy(file = c) })
    }

    if (argParser.parse(args, ()).isEmpty) {
      System.exit(1)
      return
    }

    val cppHeaderOutFolder = if (cppHeaderOutFolderOptional.isDefined) cppHeaderOutFolderOptional else cppOutFolder
    val jniHeaderOutFolder = if (jniHeaderOutFolderOptional.isDefined) jniHeaderOutFolderOptional else jniOutFolder
    val objcHeaderOutFolder = if (objcHeaderOutFolderOptional.isDefined) objcHeaderOutFolderOptional else objcOutFolder
    val objcppHeaderOutFolder = if (objcppHeaderOutFolderOptional.isDefined) objcppHeaderOutFolderOptional else objcppOutFolder
    val cWrapperHeaderOutFolder = if (cWrapperHeaderOutFolderOptional.isDefined) cWrapperHeaderOutFolderOptional else cWrapperOutFolder
    val jniClassIdentStyle = jniClassIdentStyleOptional.getOrElse(cppIdentStyle.ty)
    val jniBaseLibClassIdentStyle = jniBaseLibClassIdentStyleOptional.getOrElse(jniClassIdentStyle)
    val jniFileIdentStyle = jniFileIdentStyleOptional.getOrElse(cppFileIdentStyle)
    var objcFileIdentStyle = objcFileIdentStyleOptional.getOrElse(objcIdentStyle.ty)
    val objcppIncludeObjcPrefix = objcppIncludeObjcPrefixOptional.getOrElse(objcppIncludePrefix)

    // Add ObjC prefix to identstyle
    objcIdentStyle = objcIdentStyle.copy(ty = IdentStyle.prefix(objcTypePrefix,objcIdentStyle.ty))
    objcFileIdentStyle = IdentStyle.prefix(objcTypePrefix, objcFileIdentStyle)

    if (cppTypeEnumIdentStyle != null) {
      cppIdentStyle = cppIdentStyle.copy(enumType = cppTypeEnumIdentStyle)
    }

    // Parse IDL file.
    System.out.println("Parsing...")
    val inFileListWriter = if (inFileListPath.isDefined) {
      if (inFileListPath.get.getParentFile != null)
        createFolder("input file list", inFileListPath.get.getParentFile)
      Some(new BufferedWriter(new FileWriter(inFileListPath.get)))
    } else {
      None
    }
    val idl = try {
      Parser(idlIncludePaths).parseFile(idlFile, inFileListWriter)
    }
    catch {
      case ex @ (_: FileNotFoundException | _: IOException) =>
        System.err.println("Error reading from --idl file: " + ex.getMessage)
        System.exit(1); return
    }
    finally {
      if (inFileListWriter.isDefined) {
        inFileListWriter.get.close()
      }
    }

    // Ensure either --cpp-namespace or --objc-type-prefix are given when Objective-C is generated
    if(objcOutFolder.isDefined) {
      if (cppNamespace.isEmpty() && objcTypePrefix.isEmpty()) {
          System.err.println("Error: At least one of [--cpp-namespace, --objc-type-prefix] needs to be set when generating Objective-C code.")
          System.exit(1); return
      }
    }

    // Resolve names in IDL file, check types.
    System.out.println("Resolving...")
    resolver.resolve(
      meta.defaults,
      idl,
      cppOutRequired = cppOutFolder.isDefined,
      objcOutRequired = objcOutFolder.isDefined,
      objcppOutRequired = objcppOutFolder.isDefined,
      javaOutRequired = javaOutFolder.isDefined,
      jniOutRequired = jniOutFolder.isDefined,
      cppCliOutRequired = cppCliOutFolder.isDefined
    ) match {
      case Some(err) =>
        System.err.println(err)
        System.exit(1); return
      case _ =>
    }

    System.out.println("Generating...")
    val outFileListWriter = if (outFileListPath.isDefined) {
      if (outFileListPath.get.getParentFile != null)
        createFolder("output file list", outFileListPath.get.getParentFile)
      Some(new BufferedWriter(new FileWriter(outFileListPath.get)))
    } else {
      None
    }
    val objcSwiftBridgingHeaderWriter = if (objcSwiftBridgingHeaderName.isDefined && objcOutFolder.isDefined && !skipGeneration) {
      val objcSwiftBridgingHeaderFile = new File(objcHeaderOutFolder.get.getPath, objcSwiftBridgingHeaderName.get + ".h")
      if (objcSwiftBridgingHeaderFile.getParentFile != null)
        createFolder("output file list", objcSwiftBridgingHeaderFile.getParentFile)
      Some(new BufferedWriter(new FileWriter(objcSwiftBridgingHeaderFile)))
    } else {
      None
    }

    val outSpec = Spec(
      javaOutFolder,
      javaPackage,
      javaClassAccessModifier,
      javaIdentStyle,
      javaCppException,
      javaAnnotation,
      javaGenerateInterfaces,
      javaNullableAnnotation,
      javaNonnullAnnotation,
      javaImplementAndroidOsParcelable,
      javaUseFinalForRecord,
      cppOutFolder,
      cppHeaderOutFolder,
      cppIncludePrefix,
      cppExtendedRecordIncludePrefix,
      cppNamespace,
      cppIdentStyle,
      cppFileIdentStyle,
      cppOptionalTemplate,
      cppOptionalHeader,
      cppEnumHashWorkaround,
      cppNnHeader,
      cppNnType,
      cppNnCheckExpression,
      cppUseWideStrings,
      cppOmitDefaultRecordCtor,
      jniOutFolder,
      jniHeaderOutFolder,
      jniIncludePrefix,
      jniIncludeCppPrefix,
      jniNamespace,
      jniClassIdentStyle,
      jniFileIdentStyle,
      jniGenerateMain,
      cppExt,
      cppHeaderExt,
      objcOutFolder,
      objcHeaderOutFolder,
      objcppOutFolder,
      objcppHeaderOutFolder,
      objcIdentStyle,
      objcFileIdentStyle,
      objcppExt,
      objcHeaderExt,
      objcIncludePrefix,
      objcExtendedRecordIncludePrefix,
      objcppIncludePrefix,
      objcppIncludeCppPrefix,
      objcppIncludeObjcPrefix,
      objcppNamespace,
      objcSwiftBridgingHeaderWriter,
      cppCliOutFolder,
      cppCliIdentStyle,
      cppCliNamespace,
      cppCliIncludeCppPrefix,
      objcSwiftBridgingHeaderName,
      objcClosedEnums,
      outFileListWriter,
      skipGeneration,
      yamlOutFolder,
      yamlOutFile,
      yamlPrefix,
      pyOutFolder,
      pyIdentStyle,
      pycffiOutFolder,
      pycffiPackageName,
      pycffiDynamicLibList,
      idlFile.getName(),
      cWrapperOutFolder,
      cWrapperHeaderOutFolder,
      cWrapperIncludePrefix,
      cWrapperIncludeCppPrefix,
      pyImportPrefix)

    try {
      val r = generate(idl, outSpec)
      r.foreach(e => System.err.println("Error generating output: " + e))
    } finally {
      if (outFileListWriter.isDefined) {
        outFileListWriter.get.close()
      }
      if (objcSwiftBridgingHeaderWriter.isDefined) {
        objcSwiftBridgingHeaderWriter.get.close()
      }
    }
  }
}
