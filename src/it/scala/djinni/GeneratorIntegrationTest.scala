package djinni

import org.scalatest.GivenWhenThen
import org.scalatest.Matchers.{convertToAnyShouldWrapper, equal}
import org.scalatest.prop.TableDrivenPropertyChecks._

class GeneratorIntegrationTest extends IntegrationTest with GivenWhenThen {

  describe("djinni file generation") {
    val djinniTypes = Table(
      ("idlFile",
        "cppFilenames",
        "javaFilenames",
        "jniFilenames",
        "objcFilenames",
        "objcppFilenames"),
      ("my_enum",
        Cpp("my_enum.hpp"),
        Java("MyEnum.java"),
        Jni("my_enum.hpp"),
        ObjC("ITMyEnum.h"),
        ObjCpp("ITMyEnum+Private.h")),
      ("my_flags",
        Cpp("my_flags.hpp"),
        Java("MyFlags.java"),
        Jni("my_flags.hpp"),
        ObjC("ITMyFlags.h"),
        ObjCpp("ITMyFlags+Private.h")),
      ("my_record",
        Cpp("my_record.hpp", "my_record.cpp"),
        Java("MyRecord.java"),
        Jni("my_record.hpp", "my_record.cpp"),
        ObjC("ITMyRecord.h", "ITMyRecord.mm"),
        ObjCpp("ITMyRecord+Private.h", "ITMyRecord+Private.mm")),
      ("my_cpp_interface",
        Cpp("my_cpp_interface.hpp", "my_cpp_interface.cpp"),
        Java("MyCppInterface.java"),
        Jni("my_cpp_interface.hpp", "my_cpp_interface.cpp"),
        ObjC("ITMyCppInterface.h", "ITMyCppInterface.mm"),
        ObjCpp("ITMyCppInterface+Private.h", "ITMyCppInterface+Private.mm")),
      ("my_client_interface",
        Cpp("my_client_interface.hpp"),
        Java("MyClientInterface.java"),
        Jni("my_client_interface.hpp", "my_client_interface.cpp"),
        ObjC("ITMyClientInterface.h"),
        ObjCpp("ITMyClientInterface+Private.h", "ITMyClientInterface+Private.mm")),
      ("all_datatypes",
        Cpp("all_datatypes.hpp"),
        Java("AllDatatypes.java"),
        Jni("all_datatypes.hpp", "all_datatypes.cpp"),
        ObjC("ITAllDatatypes.h", "ITAllDatatypes.mm"),
        ObjCpp("ITAllDatatypes+Private.h", "ITAllDatatypes+Private.mm")),
      ("using_custom_datatypes",
        Cpp("custom_datatype.hpp", "other_record.hpp"),
        Java("CustomDatatype.java", "OtherRecord.java"),
        Jni("custom_datatype.hpp", "custom_datatype.cpp", "other_record.hpp", "other_record.cpp"),
        ObjC("ITCustomDatatype.h", "ITCustomDatatype.mm", "ITOtherRecord.h", "ITOtherRecord.mm"),
        ObjCpp("ITCustomDatatype+Private.h", "ITCustomDatatype+Private.mm", "ITOtherRecord+Private.h", "ITOtherRecord+Private.mm"))
      )
    forAll (djinniTypes) { (idlFile: String, cppFilenames: Cpp, javaFilenames: Java, jniFilenames: Jni, objcFilenames: ObjC, objcppFilenames: ObjCpp) =>
      it(s"should generate valid language bridges for `$idlFile`-types") {
        Given(s"`$idlFile.djinni`")
        When(s"generating language-bridges from `$idlFile.djinni`")
        djinniGenerate(idlFile)

        Then(s"the expected files should be created for cpp: ${cppFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, CPP, cppFilenames)

        Then(s"the expected files should be created for java: ${javaFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, JAVA, javaFilenames)

        Then(s"the expected files should be created for jni: ${jniFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, JNI, jniFilenames)

        Then(s"the expected files should be created for objc: ${objcFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, OBJC, objcFilenames)

        Then(s"the expected files should be created for objcpp: ${objcppFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, OBJCPP, objcppFilenames)
      }
    }

    it("should be able to only generate Java output") {
      val outputPath = "src/it/resources/result/only_java_out"
      When("calling the generator with just `--java-out`")
      val output: String = djinni(s"--idl src/it/resources/all_datatypes.djinni --java-out $outputPath")
      Then("the generator should successfully generate just java output")
      output should equal ("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/AllDatatypes.java")
    }

    it("should be able to only generate Obj-C output") {
      val outputPath = "src/it/resources/result/only_objc_out"
      When("calling the generator with just `--objc-out`")
      val output = djinni(s"--idl src/it/resources/all_datatypes.djinni --objc-out $outputPath")
      Then("the generator should successfully generate just objc output")
      output should equal ("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/AllDatatypes.h")
      assertFileExists(s"$outputPath/AllDatatypes.mm")
    }

    it("should be able to only generate C++ output") {
      val outputPath = "src/it/resources/result/only_cpp_out"
      When("calling the generator with just `--cpp-out`")
      val output = djinni(s"--idl src/it/resources/all_datatypes.djinni --cpp-out $outputPath")
      Then("the generator should successfully generate just cpp output")
      output should equal ("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/all_datatypes.hpp")
    }
  }
}
