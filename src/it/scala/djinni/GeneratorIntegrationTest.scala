package djinni

import org.scalatest.GivenWhenThen
import org.scalatest.Matchers._
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
        "objcppFilenames",
        "pythonFilenames",
        "pyCffiFilenames",
        "cWrapperFilenames",
        "cWrapperHeaderFilenames",
        "cppcliFilenames"),
      ("my_enum",
        Cpp(),
        CppHeaders("my_enum.hpp"),
        Java("MyEnum.java"),
        Jni(),
        JniHeaders("my_enum.hpp"),
        ObjC(),
        ObjCHeaders("ITMyEnum.h", "bridging-header.h"),
        ObjCpp("ITMyEnum+Private.h"),
        Python("my_enum.py"),
        PyCffi(),
        CWrapper("dh__my_enum.cpp", "dh__my_enum.hpp"),
        CWrapperHeaders("dh__my_enum.h"),
        CppCli("MyEnum.hpp", "MyEnum.cpp")),
      ("my_flags",
        Cpp(),
        CppHeaders("my_flags.hpp"),
        Java("MyFlags.java"),
        Jni(),
        JniHeaders("my_flags.hpp"),
        ObjC(),
        ObjCHeaders("ITMyFlags.h"),
        ObjCpp("ITMyFlags+Private.h"),
        Python("my_flags.py"),
        PyCffi(),
        CWrapper("dh__my_flags.cpp", "dh__my_flags.hpp"),
        CWrapperHeaders("dh__my_flags.h"),
        CppCli("MyFlags.hpp", "MyFlags.cpp")),
      ("my_record",
        Cpp("my_record.cpp"),
        CppHeaders("my_record.hpp"),
        Java("MyRecord.java"),
        Jni("my_record.cpp"),
        JniHeaders("my_record.hpp"),
        ObjC("ITMyRecord.mm"),
        ObjCHeaders("ITMyRecord.h", "bridging-header.h"),
        ObjCpp("ITMyRecord+Private.h", "ITMyRecord+Private.mm"),
        Python("dh__map_string_int32_t.py", "dh__set_string.py", "my_record.py", "my_record_helper.py"),
        PyCffi(),
        CWrapper("dh__map_string_int32_t.cpp", "dh__map_string_int32_t.hpp", "dh__my_record.cpp",
          "dh__my_record.hpp", "dh__set_string.cpp", "dh__set_string.hpp"),
        CWrapperHeaders("dh__map_string_int32_t.h", "dh__my_record.h", "dh__set_string.h"),
        CppCli("MyRecord.hpp", "MyRecord.cpp")),
      ("my_cpp_interface",
        Cpp("my_cpp_interface.cpp"),
        CppHeaders("my_cpp_interface.hpp"),
        Java("MyCppInterface.java"),
        Jni("my_cpp_interface.cpp"),
        JniHeaders("my_cpp_interface.hpp"),
        ObjC("ITMyCppInterface.mm"),
        ObjCHeaders("ITMyCppInterface.h", "bridging-header.h"),
        ObjCpp("ITMyCppInterface+Private.h", "ITMyCppInterface+Private.mm"),
        Python("my_cpp_interface.py"),
        PyCffi("pycffi_lib_build.py"),
        CWrapper("cw__my_cpp_interface.cpp", "cw__my_cpp_interface.hpp"),
        CWrapperHeaders("cw__my_cpp_interface.h"),
        CppCli("MyCppInterface.hpp", "MyCppInterface.cpp")),
      ("my_client_interface",
        Cpp(),
        CppHeaders("my_client_interface.hpp"),
        Java("MyClientInterface.java"),
        Jni("my_client_interface.cpp"),
        JniHeaders("my_client_interface.hpp"),
        ObjC(),
        ObjCHeaders("ITMyClientInterface.h", "bridging-header.h"),
        ObjCpp("ITMyClientInterface+Private.h", "ITMyClientInterface+Private.mm"),
        Python("my_client_interface.py"),
        PyCffi("pycffi_lib_build.py"),
        CWrapper("cw__my_client_interface.cpp", "cw__my_client_interface.hpp"),
        CWrapperHeaders("cw__my_client_interface.h"),
        CppCli("MyClientInterface.hpp", "MyClientInterface.cpp")),
      ("all_datatypes",
        Cpp(),
        CppHeaders("all_datatypes.hpp"),
        Java("AllDatatypes.java"),
        Jni("all_datatypes.cpp"),
        JniHeaders("all_datatypes.hpp"),
        ObjC("ITAllDatatypes.mm"),
        ObjCHeaders("ITAllDatatypes.h", "bridging-header.h"),
        ObjCpp("ITAllDatatypes+Private.h", "ITAllDatatypes+Private.mm"),
        Python("all_datatypes.py", "all_datatypes_helper.py", "dh__list_bool.py", "dh__map_int8_t_bool.py", "dh__set_bool.py"),
        PyCffi(),
        CWrapper("dh__all_datatypes.cpp", "dh__all_datatypes.hpp", "dh__list_bool.cpp",
          "dh__list_bool.hpp", "dh__map_int8_t_bool.cpp", "dh__map_int8_t_bool.hpp", "dh__set_bool.cpp", "dh__set_bool.hpp"),
        CWrapperHeaders("dh__all_datatypes.h", "dh__list_bool.h", "dh__map_int8_t_bool.h", "dh__set_bool.h"),
        CppCli("AllDatatypes.hpp", "AllDatatypes.cpp")),
      ("using_custom_datatypes",
        Cpp(),
        CppHeaders("custom_datatype.hpp", "other_record.hpp"),
        Java("CustomDatatype.java", "OtherRecord.java"),
        Jni("custom_datatype.cpp", "other_record.cpp"),
        JniHeaders("custom_datatype.hpp", "other_record.hpp"),
        ObjC("ITCustomDatatype.mm", "ITOtherRecord.mm"),
        ObjCHeaders("ITCustomDatatype.h","ITOtherRecord.h", "bridging-header.h"),
        ObjCpp("ITCustomDatatype+Private.h", "ITCustomDatatype+Private.mm", "ITOtherRecord+Private.h", "ITOtherRecord+Private.mm"),
        Python("custom_datatype.py", "custom_datatype_helper.py", "other_record.py", "other_record_helper.py"),
        PyCffi(),
        CWrapper("dh__custom_datatype.cpp", "dh__custom_datatype.hpp", "dh__other_record.cpp", "dh__other_record.hpp"),
        CWrapperHeaders("dh__custom_datatype.h", "dh__other_record.h"),
        CppCli("CustomDatatype.hpp", "CustomDatatype.cpp"))
      )
    forAll (djinniTypes) { (idlFile: String, cppFilenames: Cpp, cppHeaderFilenames: CppHeaders, javaFilenames: Java, jniFilenames: Jni, jniHeaderFilenames: JniHeaders, objcFilenames: ObjC, objcHeaderFilenames: ObjCHeaders, objcppFilenames: ObjCpp, pythonFilenames: Python, pyCffiFilenames: PyCffi, cWrapperFilenames: CWrapper, cWrapperHeaderFilenames: CWrapperHeaders, cppcliFilenames: CppCli) =>
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

        Then(s"the expected files should be created for python: ${pythonFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, PY, pythonFilenames)

        Then(s"the expected files should be created for cffi: ${pyCffiFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, CFFI, pyCffiFilenames)

        Then(s"the expected source files should be created for c wrapper: ${cWrapperFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, CWRAPPER, cWrapperFilenames)

        Then(s"the expected header files should be created for c wrapper: ${cWrapperHeaderFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, CWRAPPER_HEADERS, cWrapperHeaderFilenames)

        Then(s"the expected files should be created for C++/CLI: ${cppcliFilenames.mkString(", ")}")
        assertFileContentEquals(idlFile, CPPCLI, cppcliFilenames)
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

    it("should be able to only generate C++/CLI output") {
      val outputPath = "src/it/resources/result/only_cppcli_out"
      When("calling the generator with just `--cppcli-out`")
      val output = djinni(s"--idl src/it/resources/all_datatypes.djinni --cppcli-out $outputPath")
      Then("the generator should successfully generate just C++/CLI output")
      output should equal ("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/AllDatatypes.hpp")
      assertFileExists(s"$outputPath/AllDatatypes.cpp")
    }

    it("should be able to only generate C++ output") {
      val outputPath = "src/it/resources/result/only_cpp_out"
      When("calling the generator with just `--cpp-out`")
      val output = djinni(s"--idl src/it/resources/all_datatypes.djinni --cpp-out $outputPath")
      Then("the generator should successfully generate just cpp output")
      output should equal ("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/all_datatypes.hpp")
    }

    it("should be able to only generate Python output") {
      val outputPath = "src/it/resources/result/only_python_out"
      When("calling the generator with just `--py-out`")
      val output = djinni(s"--idl src/it/resources/all_datatypes.djinni --py-out $outputPath")
      Then("the generator should successfully generate just python output")
      output should equal ("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/all_datatypes.py")
      assertFileExists(s"$outputPath/all_datatypes_helper.py")
      assertFileExists(s"$outputPath/dh__list_bool.py")
      assertFileExists(s"$outputPath/dh__map_int8_t_bool.py")
      assertFileExists(s"$outputPath/dh__set_bool.py")
    }

    it("should be able to parse yaml files without all languages defined") {
      val outputPath = "src/it/resources/result/only_yaml_out"
      Given("an IDL that depends on a YAML type definition that misses the `cs` key")
      When("calling the generator  with `--cppcli-out` to generate C# gluecode")
      Then("the generation should fail gracefully")
      a [RuntimeException] should be thrownBy djinni(s"--idl src/it/resources/date_no_cs.djinni --cppcli-out $outputPath")
      When("calling the generator with `--java-out` to generate just Java gluecode")
      Then("the generator should not fail, because the `cs` type definition is not needed")
      noException should be thrownBy djinni(s"--idl src/it/resources/date_no_cs.djinni --java-out $outputPath")
    }

    it("should gracefully fail when a required language definition is incomplete") {
      val outputPath = "src/it/resources/result/only_yaml_out"
      When("calling the generator with ")
      a [RuntimeException] should be thrownBy djinni(s"--idl src/it/resources/date_no_cs.djinni --objc-out $outputPath")
    }
  }
}
