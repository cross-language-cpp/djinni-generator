// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from cppcli_circular_dependent_interface.djinni

#include "OneInterface.hpp"  // my header
#include "another_interface.hpp"
#include "djinni/cppcli/Error.hpp"
#include "djinni/cppcli/Marshal.hpp"
#include "djinni/cppcli/WrapperCache.hpp"

ref class OneInterfaceCppProxy : public OneInterface {
    using CppType = std::shared_ptr<::OneInterface>;
    using HandleType = ::djinni::CppProxyCache::Handle<CppType>;
public:
    OneInterfaceCppProxy(const CppType& cppRef) : _cppRefHandle(new HandleType(cppRef)) {}

    void MethodTakingAnotherInterface(AnotherInterface^ dep) override {
        try {
            _cppRefHandle->get()->method_taking_another_interface(::AnotherInterface::ToCpp(dep));
        } DJINNI_TRANSLATE_EXCEPTIONS()
    }

    void MethodTakingOptionalAnotherInterface(AnotherInterface^ dep) override {
        try {
            _cppRefHandle->get()->method_taking_optional_another_interface(::djinni::Optional<std::optional, ::AnotherInterface>::ToCpp(dep));
        } DJINNI_TRANSLATE_EXCEPTIONS()
    }

    AnotherInterface^ MethodReturningAnotherInterface() override {
        try {
            auto cs_result = _cppRefHandle->get()->method_returning_another_interface();
            return ::AnotherInterface::FromCpp(cs_result);
        } DJINNI_TRANSLATE_EXCEPTIONS()
        return nullptr; // Unreachable! (Silencing compiler warnings.)
    }

    AnotherInterface^ MethodReturningOptionalAnotherInterface() override {
        try {
            auto cs_result = _cppRefHandle->get()->method_returning_optional_another_interface();
            return ::djinni::Optional<std::optional, ::AnotherInterface>::FromCpp(cs_result);
        } DJINNI_TRANSLATE_EXCEPTIONS()
        return nullptr; // Unreachable! (Silencing compiler warnings.)
    }

    CppType djinni_private_get_proxied_cpp_object() {
        return _cppRefHandle->get();
    }

private:
    AutoPtr<HandleType> _cppRefHandle;
};

OneInterface::CppType OneInterface::ToCpp(OneInterface::CsType cs)
{
    if (!cs) {
        return nullptr;
    }
    return dynamic_cast<OneInterfaceCppProxy^>(cs)->djinni_private_get_proxied_cpp_object();
}

OneInterface::CsType OneInterface::FromCppOpt(const OneInterface::CppOptType& cpp)
{
    if (!cpp) {
        return nullptr;
    }
    return ::djinni::get_cpp_proxy<OneInterfaceCppProxy^>(cpp);
}
