package djinni

import org.scalatest.GivenWhenThen
import org.scalatest.Matchers.{convertToAnyShouldWrapper, equal}
import org.scalatest.prop.TableDrivenPropertyChecks._

class GeneratorIntegrationTest extends IntegrationTest with GivenWhenThen {

  describe("djinni file generation") {
    val djinniTypes = Table(
      ("idlFile",
        "cppFilenames",
        "cppHeaderFilenames",
        "javaFilenames",
        "jniFilenames",
        "jniHeaderFilenames",
        "objcFilenames",
        "objcHeaderFilenames",
        "objcppFilenames"),
      ("my_enum",
        Cpp(),
        CppHeaders("my_enum.hpp"),
        Java("MyEnum.java"),
        Jni(),
        JniHeaders("my_enum.hpp"),
        ObjC(),
        ObjCHeaders("ITMyEnum.h", "bridging-header.h"),
        ObjCpp("ITMyEnum+Private.h")),
      ("my_flags",
        Cpp(),
        CppHeaders("my_flags.hpp"),
        Java("MyFlags.java"),
        Jni(),
        JniHeaders("my_flags.hpp"),
        ObjC(),
        ObjCHeaders("ITMyFlags.h"),
        ObjCpp("ITMyFlags+Private.h")),
      ("my_record",
        Cpp("my_record.cpp"),
        CppHeaders("my_record.hpp"),
        Java("MyRecord.java"),
        Jni("my_record.cpp"),
        JniHeaders("my_record.hpp"),
        ObjC("ITMyRecord.mm"),
        ObjCHeaders("ITMyRecord.h", "bridging-header.h"),
        ObjCpp("ITMyRecord+Private.h", "ITMyRecord+Private.mm")),
      ("my_cpp_interface",
        Cpp("my_cpp_interface.cpp"),
        CppHeaders("my_cpp_interface.hpp"),
        Java("MyCppInterface.java"),
        Jni("my_cpp_interface.cpp"),
        JniHeaders("my_cpp_interface.hpp"),
        ObjC("ITMyCppInterface.mm"),
        ObjCHeaders("ITMyCppInterface.h", "bridging-header.h"),
        ObjCpp("ITMyCppInterface+Private.h", "ITMyCppInterface+Private.mm")),
      ("my_client_interface",
        Cpp(),
        CppHeaders("my_client_interface.hpp"),
        Java("MyClientInterface.java"),
        Jni("my_client_interface.cpp"),
        JniHeaders("my_client_interface.hpp"),
        ObjC(),
        ObjCHeaders("ITMyClientInterface.h", "bridging-header.h"),
        ObjCpp("ITMyClientInterface+Private.h", "ITMyClientInterface+Private.mm")),
      ("all_datatypes",
        Cpp(),
        CppHeaders("all_datatypes.hpp"),
        Java("AllDatatypes.java"),
        Jni("all_datatypes.cpp"),
        JniHeaders("all_datatypes.hpp"),
        ObjC("ITAllDatatypes.mm"),
        ObjCHeaders("ITAllDatatypes.h", "bridging-header.h"),
        ObjCpp("ITAllDatatypes+Private.h", "ITAllDatatypes+Private.mm")),
      ("using_custom_datatypes",
        Cpp(),
        CppHeaders("custom_datatype.hpp", "other_record.hpp"),
        Java("CustomDatatype.java", "OtherRecord.java"),
        Jni("custom_datatype.cpp", "other_record.cpp"),
        JniHeaders("custom_datatype.hpp", "other_record.hpp"),
        ObjC("ITCustomDatatype.mm", "ITOtherRecord.mm"),
        ObjCHeaders("ITCustomDatatype.h","ITOtherRecord.h", "bridging-header.h"),
        ObjCpp("ITCustomDatatype+Private.h", "ITCustomDatatype+Private.mm", "ITOtherRecord+Private.h", "ITOtherRecord+Private.mm"))
      )
    forAll (djinniTypes) { (idlFile: String, cppFilenames: Cpp, cppHeaderFilenames: CppHeaders, javaFilenames: Java, jniFilenames: Jni, jniHeaderFilenames: JniHeaders, objcFilenames: ObjC, objcHeaderFilenames: ObjCHeaders, objcppFilenames: ObjCpp) =>
      it(s"should generate valid language bridges for `$idlFile`-types") {
        Given(s"`$idlFile.djinni`")
        When(s"generating language-bridges from `$idlFile.djinni`")
        djinniGenerate(idlFile)

        Then(s"the expected source files should be created for cpp: ${cppFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, CPP, cppFilenames)

        Then(s"the expected header files should be created for cpp: ${cppHeaderFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, CPP_HEADERS, cppHeaderFilenames)

        Then(s"the expected files should be created for java: ${javaFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, JAVA, javaFilenames)

        Then(s"the expected source files should be created for jni: ${jniFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, JNI, jniFilenames)

        Then(s"the expected header files should be created for jni: ${jniHeaderFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, JNI_HEADERS, jniHeaderFilenames)

        Then(s"the expected source files should be created for objc: ${objcFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, OBJC, objcFilenames)

        Then(s"the expected header files should be created for objc: ${objcHeaderFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, OBJC_HEADERS, objcHeaderFilenames)

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
