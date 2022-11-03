// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from all_datatypes_json.djinni

#pragma once

#include "json+extension.hpp"
#include "my_flags.hpp"
#include <algorithm>
#include <nlohmann/json.hpp>

inline void to_json(nlohmann::json& j, my_flags e)
{
    static const std::pair<my_flags, nlohmann::json> m[] = {{my_flags::FLAG1,"flag1"},{my_flags::FLAG2,"flag2"},{my_flags::FLAG3,"flag3"}};
    j = nlohmann::json::array();
    for(const auto& flagOption : m)
    {
        if(static_cast<unsigned>(e & flagOption.first) != 0)
        {
            j.push_back(flagOption.second);
        }
    }
}
inline void from_json(const nlohmann::json& j, my_flags& e)
{
    static const std::pair<my_flags, nlohmann::json> m[] = {{my_flags::FLAG1,"flag1"},{my_flags::FLAG2,"flag2"},{my_flags::FLAG3,"flag3"}};
    e = static_cast<my_flags>(0);
    for(const auto& flagName : j)
    {
        auto it = std::find_if(std::begin(m), std::end(m),
                               [flagName](const std::pair<my_flags, nlohmann::json>& ej_pair) -> bool
        {
            return ej_pair.second == flagName;
        });
        if(it != std::end(m))
        {
            e |= it->first;
        }
    }
}
