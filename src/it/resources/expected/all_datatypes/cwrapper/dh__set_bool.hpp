// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from all_datatypes.djinni

#pragma once

#include <atomic>
#include <optional>
#include "../cpp-headers/all_datatypes.hpp"
#ifdef __cplusplus
extern "C" {
#endif

#include "../cwrapper-headers/dh__set_bool.h"

#ifdef __cplusplus
}
#endif
struct DjinniSetBool {
    static djinni::Handle<DjinniObjectHandle> fromCpp(const std::unordered_set<bool> & dc);
    static std::unordered_set<bool> toCpp(djinni::Handle<DjinniObjectHandle> dh);
    static djinni::Handle<DjinniOptionalObjectHandle>fromCpp(std::optional<std::unordered_set<bool>> dc);
    static std::optional<std::unordered_set<bool>> toCpp(djinni::Handle<DjinniOptionalObjectHandle> dh);
};
