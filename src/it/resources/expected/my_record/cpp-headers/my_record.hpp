// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_record.djinni

#pragma once

#include <cstdint>
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <utility>

/** record comment */
struct MyRecord final {

    static std::string const STRING_CONST;
    /** record property comment */
    int32_t id;
    std::string info;
    std::unordered_set<std::string> store;
    std::unordered_map<std::string, int32_t> hash;

    MyRecord(int32_t id_,
             std::string info_,
             std::unordered_set<std::string> store_,
             std::unordered_map<std::string, int32_t> hash_)
    : id(std::move(id_))
    , info(std::move(info_))
    , store(std::move(store_))
    , hash(std::move(hash_))
    {}

    MyRecord()
    : id()
    , info()
    , store()
    , hash()
    {}
};
