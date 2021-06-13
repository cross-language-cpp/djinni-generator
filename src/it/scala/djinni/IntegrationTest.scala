package djinni

import org.scalatest.FunSpec
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper, equal, noException}

import scala.io.Source
import scala.sys.process._

/**
  * Base class for integration tests, providing a few handy helper functions
  */
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

  /**
    * Executes the djinni generator with the given parameters
    * @param parameters parameters that should be passed to the process
    * @return command-line output of the executed djinni-cli
    */
  def djinni(parameters: String): String = {
    "target/bin/djinni " + parameters !!
  }

  /**
    * Executes the djinni generator with the given idl-file as input
    * @param idl filename of the djinni-file (without file extension). The file must be located in the
    *            `resources`-folder of the integration-tests (`src/it/resources`)
    * @return command-line output of the executed djinni-cli
    */
  def djinniGenerate(idl: String): String = {
    val baseOutputPath = "src/it/resources/result"
    djinni("--java-package djinni.it " +
      s"--java-out $baseOutputPath/$idl/$JAVA " +
      s"--cpp-out $baseOutputPath/$idl/$CPP " +
      s"--cpp-header-out $baseOutputPath/$idl/$CPP_HEADERS " +
      s"--jni-out $baseOutputPath/$idl/$JNI " +
      s"--jni-header-out $baseOutputPath/$idl/$JNI_HEADERS " +
      s"--objc-out $baseOutputPath/$idl/$OBJC " +
      s"--objc-header-out $baseOutputPath/$idl/$OBJC_HEADERS " +
      "--objc-swift-bridging-header bridging-header " +
      "--objc-type-prefix IT " +
      s"--objcpp-out $baseOutputPath/$idl/$OBJCPP " +
      s"--objcpp-header-out $baseOutputPath/$idl/$OBJCPP_HEADERS " +
      s"--py-out $baseOutputPath/$idl/python " +
      s"--pycffi-out $baseOutputPath/$idl/cffi " +
      s"--c-wrapper-out $baseOutputPath/$idl/$CWRAPPER " +
      s"--c-wrapper-header-out $baseOutputPath/$idl/$CWRAPPER_HEADERS " +
      s"--c-wrapper-include-prefix ../$CWRAPPER_HEADERS/ " +
      s"--c-wrapper-include-cpp-prefix ../$CPP_HEADERS/ " +
      s"--cppcli-out $baseOutputPath/$idl/$CPPCLI " +
      s"--cppcli-include-cpp-prefix ../$CPP_HEADERS/ " +
      s"--list-out-files $baseOutputPath/$idl/generated-files.txt " +
      s"--idl src/it/resources/$idl.djinni")
  }

  /**
    * Asserts that all expected files have been created & have the expected content. It basically compares the contents
    * of the generator output in `resources/result/$lang` with the expectations defined in `resources/expected/$lang`.
    * @param idl filename of the input-idl (without file extension)
    * @param lang language to assert for (e.g. `cpp`, `cpp-headers`, `java`, `jni`, `jni-headers`, `objc`, `objc-headers`, `objcpp`, `objcpp-headers`)
    * @param filenames list of expected filenames that should have been generated for the given language
    */
  def assertFileContentEquals(idl: String, lang: String, filenames: List[String]): Unit = {
    for(filename <- filenames) {
      val resultFile = Source.fromFile(s"src/it/resources/result/$idl/$lang/$filename")
      val expectationFile = Source.fromFile(s"src/it/resources/expected/$idl/$lang/$filename")
      resultFile.mkString should equal (expectationFile.mkString)
      resultFile.close()
      expectationFile.close()
    }
  }

  def assertFileExists(filename: String): Unit = {
      noException should be thrownBy Source.fromFile(filename)
  }

}
