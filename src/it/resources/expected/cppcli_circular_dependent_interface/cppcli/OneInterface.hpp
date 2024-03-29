// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from cppcli_circular_dependent_interface.djinni

#pragma once

#include "../cpp-headers/one_interface.hpp"
#include "AnotherInterface.hpp"
#include <memory>

ref class AnotherInterface;

public ref class OneInterface abstract {
public:
    virtual void MethodTakingAnotherInterface(AnotherInterface^ dep) abstract;

    virtual void MethodTakingOptionalAnotherInterface(AnotherInterface^ dep) abstract;

    virtual AnotherInterface^ MethodReturningAnotherInterface() abstract;

    virtual AnotherInterface^ MethodReturningOptionalAnotherInterface() abstract;

internal:
    using CppType = std::shared_ptr<::OneInterface>;
    using CppOptType = std::shared_ptr<::OneInterface>;
    using CsType = OneInterface^;

    static CppType ToCpp(CsType cs);
    static CsType FromCppOpt(const CppOptType& cpp);
    static CsType FromCpp(const CppType& cpp) { return FromCppOpt(cpp); }
};
