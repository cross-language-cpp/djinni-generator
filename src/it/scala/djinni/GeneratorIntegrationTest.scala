package djinni

import org.scalatest._
import matchers.should.Matchers._

import org.scalatest.prop.TableDrivenPropertyChecks._

import java.nio.file.Paths

class GeneratorIntegrationTest extends IntegrationTest with GivenWhenThen {

  describe("djinni file generation") {

    removeTestOutputDirectory()

    val djinniTypes = Table(
      (
        "idlFile",
        "cppFilenames",
        "cppHeaderFilenames",
        "javaFilenames",
        "jniFilenames",
        "jniHeaderFilenames",
        "objcFilenames",
        "objcHeaderFilenames",
        "objcppFilenames",
        "objcppHeaderFilenames"
      ),
      (
        "my_enum",
        Cpp(),
        CppHeaders("my_enum.hpp"),
        Java("MyEnum.java"),
        Jni("djinni_jni_main.cpp"),
        JniHeaders("my_enum.hpp"),
        ObjC(),
        ObjCHeaders("ITMyEnum.h", "bridging-header.h"),
        ObjCpp(),
        ObjCppHeaders("ITMyEnum+Private.h")
      ),
      (
        "my_flags",
        Cpp(),
        CppHeaders("my_flags.hpp"),
        Java("MyFlags.java"),
        Jni("djinni_jni_main.cpp"),
        JniHeaders("my_flags.hpp"),
        ObjC(),
        ObjCHeaders("ITMyFlags.h"),
        ObjCpp(),
        ObjCppHeaders("ITMyFlags+Private.h")
      ),
      (
        "my_record",
        Cpp("my_record.cpp"),
        CppHeaders("my_record.hpp"),
        Java("MyRecord.java"),
        Jni("my_record.cpp", "djinni_jni_main.cpp"),
        JniHeaders("my_record.hpp"),
        ObjC("ITMyRecord.mm"),
        ObjCHeaders("ITMyRecord.h", "bridging-header.h"),
        ObjCpp("ITMyRecord+Private.mm"),
        ObjCppHeaders("ITMyRecord+Private.h")
      ),
      (
        "my_cpp_interface",
        Cpp("my_cpp_interface.cpp"),
        CppHeaders("my_cpp_interface.hpp"),
        Java("MyCppInterface.java"),
        Jni("my_cpp_interface.cpp", "djinni_jni_main.cpp"),
        JniHeaders("my_cpp_interface.hpp"),
        ObjC("ITMyCppInterface.mm"),
        ObjCHeaders("ITMyCppInterface.h", "bridging-header.h"),
        ObjCpp("ITMyCppInterface+Private.mm"),
        ObjCppHeaders("ITMyCppInterface+Private.h")
      ),
      (
        "my_client_interface",
        Cpp(),
        CppHeaders("my_client_interface.hpp"),
        Java("MyClientInterface.java"),
        Jni("my_client_interface.cpp", "djinni_jni_main.cpp"),
        JniHeaders("my_client_interface.hpp"),
        ObjC(),
        ObjCHeaders("ITMyClientInterface.h", "bridging-header.h"),
        ObjCpp("ITMyClientInterface+Private.mm"),
        ObjCppHeaders("ITMyClientInterface+Private.h")
      ),
      (
        "all_datatypes",
        Cpp(),
        CppHeaders("all_datatypes.hpp", "enum_data.hpp"),
        Java("AllDatatypes.java", "EnumData.java"),
        Jni("all_datatypes.cpp", "djinni_jni_main.cpp"),
        JniHeaders("all_datatypes.hpp", "enum_data.hpp"),
        ObjC("ITAllDatatypes.mm"),
        ObjCHeaders("ITAllDatatypes.h", "ITEnumData.h", "bridging-header.h"),
        ObjCpp("ITAllDatatypes+Private.mm"),
        ObjCppHeaders("ITAllDatatypes+Private.h", "ITEnumData+Private.h")
      ),
      (
        "using_custom_datatypes",
        Cpp(),
        CppHeaders("custom_datatype.hpp", "other_record.hpp"),
        Java("CustomDatatype.java", "OtherRecord.java"),
        Jni("custom_datatype.cpp", "other_record.cpp", "djinni_jni_main.cpp"),
        JniHeaders("custom_datatype.hpp", "other_record.hpp"),
        ObjC("ITCustomDatatype.mm", "ITOtherRecord.mm"),
        ObjCHeaders(
          "ITCustomDatatype.h",
          "ITOtherRecord.h",
          "bridging-header.h"
        ),
        ObjCpp("ITCustomDatatype+Private.mm", "ITOtherRecord+Private.mm"),
        ObjCppHeaders("ITCustomDatatype+Private.h", "ITOtherRecord+Private.h")
      )
    )
    forAll(djinniTypes) {
      (
          idlFile: String,
          cppFilenames: Cpp,
          cppHeaderFilenames: CppHeaders,
          javaFilenames: Java,
          jniFilenames: Jni,
          jniHeaderFilenames: JniHeaders,
          objcFilenames: ObjC,
          objcHeaderFilenames: ObjCHeaders,
          objcppFilenames: ObjCpp,
          objcppHeaderFilenames: ObjCppHeaders
      ) =>
        it(s"should generate valid language bridges for `$idlFile`-types") {
          Given(s"`$idlFile.djinni`")
          When(s"generating language-bridges from `$idlFile.djinni`")
          djinniGenerate(idlFile)

          Then(
            s"the expected source files should be created for cpp: ${cppFilenames.mkString(", ")}"
          )
          assertFileContentEquals(idlFile, CPP, cppFilenames)

          Then(
            s"the expected header files should be created for cpp: ${cppHeaderFilenames.mkString(", ")}"
          )
          assertFileContentEquals(idlFile, CPP_HEADERS, cppHeaderFilenames)

          Then(
            s"the expected files should be created for java: ${javaFilenames.mkString(", ")}"
          )
          assertFileContentEquals(idlFile, JAVA, javaFilenames)

          Then(
            s"the expected source files should be created for jni: ${jniFilenames.mkString(", ")}"
          )
          assertFileContentEquals(idlFile, JNI, jniFilenames)

          Then(
            s"the expected header files should be created for jni: ${jniHeaderFilenames.mkString(", ")}"
          )
          assertFileContentEquals(idlFile, JNI_HEADERS, jniHeaderFilenames)

          Then(
            s"the expected source files should be created for objc: ${objcFilenames.mkString(", ")}"
          )
          assertFileContentEquals(idlFile, OBJC, objcFilenames)

          Then(
            s"the expected header files should be created for objc: ${objcHeaderFilenames.mkString(", ")}"
          )
          assertFileContentEquals(idlFile, OBJC_HEADERS, objcHeaderFilenames)

          Then(
            s"the expected source files should be created for objcpp: ${objcppFilenames.mkString(", ")}"
          )
          assertFileContentEquals(idlFile, OBJCPP, objcppFilenames)

          Then(
            s"the expected header files should be created for objcpp: ${objcppHeaderFilenames.mkString(", ")}"
          )
          assertFileContentEquals(
            idlFile,
            OBJCPP_HEADERS,
            objcppHeaderFilenames
          )

          Then(
            "the file `generated-files.txt` should contain all generated files"
          )
          assertFileContentEquals(
            idlFile,
            "",
            List("generated-files.txt"),
            s => Paths.get(s)
          )
        }
    }

    it(
      "should be able to generate C++ output for interfaces with external dependencies and non-null pointers"
    ) {
      val idlFile = "cpp_dependent_interface"
      When(s"generating C++ language-bridges from `$idlFile.djinni`")
      val cppHeaderFilenames = CppHeaders("dependent_interface.hpp")
      val cmd = djinniParams(
        idlFile,
        cpp = true,
        objc = false,
        java = false,
        useNNHeader = true
      )
      djinni(cmd)

      Then(
        s"the expected header files should be created for cpp: ${cppHeaderFilenames.mkString(", ")}"
      )
      assertFileContentEquals(idlFile, CPP_HEADERS, cppHeaderFilenames)

      Then("the file `generated-files.txt` should contain all generated files")
      assertFileContentEquals(
        idlFile,
        "",
        List("generated-files.txt"),
        s => Paths.get(s)
      )
    }

    it("should be able to generate C++ records without a default constructor") {
      val idlFile = "my_record_omit_default_ctor"
      When(
        s"generating a C++ record from `$idlFile.djinni` with omit default ctor flag enabled"
      )
      val cppHeaderFilenames = CppHeaders("my_record_omit_default_ctor.hpp")
      val cmd = djinniParams(
        idlFile,
        cpp = true,
        objc = false,
        java = false,
        cppOmitDefaultRecordCtor = true
      )
      djinni(cmd)

      Then(
        s"the expected header files should be created for cpp: ${cppHeaderFilenames.mkString(", ")}"
      )
      assertFileContentEquals(idlFile, CPP_HEADERS, cppHeaderFilenames)

      Then("the file `generated-files.txt` should contain all generated files")
      assertFileContentEquals(
        idlFile,
        "",
        List("generated-files.txt"),
        s => Paths.get(s)
      )
    }

    it("should be able to only generate Java output") {
      val outputPath = "src/it/resources/result/only_java_out"
      When("calling the generator with just `--java-out`")
      val output: String = djinni(
        s"--idl src/it/resources/all_datatypes.djinni --java-out $outputPath"
      )
      Then("the generator should successfully generate just java output")
      output should equal("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/AllDatatypes.java")
    }

    it("should be able to only generate Obj-C output with --objc-type-prefix") {
      val outputPath = "src/it/resources/result/only_objc_out"
      When(
        "calling the generator with just `--objc-out and --objc-type-prefix Cpp`"
      )
      val output = djinni(
        s"--idl src/it/resources/all_datatypes.djinni --objc-type-prefix Cpp --objc-out $outputPath"
      )
      Then("the generator should successfully generate just objc output")
      output should equal("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/CppAllDatatypes.h")
      assertFileExists(s"$outputPath/CppAllDatatypes.mm")
    }

    it(
      "should be able to only generate Obj-C output when using --cpp-namespace"
    ) {
      val outputPath = "src/it/resources/result/only_objc_out"
      When(
        "calling the generator with just `--objc-out and --cpp-namespace Cpp`"
      )
      val output = djinni(
        s"--idl src/it/resources/all_datatypes.djinni --cpp-namespace Cpp --objc-out $outputPath"
      )
      Then(
        "the generator should successfully generate just objc output and write all generate files to the given path, including headers"
      )
      output should equal("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/AllDatatypes.h")
      assertFileExists(s"$outputPath/AllDatatypes.mm")
    }

    it(
      "should be able to include objc generic type information for extern types"
    ) {
      Given(
        "an IDL-file that uses a parameterized extern type that enables the Objective-C option 'generics: true'"
      )
      val idlFile = "extern_generics"

      When("generating the Objective-C gluecode")
      val objcFilenames = ObjC("ITMyRecord.mm")
      val objcHeaderFilenames = ObjCHeaders("ITMyRecord.h")
      val objcppFilenames = ObjCpp("ITMyRecord+Private.mm")
      val objcppHeaderFilenames = ObjCppHeaders("ITMyRecord+Private.h")
      val cmd = djinniParams(
        idlFile,
        cpp = false,
        objc = true,
        java = false,
        cppOmitDefaultRecordCtor = true
      )
      djinni(cmd)

      Then(
        "the generic type parameters should be included in the Objective-C type declarations"
      )
      assertFileContentEquals(idlFile, OBJC, objcFilenames)
      assertFileContentEquals(idlFile, OBJC_HEADERS, objcHeaderFilenames)
      assertFileContentEquals(idlFile, OBJCPP, objcppFilenames)
      assertFileContentEquals(idlFile, OBJCPP_HEADERS, objcppHeaderFilenames)
    }

    it(
      "should be able to include Java generic type information for extern types"
    ) {
      Given(
        "an IDL-file that uses a parameterized extern type that enables the Java option 'generics: true'"
      )
      val idlFile = "extern_generics"

      When("generating the Java/JNI gluecode")
      val javaFilename = Java("MyRecord.java")
      val jniFilename = Jni("my_record.cpp")
      val jniHeaderFilename = JniHeaders("my_record.hpp")
      val cmd = djinniParams(
        idlFile,
        cpp = false,
        objc = false,
        java = true,
        cppOmitDefaultRecordCtor = true
      )

      djinni(cmd)

      Then(
        "the generic type parameters should be included in the Java type declarations"
      )
      assertFileContentEquals(idlFile, JAVA, javaFilename)
      assertFileContentEquals(idlFile, JNI, jniFilename)
      assertFileContentEquals(idlFile, JNI_HEADERS, jniHeaderFilename)
    }

    it(
      "should not be able to generate Obj-C output when either a namespace or prefix is missing"
    ) {
      val outputPath = "src/it/resources/result/only_objc_out"
      When("calling the generator with just `--objc-out`")
      Then("the generator should fail")
      a[RuntimeException] should be thrownBy djinni(
        s"--idl src/it/resources/all_datatypes.djinni --objc-out $outputPath"
      )
    }

    it("should be able to only generate Obj-C++ output") {
      val outputPath = "src/it/resources/result/only_objcpp_out"
      When("calling the generator with just `--objcpp-out`")
      val output = djinni(
        s"--idl src/it/resources/all_datatypes.djinni --objcpp-out $outputPath"
      )
      Then(
        "the generator should successfully generate just objcpp output and write all generate files to the given path, including headers"
      )
      output should equal("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/AllDatatypes+Private.h")
      assertFileExists(s"$outputPath/AllDatatypes+Private.mm")
    }

    it("should be able to only generate C++ output") {
      val outputPath = "src/it/resources/result/only_cpp_out"
      When("calling the generator with just `--cpp-out`")
      val output = djinni(
        s"--idl src/it/resources/all_datatypes.djinni --cpp-out $outputPath"
      )
      Then("the generator should successfully generate just cpp output")
      output should equal("Parsing...\nResolving...\nGenerating...\n")
      assertFileExists(s"$outputPath/all_datatypes.hpp")
    }

    it("should be able to parse yaml files without all languages defined") {
      val outputPath = "src/it/resources/result/only_yaml_out"
      Given(
        "an IDL that depends on a YAML type definition that misses the `cs` key"
      )
      When("calling the generator  with `--cppcli-out` to generate C# gluecode")
      Then("the generation should fail gracefully")
      a[RuntimeException] should be thrownBy djinni(
        s"--idl src/it/resources/date_no_cs.djinni --cppcli-out $outputPath"
      )
      When(
        "calling the generator with `--java-out` to generate just Java gluecode"
      )
      Then(
        "the generator should not fail, because the `cs` type definition is not needed"
      )
      noException should be thrownBy djinni(
        s"--idl src/it/resources/date_no_cs.djinni --java-out $outputPath"
      )
    }

    it(
      "should gracefully fail when a required language definition is incomplete"
    ) {
      val outputPath = "src/it/resources/result/only_yaml_out"
      Given(
        "an IDL file that depends on a YAML type definition that misses a required key in the `objc` definition"
      )
      When("calling the generator")
      Then(
        "the generation should fail gracefully if code for Objective-C is generated"
      )
      a[RuntimeException] should be thrownBy djinni(
        s"--idl src/it/resources/date_no_cs.djinni --objc-out $outputPath"
      )
    }

    it(s"skip-generate should not generate any files") {
      val idlFile = "all_datatypes"
      val outputPath = "src/it/resources/result/skip_generate"
      Given(s"`$idlFile.djinni`")
      When(s"passing skip-generation true")

      djinni(djinniParams(idlFile, outputPath) + " --skip-generation true")

      Then(s"`$outputPath` should have been created")
      val dir = new java.io.File(outputPath, idlFile)
      dir.exists should be(true)

      Then(s"genreated-files.txt should have been generated")
      val gen = new java.io.File(dir, "generated-files.txt")
      gen.exists should be(true)

      Then(s"only the generated-files.txt should be generated")
      val files = dir.listFiles()
      files should contain only (gen)
    }

    it(
      "should fail if an unsupported json serializer is specified"
    ) {
      val outputPath = "src/it/resources/result/unknown_json_serializer"
      When(
        "calling the generator with json_serializer `--cpp-json-serialiation arbitrary_unsupported_serializer`"
      )
      Then("the generator should fail")
      a[RuntimeException] should be thrownBy djinni(
        s"--idl src/it/resources/all_datatypes.djinni --cpp-json-serialization arbitrary_unsupported_serializer --cpp-out $outputPath/cpp"
      )
    }

    it("should generate json serializers for all data types") {
      val idlFile = "all_datatypes_json"
      When(
        s"generating a C++ record from `$idlFile.djinni` with json serialization enabled"
      )
      val cppHeaderFilenames = CppHeaders(
        "all_datatypes_json.hpp",
        "all_datatypes_json+json.hpp",
        "enum_data.hpp",
        "enum_data+json.hpp",
        "my_flags.hpp",
        "my_flags+json.hpp",
        "json+extension.hpp"
      )
      val cmd = djinniParams(
        idlFile,
        cpp = true,
        objc = false,
        java = false,
        cppJsonSerialization = Some("nlohmann_json")
      )
      djinni(cmd)

      Then(
        s"the expected header files should be created for cpp: ${cppHeaderFilenames.mkString(", ")}"
      )
      assertFileContentEquals(idlFile, CPP_HEADERS, cppHeaderFilenames)

      Then("the file `generated-files.txt` should contain all generated files")
      assertFileContentEquals(
        idlFile,
        "",
        List("generated-files.txt"),
        s => Paths.get(s)
      )
    }
  }

  it(
    "`should create json serializers with appropriate namespace prefix when --cpp-namespace is specified"
  ) {
    val idlFile = "all_json_specialized_datatypes"
    val outputPath =
      "src/it/resources/result/all_json_specialized_datatypes_in_custom_namespace"
    When(
      "calling the generator with `--cpp-namespace custom_namespace, --cpp-json-serialization nlohmann_json and --cpp-out`"
    )
    val _ = djinni(
      s"--idl src/it/resources/${idlFile}.djinni --cpp-namespace custom_namespace --cpp-json-serialization nlohmann_json --cpp-out $outputPath/cpp --cpp-header-out $outputPath/cpp-headers"
    )
    Then(
      "the generated C++ json serializers should use the correct namespace for the datatypes"
    )

    val cppHeaderFilenames = CppHeaders(
      "my_record+json.hpp",
      "my_enum+json.hpp",
      "my_flags+json.hpp"
    )

    Then(
      s"the expected header files should be created for cpp: ${cppHeaderFilenames.mkString(", ")}"
    )
    assertFileContentEquals(
      "all_json_specialized_datatypes_in_custom_namespace",
      CPP_HEADERS,
      cppHeaderFilenames
    )
  }

  it(
    "should generate C++14 deprecation attributes from @deprecated notes in comments"
  ) {
    Given(
      "an IDL-file that documents deprecation using @deprecated in comments"
    )
    val idlFile = "deprecation"

    When("generating the C++ headers")
    val cppHeaderFilenames = CppHeaders(
      "my_record.hpp",
      "my_enum.hpp",
      "my_flags.hpp",
      "my_interface.hpp"
    )
    val cmd = djinniParams(
      idlFile,
      cpp = true,
      objc = false,
      java = false,
      cppOmitDefaultRecordCtor = true
    )

    djinni(cmd)

    Then(
      "the @deprecated comments are generated as C++14 [[deprecated]] attributes"
    )
    assertFileContentEquals(idlFile, CPP_HEADERS, cppHeaderFilenames)
  }

  it(
    "should route CppProxy destruction through the destruction queue when --java-non-blocking-finalizers is set"
  ) {
    Given("an IDL-file with a C++ implemented interface")
    val idlFile = "non_blocking_finalizers"

    When("generating Java source with --java-non-blocking-finalizers true")
    val javaFilenames = Java(
      "NonBlockingFinalizersInterface.java",
      "DjinniNativeDestructionQueue.java"
    )
    val cmd = djinniParams(
      idlFile,
      cpp = false,
      objc = false,
      java = true,
      javaNonBlockingFinalizers = true
    )

    djinni(cmd)

    Then(
      "finalize() enqueues the destruction and the queue support class is emitted"
    )
    assertFileContentEquals(idlFile, JAVA, javaFilenames)
  }

  it(
    "should generate @Deprecated annotations for Java from @deprecated notes in comments"
  ) {
    Given(
      "an IDL-file that documents deprecation using @deprecated in comments"
    )
    val idlFile = "deprecation"

    When("generating Java source")
    val javaFilenames =
      Java("MyRecord.java", "MyEnum.java", "MyFlags.java", "MyInterface.java")
    val cmd = djinniParams(
      idlFile,
      cpp = false,
      objc = false,
      java = true,
      cppOmitDefaultRecordCtor = true
    )

    djinni(cmd)

    Then(
      "the @deprecated comments are generated as @Deprecated annotations"
    )
    assertFileContentEquals(idlFile, JAVA, javaFilenames)
  }

  it(
    "should generate __deprecated attributes for ObjC from @deprecated notes in comments"
  ) {
    Given(
      "an IDL-file that documents deprecation using @deprecated in comments"
    )
    val idlFile = "deprecation"

    When("generating ObjC source")
    val objcHeaderFilenames = ObjCHeaders(
      "ITMyRecord.h",
      "ITMyEnum.h",
      "ITMyFlags.h",
      "ITMyInterface.h"
    )
    val cmd = djinniParams(
      idlFile,
      cpp = false,
      objc = true,
      java = false,
      cppOmitDefaultRecordCtor = true
    )

    djinni(cmd)

    Then(
      "the @deprecated comments are generated as __deprecated attributes"
    )
    assertFileContentEquals(idlFile, OBJC_HEADERS, objcHeaderFilenames)
  }

  it(
    "should generate equality or ordinality operators for interfaces that ask for them using the require clause"
  ) {
    Given(
      "an IDL-file that contains a C++ iterface with the requires() clause"
    )
    val idlFile = "requires"

    When("generating Java, JNI & C++ source")
    val cppHeaderFilenames = CppHeaders(
      "requires_all_interface.hpp",
      "requires_eq_interface.hpp",
      "requires_ord_interface.hpp"
    )
    val javaFilenames = Java(
      "RequiresAllInterface.java",
      "RequiresEqInterface.java",
      "RequiresOrdInterface.java"
    )
    val jniFilenames = Jni(
      "requires_all_interface.cpp",
      "requires_eq_interface.cpp",
      "requires_ord_interface.cpp"
    )
    val jniHeaderFilenames = JniHeaders(
      "requires_all_interface.hpp",
      "requires_eq_interface.hpp",
      "requires_ord_interface.hpp"
    )

    val cmd = djinniParams(
      idlFile,
      cpp = true,
      objc = false,
      java = true,
      cppOmitDefaultRecordCtor = false
    )

    djinni(cmd)

    Then(
      "the interface with a requires() clause has generated the requested operator functions in C++ and hooked them up to equals() in the Java class via JNI"
    )
    assertFileContentEquals(idlFile, CPP_HEADERS, cppHeaderFilenames)
    assertFileContentEquals(idlFile, JAVA, javaFilenames)
    assertFileContentEquals(idlFile, JNI, jniFilenames)
    assertFileContentEquals(idlFile, JNI_HEADERS, jniHeaderFilenames)
  }
}
