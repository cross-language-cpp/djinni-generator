// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from cppcli_circular_dependent_interface.djinni

#pragma once

#include "../cpp-headers/another_interface.hpp"
#include "OneInterface.hpp"
#include <memory>

ref class OneInterface;

public ref class AnotherInterface abstract {
public:
    virtual void MethodTakingOneInterface(OneInterface^ dep) abstract;

    virtual void MethodTakingOptionalOneInterface(OneInterface^ dep) abstract;

    virtual OneInterface^ MethodReturningOneInterface() abstract;

    virtual OneInterface^ MethodReturningOptionalOneInterface() abstract;

internal:
    using CppType = std::shared_ptr<::AnotherInterface>;
    using CppOptType = std::shared_ptr<::AnotherInterface>;
    using CsType = AnotherInterface^;

    static CppType ToCpp(CsType cs);
    static CsType FromCppOpt(const CppOptType& cpp);
    static CsType FromCpp(const CppType& cpp) { return FromCppOpt(cpp); }
};
