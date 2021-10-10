package djinni

import org.scalatest._
import matchers.should.Matchers._
import org.scalatest.funspec._
import org.scalatest.FunSpec

import scala.io.Source
import scala.sys.process._

import scala.reflect.io.Directory
import java.io.File

// Base class for integration tests, providing a few handy helper functions
class IntegrationTest extends FunSpec {
  final val CPP = "cpp"
  final val CPP_HEADERS = "cpp-headers"
  final val JAVA = "java"
  final val JNI = "jni"
  final val JNI_HEADERS = "jni-headers"
  final val OBJC = "objc"
  final val OBJC_HEADERS = "objc-headers"
  final val OBJCPP = "objcpp"
  final val OBJCPP_HEADERS = "objcpp-headers"
  final val PY = "python"
  final val CFFI = "cffi"
  final val CWRAPPER = "cwrapper"
  final val CWRAPPER_HEADERS = "cwrapper-headers"
  final val CPPCLI = "cppcli"

  type Cpp = List[String]
  def Cpp(params: String*) = List(params: _*)
  type CppHeaders = List[String]
  def CppHeaders(params: String*) = List(params: _*)
  type Java = List[String]
  def Java(params: String*) = List(params: _*)
  type Jni = List[String]
  def Jni(params: String*) = List(params: _*)
  type JniHeaders = List[String]
  def JniHeaders(params: String*) = List(params: _*)
  type ObjC = List[String]
  def ObjC(params: String*) = List(params: _*)
  type ObjCHeaders = List[String]
  def ObjCHeaders(params: String*) = List(params: _*)
  type ObjCpp = List[String]
  def ObjCpp(params: String*) = List(params: _*)
  type ObjCppHeaders = List[String]
  def ObjCppHeaders(params: String*) = List(params: _*)
  type Python = List[String]
  def Python(params: String*) = List(params: _*)
  type PyCffi = List[String]
  def PyCffi(params: String*) = List(params: _*)
  type CWrapper = List[String]
  def CWrapper(params: String*) = List(params: _*)
  type CWrapperHeaders = List[String]
  def CWrapperHeaders(params: String*) = List(params: _*)
  type CppCli = List[String]
  def CppCli(params: String*) = List(params: _*)

  /** Executes the djinni generator with the given parameters
    * @param parameters
    *   parameters that should be passed to the process
    * @return
    *   command-line output of the executed djinni-cli
    */
  def djinni(parameters: String): String = {
    "target/bin/djinni " + parameters !!
  }

  /** Generates the command line parameters to pass to the djinni generator.
    *
    * @param idl
    *   filename of the djinni-file (without file extension). The file must be
    *   located in the `resources`-folder of the integration-tests
    *   (`src/it/resources`)
    * @param baseOutputPath
    *   The root folder for the outputs to be generated.
    * @param cpp
    *   Whether to generate C++ output. Default: true.
    * @param java
    *   Whether to generate Java output. Default: true.
    * @param objc
    *   Whether to generate Objective C output. Default: true.
    * @param python
    *   Whether to generate Python output. Default: true.
    * @param cWrapper
    *   Whether to generate Python output. Default: true.
    * @param cppCLI
    *   Whether to generate Python output. Default: true.
    * @param useNNHeader
    *   Whether to use the nn.hpp header for non-null pointers. Default: false.
    *
    * @return
    *   command line params to pass to the djinni generator.
    */
  def djinniParams(
      idl: String,
      baseOutputPath: String =
        "src/it/resources/result", // this should never change, see removeTestOutputDirectory, and it is also used on other locations
      cpp: Boolean = true,
      java: Boolean = true,
      objc: Boolean = true,
      python: Boolean = true,
      cWrapper: Boolean = true,
      cppCLI: Boolean = true,
      useNNHeader: Boolean = false,
      cppOmitDefaultRecordCtor: Boolean = false
  ): String = {
    var cmd = s"--idl src/it/resources/$idl.djinni"
    if (cpp) {
      cmd += s" --cpp-out $baseOutputPath/$idl/$CPP"
      cmd += s" --cpp-header-out $baseOutputPath/$idl/$CPP_HEADERS"
    }
    if (java) {
      cmd += " --java-package djinni.it"
      cmd += s" --java-out $baseOutputPath/$idl/$JAVA"
      cmd += s" --jni-out $baseOutputPath/$idl/$JNI"
      cmd += s" --jni-header-out $baseOutputPath/$idl/$JNI_HEADERS"
    }
    if (objc) {
      cmd += s" --objc-out $baseOutputPath/$idl/$OBJC"
      cmd += s" --objc-header-out $baseOutputPath/$idl/$OBJC_HEADERS"
      cmd += " --objc-swift-bridging-header bridging-header"
      cmd += " --objc-type-prefix IT"
      cmd += s" --objcpp-out $baseOutputPath/$idl/$OBJCPP"
      cmd += s" --objcpp-header-out $baseOutputPath/$idl/$OBJCPP_HEADERS"
    }
    if (python) {
      cmd += s" --py-out $baseOutputPath/$idl/python"
      cmd += s" --pycffi-out $baseOutputPath/$idl/cffi"
    }
    if (cWrapper) {
      cmd += s" --c-wrapper-out $baseOutputPath/$idl/$CWRAPPER"
      cmd += s" --c-wrapper-header-out $baseOutputPath/$idl/$CWRAPPER_HEADERS"
      cmd += s" --c-wrapper-include-prefix ../$CWRAPPER_HEADERS/"
      cmd += s" --c-wrapper-include-cpp-prefix ../$CPP_HEADERS/"
    }
    if (cppCLI) {
      cmd += s" --cppcli-out $baseOutputPath/$idl/$CPPCLI"
      cmd += s" --cppcli-include-cpp-prefix ../$CPP_HEADERS/"
    }
    if (useNNHeader) {
      cmd += " --cpp-nn-header nn.hpp"
      cmd += " --cpp-nn-type dropbox::oxygen::nn_shared_ptr"
      cmd += " --cpp-nn-check-expression NN_CHECK_ASSERT"
    }
    if (cppOmitDefaultRecordCtor) {
      cmd += " --cpp-omit-default-record-constructor true"
    }
    cmd += s" --list-out-files $baseOutputPath/$idl/generated-files.txt"
    return cmd
  }

  /** Executes the djinni generator with the given idl-file as input.
    *
    * @param idl
    *   filename of the djinni-file (without file extension). The file must be
    *   located in the `resources`-folder of the integration-tests
    *   (`src/it/resources`)
    * @return
    *   command-line output of the executed djinni-cli
    */
  def djinniGenerate(idl: String): String = {
    return djinni(djinniParams(idl))
  }

  /** Asserts that all expected files have been created & have the expected
    * content. It basically compares the contents of the generator output in
    * `resources/result/$lang` with the expectations defined in
    * `resources/expected/$lang`.
    * @param idl
    *   filename of the input-idl (without file extension)
    * @param lang
    *   language to assert for (e.g. `cpp`, `cpp-headers`, `java`, `jni`,
    *   `jni-headers`, `objc`, `objc-headers`, `objcpp`, `objcpp-headers`)
    * @param filenames
    *   list of expected filenames that should have been generated for the given
    *   language
    */
  def assertFileContentEquals(
      idl: String,
      lang: String,
      filenames: List[String]
  ): Unit = {
    for (filename <- filenames) {
      val resultFile =
        Source.fromFile(s"src/it/resources/result/$idl/$lang/$filename")
      val expectationFile =
        Source.fromFile(s"src/it/resources/expected/$idl/$lang/$filename")
      resultFile.mkString should equal(expectationFile.mkString)
      resultFile.close()
      expectationFile.close()
    }
  }

  def removeTestOutputDirectory(
      baseOutputPath: String = "src/it/resources/result"
  ) {
    val directory = new Directory(new File(baseOutputPath))
    if (directory.deleteRecursively()) {
      System.console.printf(
        "[info] Clean up old generated test output/files.\n"
      )
    }
  }

  def assertFileExists(filename: String): Unit = {
    noException should be thrownBy Source.fromFile(filename)
  }

}
