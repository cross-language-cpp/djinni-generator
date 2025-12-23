package djinni

import org.scalatest._
import matchers.should.Matchers._
import org.scalatest.funspec._
import scala.language.postfixOps

import scala.io.Source
import scala.sys.process._

import scala.reflect.io.Directory
import java.io.File
//import java.nio.file.Paths

// Base class for integration tests, providing a few handy helper functions
class IntegrationTest extends AnyFunSpec {
  final val CPP = "cpp"
  final val CPP_HEADERS = "cpp-headers"
  final val JAVA = "java"
  final val JNI = "jni"
  final val JNI_HEADERS = "jni-headers"
  final val OBJC = "objc"
  final val OBJC_HEADERS = "objc-headers"
  final val OBJCPP = "objcpp"
  final val OBJCPP_HEADERS = "objcpp-headers"

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

  /** Executes the djinni generator with the given parameters
    * @param parameters
    *   parameters that should be passed to the process
    * @return
    *   command-line output of the executed djinni-cli
    */
  def djinni(parameters: String): String = {
    val binExt =
      if (System.getProperty("os.name").startsWith("Windows")) ".bat" else ""
    val sbtBinary = "target/bin/djinni" + binExt

    if (new java.io.File(sbtBinary).isFile) {
      // assume this is a sbt it:test.
      // this expect that sbt assembly has been executed, and the executable is there
      toUnixLineSeparator(sbtBinary + " " + parameters !!)
    } else if (new java.io.File("bazel-bin/djinni").isFile) {
      // bazel build, or anything else. Expect to find djinni in bazel-bin directory
      toUnixLineSeparator("bazel-bin/djinni" + binExt + " " + parameters !!)
    } else if (new java.io.File("djinni" + binExt).isFile) {
      // fallback: djinni in current directory
      toUnixLineSeparator("djinni" + binExt + " " + parameters !!)
    } else {
      fail("djinni executable not found")
    }
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
    *
    * @param useNNHeader
    *   Whether to use the nn.hpp header for non-null pointers. Default: false.
    * @param cppJsonSerialization
    *   Specify JSON serializer library for c++ output. Default: None
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
      useNNHeader: Boolean = false,
      cppOmitDefaultRecordCtor: Boolean = false,
      cppJsonSerialization: Option[String] = None
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

    if (useNNHeader) {
      cmd += " --cpp-nn-header nn.hpp"
      cmd += " --cpp-nn-type dropbox::oxygen::nn_shared_ptr"
      cmd += " --cpp-nn-check-expression NN_CHECK_ASSERT"
    }
    if (cppOmitDefaultRecordCtor) {
      cmd += " --cpp-omit-default-record-constructor true"
    }
    if (cppJsonSerialization.isDefined) {
      cmd += s" --cpp-json-serialization ${cppJsonSerialization.get}"
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

  /** Transform the string generated on Windows to Linux format in order to
    * compare it with the test data generated on Linux.
    * @param str
    *   the string generated on Windows to be transformed
    * @return
    *   the string that \r is removed and \\ is replaced by / from str
    */
  def toUnixLineSeparator(
      str: String
  ): String = {
    if (System.lineSeparator != "\n") {
      str.replace(System.lineSeparator, "\n")
    } else {
      str
    }
  }

  /** Asserts that all expected files have been created & have the expected
    * content. It basically compares the contents of the generator output in
    * `resources/result/$idl/$lang/$filename` with the expectations defined in
    * `resources/expected/$idl/$lang/$filename`.
    * @param idl
    *   filename of the input-idl (without file extension)
    * @param lang
    *   language to assert for (e.g. `cpp`, `cpp-headers`, `java`, `jni`,
    *   `jni-headers`, `objc`, `objc-headers`, `objcpp`, `objcpp-headers`)
    * @param filenames
    *   list of expected filenames that should have been generated for the given
    *   language
    * @param lineTransformation
    *   function to transform a line, e.g. to transform the line to a Path type
    */
  def assertFileContentEquals(
      idl: String,
      lang: String,
      filenames: List[String],
      lineTransformation: String => Any = (s: String) => s
  ): Unit = {
    for (filename <- filenames) {
      val resultFile =
        Source.fromFile(s"src/it/resources/result/$idl/$lang/$filename")
      val expectationFile =
        Source.fromFile(s"src/it/resources/expected/$idl/$lang/$filename")
      val resultFileLines = resultFile.getLines
      val expectationFileLines = expectationFile.getLines
      for (
        (result, expectation) <- resultFileLines.zipAll(
          expectationFileLines,
          "",
          ""
        )
      ) {
        lineTransformation(result) should equal(lineTransformation(expectation))
      }

      resultFile.close()
      expectationFile.close()
    }
  }

  def removeTestOutputDirectory(
      baseOutputPath: String = "src/it/resources/result"
  ) {
    val directory = new Directory(new File(baseOutputPath))
    if (directory.deleteRecursively()) {
      Option(System.console) match {
        case Some(console) =>
          console.printf(
            "[info] Clean up old generated test output/files.\n"
          )
        case None => println("[info] Clean up old generated test output/files.")
      }
    }
  }

  def assertFileExists(filename: String): Unit = {
    noException should be thrownBy Source.fromFile(filename)
  }

}
