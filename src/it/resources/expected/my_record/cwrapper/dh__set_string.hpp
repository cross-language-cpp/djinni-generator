// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_record.djinni

#pragma once

#include <atomic>
#include <optional>
#include "../cpp-headers/my_record.hpp"
#ifdef __cplusplus
extern "C" {
#endif

#include "../cwrapper-headers/dh__set_string.h"

#ifdef __cplusplus
}
#endif
struct DjinniSetString {
    static djinni::Handle<DjinniObjectHandle> fromCpp(const std::unordered_set<std::string> & dc);
    static std::unordered_set<std::string> toCpp(djinni::Handle<DjinniObjectHandle> dh);
    static djinni::Handle<DjinniOptionalObjectHandle>fromCpp(std::optional<std::unordered_set<std::string>> dc);
    static std::optional<std::unordered_set<std::string>> toCpp(djinni::Handle<DjinniOptionalObjectHandle> dh);
};