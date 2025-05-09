// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from requires.djinni

#pragma once

#include "djinni/jni/djinni_support.hpp"
#include "requires_ord_interface.hpp"

namespace djinni_generated {

class RequiresOrdInterface final : ::djinni::JniInterface<::RequiresOrdInterface, RequiresOrdInterface> {
public:
    using CppType = std::shared_ptr<::RequiresOrdInterface>;
    using CppOptType = std::shared_ptr<::RequiresOrdInterface>;
    using JniType = jobject;

    using Boxed = RequiresOrdInterface;

    ~RequiresOrdInterface();

    static CppType toCpp(JNIEnv* jniEnv, JniType j) { return ::djinni::JniClass<RequiresOrdInterface>::get()._fromJava(jniEnv, j); }
    static ::djinni::LocalRef<JniType> fromCppOpt(JNIEnv* jniEnv, const CppOptType& c) { return {jniEnv, ::djinni::JniClass<RequiresOrdInterface>::get()._toJava(jniEnv, c)}; }
    static ::djinni::LocalRef<JniType> fromCpp(JNIEnv* jniEnv, const CppType& c) { return fromCppOpt(jniEnv, c); }

private:
    RequiresOrdInterface();
    friend ::djinni::JniClass<RequiresOrdInterface>;
    friend ::djinni::JniInterface<::RequiresOrdInterface, RequiresOrdInterface>;

};

}  // namespace djinni_generated
