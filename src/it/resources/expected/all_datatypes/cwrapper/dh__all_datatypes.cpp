// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from all_datatypes.djinni

#include <iostream> // for debugging
#include <cassert>
#include "djinni/cwrapper/wrapper_marshal.hpp"
#include "../cpp-headers/all_datatypes.hpp"

#include "dh__all_datatypes.hpp"
#include "dh__list_bool.hpp"
#include "dh__map_int8_t_bool.hpp"
#include "dh__set_bool.hpp"
#include <chrono>
#include <optional>
#include <vector>

static void(*s_callback_all_datatypes___delete)(DjinniRecordHandle * );
void all_datatypes_add_callback___delete(void(* ptr)(DjinniRecordHandle * )) {
    s_callback_all_datatypes___delete = ptr;
}

void all_datatypes___delete(DjinniRecordHandle * drh) {
    s_callback_all_datatypes___delete(drh);
}
void optional_all_datatypes___delete(DjinniOptionalRecordHandle * drh) {
    s_callback_all_datatypes___delete((DjinniRecordHandle *) drh); // can't static cast, find better way
}
static bool ( * s_callback_all_datatypes_get_all_datatypes_f1)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f1(bool( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f1 = ptr;
}

static int8_t ( * s_callback_all_datatypes_get_all_datatypes_f2)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f2(int8_t( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f2 = ptr;
}

static int16_t ( * s_callback_all_datatypes_get_all_datatypes_f3)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f3(int16_t( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f3 = ptr;
}

static int32_t ( * s_callback_all_datatypes_get_all_datatypes_f4)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f4(int32_t( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f4 = ptr;
}

static int64_t ( * s_callback_all_datatypes_get_all_datatypes_f5)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f5(int64_t( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f5 = ptr;
}

static float ( * s_callback_all_datatypes_get_all_datatypes_f6)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f6(float( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f6 = ptr;
}

static double ( * s_callback_all_datatypes_get_all_datatypes_f7)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f7(double( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f7 = ptr;
}

static DjinniString * ( * s_callback_all_datatypes_get_all_datatypes_f8)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f8(DjinniString *( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f8 = ptr;
}

static DjinniBinary * ( * s_callback_all_datatypes_get_all_datatypes_f9)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f9(DjinniBinary *( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f9 = ptr;
}

static uint64_t ( * s_callback_all_datatypes_get_all_datatypes_f10)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f10(uint64_t( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f10 = ptr;
}

static DjinniObjectHandle * ( * s_callback_all_datatypes_get_all_datatypes_f11)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f11(DjinniObjectHandle *( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f11 = ptr;
}

static DjinniObjectHandle * ( * s_callback_all_datatypes_get_all_datatypes_f12)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f12(DjinniObjectHandle *( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f12 = ptr;
}

static DjinniObjectHandle * ( * s_callback_all_datatypes_get_all_datatypes_f13)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f13(DjinniObjectHandle *( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f13 = ptr;
}

static DjinniBoxedBool * ( * s_callback_all_datatypes_get_all_datatypes_f14)(DjinniRecordHandle *);

void all_datatypes_add_callback_get_all_datatypes_f14(DjinniBoxedBool *( * ptr)(DjinniRecordHandle *)) {
    s_callback_all_datatypes_get_all_datatypes_f14 = ptr;
}

static DjinniRecordHandle * ( * s_callback_all_datatypes_create_all_datatypes)(bool, int8_t, int16_t, int32_t, int64_t, float, double, DjinniString *, DjinniBinary *, uint64_t, DjinniObjectHandle *, DjinniObjectHandle *, DjinniObjectHandle *, DjinniBoxedBool *);

void all_datatypes_add_callback_create_all_datatypes(DjinniRecordHandle *( * ptr)(bool, int8_t, int16_t, int32_t, int64_t, float, double, DjinniString *, DjinniBinary *, uint64_t, DjinniObjectHandle *, DjinniObjectHandle *, DjinniObjectHandle *, DjinniBoxedBool *)) {
    s_callback_all_datatypes_create_all_datatypes = ptr;
}

djinni::Handle<DjinniRecordHandle> DjinniAllDatatypes::fromCpp(const ::AllDatatypes& dr) {
    auto  _field_stringData = DjinniString::fromCpp(dr.stringData);
    auto  _field_binaryData = DjinniBinary::fromCpp(dr.binaryData);
    auto  _field_listData = DjinniListBool::fromCpp(dr.listData);
    auto  _field_setData = DjinniSetBool::fromCpp(dr.setData);
    auto  _field_mapData = DjinniMapInt8TBool::fromCpp(dr.mapData);
    auto  _field_optionalData = DjinniBoxedBool::fromCpp(dr.optionalData);

    djinni::Handle<DjinniRecordHandle> _aux(
        s_callback_all_datatypes_create_all_datatypes(
            dr.booleanData,
            dr.integer8Data,
            dr.integer16Data,
            dr.integer32Data,
            dr.integer64Data,
            dr.float32Data,
            dr.float64Data,
            _field_stringData.release(),
            _field_binaryData.release(),
            DjinniDate::fromCpp(dr.dateData),
            _field_listData.release(),
            _field_setData.release(),
            _field_mapData.release(),
            _field_optionalData.release()),
        all_datatypes___delete);
    return _aux;
}

::AllDatatypes DjinniAllDatatypes::toCpp(djinni::Handle<DjinniRecordHandle> dh) {
    std::unique_ptr<DjinniString> _field_stringData(s_callback_all_datatypes_get_all_datatypes_f8(dh.get()));
    std::unique_ptr<DjinniBinary> _field_binaryData(s_callback_all_datatypes_get_all_datatypes_f9(dh.get()));
    djinni::Handle<DjinniObjectHandle> _field_listData(s_callback_all_datatypes_get_all_datatypes_f11(dh.get()), list_bool___delete);
    djinni::Handle<DjinniObjectHandle> _field_setData(s_callback_all_datatypes_get_all_datatypes_f12(dh.get()), set_bool___delete);
    djinni::Handle<DjinniObjectHandle> _field_mapData(s_callback_all_datatypes_get_all_datatypes_f13(dh.get()), map_int8_t_bool___delete);
    std::unique_ptr<DjinniBoxedBool> _field_optionalData(s_callback_all_datatypes_get_all_datatypes_f14(dh.get()));

    auto _aux = ::AllDatatypes(
        s_callback_all_datatypes_get_all_datatypes_f1(dh.get()),
        s_callback_all_datatypes_get_all_datatypes_f2(dh.get()),
        s_callback_all_datatypes_get_all_datatypes_f3(dh.get()),
        s_callback_all_datatypes_get_all_datatypes_f4(dh.get()),
        s_callback_all_datatypes_get_all_datatypes_f5(dh.get()),
        s_callback_all_datatypes_get_all_datatypes_f6(dh.get()),
        s_callback_all_datatypes_get_all_datatypes_f7(dh.get()),
        DjinniString::toCpp(std::move( _field_stringData)),
        DjinniBinary::toCpp(std::move( _field_binaryData)),
        DjinniDate::toCpp(s_callback_all_datatypes_get_all_datatypes_f10(dh.get())),
        DjinniListBool::toCpp(std::move( _field_listData)),
        DjinniSetBool::toCpp(std::move( _field_setData)),
        DjinniMapInt8TBool::toCpp(std::move( _field_mapData)),
        DjinniBoxedBool::toCpp(std::move( _field_optionalData)));
    return _aux;
}

djinni::Handle<DjinniOptionalRecordHandle> DjinniAllDatatypes::fromCpp(std::optional<::AllDatatypes> dc) {
    if (!dc) {
        return nullptr;
    }
    return djinni::optionals::toOptionalHandle(DjinniAllDatatypes::fromCpp(std::move(* dc)), optional_all_datatypes___delete);
}

std::optional<::AllDatatypes>DjinniAllDatatypes::toCpp(djinni::Handle<DjinniOptionalRecordHandle> dh) {
     if (dh) {
        return std::optional<::AllDatatypes>(DjinniAllDatatypes::toCpp(djinni::optionals::fromOptionalHandle(std::move(dh), all_datatypes___delete)));
    }
    return {};
}

