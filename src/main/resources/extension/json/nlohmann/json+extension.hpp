#pragma once

#include <optional>
#include <chrono>
#include <sstream>

#include <nlohmann/json.hpp>

namespace nlohmann {

    template <typename T>
    struct adl_serializer<std::optional<T>> {
        static inline void to_json(json& j, const std::optional<T>& opt) {
            if (opt == std::nullopt) {
                j = nullptr;
            } else {
              j = *opt;
            }
        }

        static inline void from_json(const json& j, std::optional<T>& opt) {
            if (j.is_null()) {
                opt = std::nullopt;
            } else {
                opt = j.get<T>();
            }
        }
    };

#ifndef DDJINNI_CUSTOM_JSON_DATE
    template <typename Clock, typename Duration>
    struct adl_serializer<std::chrono::time_point<Clock, Duration>>
    {
        static inline void to_json(json &j, const std::chrono::time_point<Clock, Duration>& tp) {
            j = std::chrono::duration_cast<std::chrono::milliseconds>(tp.time_since_epoch()).count();
        }

        static inline void from_json(const json &j, std::chrono::time_point<Clock, Duration>& value) {
            if (j.is_null()) {
                auto dur = std::chrono::milliseconds(0);
                value = std::chrono::time_point<std::chrono::system_clock>(dur);
            } else {
                auto dur = std::chrono::milliseconds(j);
                value = std::chrono::time_point<std::chrono::system_clock>(dur);
            }
        }
    };
#endif
}
