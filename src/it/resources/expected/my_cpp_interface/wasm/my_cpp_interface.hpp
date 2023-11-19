// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_cpp_interface.djinni

#pragma once

#include "djinni_wasm.hpp"
#include "my_cpp_interface.hpp"

namespace djinni_generated {

struct MyCppInterface : ::djinni::JsInterface<::MyCppInterface, MyCppInterface> {
    using CppType = std::shared_ptr<::MyCppInterface>;
    using CppOptType = std::shared_ptr<::MyCppInterface>;
    using JsType = em::val;
    using Boxed = MyCppInterface;

    static CppType toCpp(JsType j) { return _fromJs(j); }
    static JsType fromCppOpt(const CppOptType& c) { return {_toJs(c)}; }
    static JsType fromCpp(const CppType& c) {
        ::djinni::checkForNull(c.get(), "MyCppInterface::fromCpp");
        return fromCppOpt(c);
    }

    static em::val cppProxyMethods();

    static void method_returning_nothing(const CppType& self, int32_t w_value);
    static int32_t method_returning_some_type(const CppType& self, const std::string& w_key);
    static int32_t method_changing_nothing(const CppType& self);
    static int32_t get_version();

    static void staticInitializeConstants();
};

}  // namespace djinni_generated