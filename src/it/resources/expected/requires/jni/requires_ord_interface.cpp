// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from requires.djinni

#include "requires_ord_interface.hpp"  // my header
#include "djinni/jni/Marshal.hpp"

namespace djinni_generated {

RequiresOrdInterface::RequiresOrdInterface() : ::djinni::JniInterface<::RequiresOrdInterface, RequiresOrdInterface>("djinni/it/RequiresOrdInterface$CppProxy") {}

RequiresOrdInterface::~RequiresOrdInterface() = default;


CJNIEXPORT void JNICALL Java_djinni_it_RequiresOrdInterface_00024CppProxy_nativeDestroy(JNIEnv* jniEnv, jobject /*this*/, jlong nativeRef)
{
    try {
        DJINNI_FUNCTION_PROLOGUE1(jniEnv, nativeRef);
        delete reinterpret_cast<::djinni::CppProxyHandle<::RequiresOrdInterface>*>(nativeRef);
    } JNI_TRANSLATE_EXCEPTIONS_RETURN(jniEnv, )
}

CJNIEXPORT jboolean JNICALL Java_djinni_it_RequiresOrdInterface_00024CppProxy_native_1someMethod(JNIEnv* jniEnv, jobject /*this*/, jlong nativeRef)
{
    try {
        DJINNI_FUNCTION_PROLOGUE1(jniEnv, nativeRef);
        const auto& ref = ::djinni::objectFromHandleAddress<::RequiresOrdInterface>(nativeRef);
        auto r = ref->some_method();
        return ::djinni::release(::djinni::Bool::fromCpp(jniEnv, r));
    } JNI_TRANSLATE_EXCEPTIONS_RETURN(jniEnv, 0 /* value doesn't matter */)
}
CJNIEXPORT jint JNICALL Java_djinni_it_RequiresOrdInterface_00024CppProxy_native_1compare(JNIEnv* jniEnv, jobject /*this*/, jlong nativeRef, jobject j_obj)
{
    try {
        DJINNI_FUNCTION_PROLOGUE1(jniEnv, nativeRef);
        const auto& ref = ::djinni::objectFromHandleAddress<::RequiresOrdInterface>(nativeRef);
        const auto& otherRef = ::djinni_generated::RequiresOrdInterface::toCpp(jniEnv, j_obj);
        auto r = ::RequiresOrdInterface::Operators::compare(*ref, *otherRef);
        return ::djinni::release(::djinni::I32::fromCpp(jniEnv, r));
    } JNI_TRANSLATE_EXCEPTIONS_RETURN(jniEnv, 0 /* value doesn't matter */)
}

}  // namespace djinni_generated
