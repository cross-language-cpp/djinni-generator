// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_record.djinni

#pragma once

#include "djinni_wasm.hpp"
#include "my_record.hpp"

namespace djinni_generated {

struct MyRecord
{
    using CppType = ::MyRecord;
    using JsType = em::val;
    using Boxed = MyRecord;

    static CppType toCpp(const JsType& j);
    static JsType fromCpp(const CppType& c);
    static void staticInitializeConstants();
};

}  // namespace djinni_generated