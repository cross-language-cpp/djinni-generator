// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_enum.djinni

#include <iostream> // for debugging
#include <cassert>
#include "djinni/cwrapper/wrapper_marshal.hpp"
#include "../cpp-headers/my_enum.hpp"

int32_t int32_from_enum_my_enum(std::optional<::MyEnum> e) {
    if (e) {
        return static_cast<int32_t>(* e);
    }
    return -1; // -1 to signal null boxed enum
}

int32_t int32_from_enum_my_enum(::MyEnum e) {
    return static_cast<int32_t>(e);
}
std::optional<::MyEnum> get_boxed_enum_my_enum_from_int32(int32_t e) {
    if (e == -1) { // to signal null enum
        return std::experimental::nullopt;
    }
    return std::optional<::MyEnum>(static_cast<::MyEnum>(e));
}
