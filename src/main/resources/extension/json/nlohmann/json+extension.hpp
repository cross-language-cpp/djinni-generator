#pragma once

#include <optional>
#include <chrono>
#include <sstream>

#include <nlohmann/json.hpp>
#include <date/date.h>

namespace nlohmann {

    template <typename T>
    struct adl_serializer<std::optional<T>> {
        static void to_json(json& j, const std::optional<T>& opt) {
            if (opt == std::nullopt) {
                j = nullptr;
            } else {
              j = *opt;
            }
        }

        static void from_json(const json& j, std::optional<T>& opt) {
            if (j.is_null()) {
                opt = std::nullopt;
            } else {
                opt = j.get<T>();
            }
        }
    };

    template <>
    struct adl_serializer<std::chrono::system_clock::time_point>
    {
        static void to_json(json &j, const std::chrono::system_clock::time_point& tp) {
            j = date::format("%F %T %Z", tp);
        }

        static void from_json(const json &j, std::chrono::system_clock::time_point& value) {
            if (j.is_null()) {
                auto dur = std::chrono::milliseconds(0);
                value = std::chrono::time_point<std::chrono::system_clock>(dur);
            } else {
                std::istringstream json_time{j.get<std::string>()};
                std::chrono::system_clock::time_point parsed_time{};
                // Time saved in UTC, so no need to extract time zone
                json_time >> date::parse("%F %T", value);
            }
        }
    };
}
