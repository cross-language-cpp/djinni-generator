// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from requires.djinni

#pragma once

class RequiresAllInterface {
public:
    virtual ~RequiresAllInterface() {}

    virtual bool some_method() = 0;

    class Operators {
    public:
        static bool equals(const RequiresAllInterface& left, const RequiresAllInterface& right);
        static int32_t hash_code(const RequiresAllInterface& object);
        static int32_t compare(const RequiresAllInterface& left, const RequiresAllInterface& right);
    };
};
