package djinni

import org.scalatest.FunSpec
import org.scalatest.Matchers.{convertToAnyShouldWrapper, equal}

import scala.io.Source
import scala.sys.process._

class IntegrationTest extends FunSpec {
  final val CPP = "cpp"
  final val JAVA = "java"
  final val JNI = "jni"
  final val OBJC = "objc"
  final val OBJCPP = "objcpp"

  type Cpp = List[String]
  def Cpp(params: String*) = List(params: _*)
  type Java = List[String]
  def Java(params: String*) = List(params: _*)
  type Jni = List[String]
  def Jni(params: String*) = List(params: _*)
  type ObjC = List[String]
  def ObjC(params: String*) = List(params: _*)
  type ObjCpp = List[String]
  def ObjCpp(params: String*) = List(params: _*)

  /**
    * Executes the djinni generator with the given idl-file as input
    * @param idl filename of the djinni-file (without file extension). The file must be located in the
    *            `resources`-folder of the integration-tests (`src/it/resources`)
    * @return commandline-output of the executed djinni-cli
    */
  def djinniGenerate(idl: String): String = {
    return "target/bin/djinni " +
      "--java-package djinni.it " +
      s"--java-out src/it/resources/result/$idl/java " +
      s"--cpp-out src/it/resources/result/$idl/cpp " +
      s"--jni-out src/it/resources/result/$idl/jni " +
      s"--objc-out src/it/resources/result/$idl/objc " +
      "--objc-type-prefix IT " +
      s"--objcpp-out src/it/resources/result/$idl/objcpp " +
      s"--idl src/it/resources/$idl.djinni" !!
  }

  /**
    * Asserts that all expected files have been created & have the expected content. It basically compares the contents
    * of the generator output in `resources/result/$lang` with the expectations defined in `resources/expected/$lang`.
    * @param idl filename of the input-idl (without file extension)
    * @param lang language to assert for (e.g. `cpp`, `java`, `jni`, `objc`, `objcpp`)
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

}
