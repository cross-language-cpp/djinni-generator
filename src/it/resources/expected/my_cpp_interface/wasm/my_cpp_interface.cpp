// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_cpp_interface.djinni

#include "my_cpp_interface.hpp"  // my header

namespace djinni_generated {

em::val MyCppInterface::cppProxyMethods() {
    static const em::val methods = em::val::array(std::vector<std::string> {
        "methodReturningNothing",
        "methodReturningSomeType",
        "methodChangingNothing",
    });
    return methods;
}

void MyCppInterface::method_returning_nothing(const CppType& self, int32_t w_value) {
    try {
        self->method_returning_nothing(::djinni::I32::toCpp(w_value));
    }
    catch(const std::exception& e) {
        return ::djinni::ExceptionHandlingTraits<void>::handleNativeException(e);
    }
}
int32_t MyCppInterface::method_returning_some_type(const CppType& self, const std::string& w_key) {
    try {
        auto r = self->method_returning_some_type(::djinni::String::toCpp(w_key));
        return ::djinni::I32::fromCpp(r);
    }
    catch(const std::exception& e) {
        return ::djinni::ExceptionHandlingTraits<::djinni::I32>::handleNativeException(e);
    }
}
int32_t MyCppInterface::method_changing_nothing(const CppType& self) {
    try {
        auto r = self->method_changing_nothing();
        return ::djinni::I32::fromCpp(r);
    }
    catch(const std::exception& e) {
        return ::djinni::ExceptionHandlingTraits<::djinni::I32>::handleNativeException(e);
    }
}
int32_t MyCppInterface::get_version() {
    try {
        auto r = ::MyCppInterface::get_version();
        return ::djinni::I32::fromCpp(r);
    }
    catch(const std::exception& e) {
        return ::djinni::ExceptionHandlingTraits<::djinni::I32>::handleNativeException(e);
    }
}

EMSCRIPTEN_BINDINGS(_my_cpp_interface) {
    em::class_<::MyCppInterface>("MyCppInterface")
        .smart_ptr<std::shared_ptr<::MyCppInterface>>("MyCppInterface")
        .function("nativeDestroy", &MyCppInterface::nativeDestroy)
        .function("methodReturningNothing", MyCppInterface::method_returning_nothing)
        .function("methodReturningSomeType", MyCppInterface::method_returning_some_type)
        .function("methodChangingNothing", MyCppInterface::method_changing_nothing)
        .class_function("getVersion", MyCppInterface::get_version)
        ;
}

namespace {
    EM_JS(void, djinni_init__my_cpp_interface_consts, (), {
        if (!('MyCppInterface' in Module)) {
            Module.MyCppInterface = {};
        }
        Module.MyCppInterface.VERSION = 1;
    })
}
void MyCppInterface::staticInitializeConstants() {
    static std::once_flag initOnce;
    std::call_once(initOnce, [] {
        djinni_init__my_cpp_interface_consts();
    });
}

EMSCRIPTEN_BINDINGS(_my_cpp_interface_consts) {
    MyCppInterface::staticInitializeConstants();
}

}  // namespace djinni_generated