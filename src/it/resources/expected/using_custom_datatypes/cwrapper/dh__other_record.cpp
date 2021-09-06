// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from using_custom_datatypes.djinni

#include <iostream> // for debugging
#include <cassert>
#include "djinni/cwrapper/wrapper_marshal.hpp"
#include "../cpp-headers/other_record.hpp"

#include "dh__custom_datatype.hpp"
#include "dh__other_record.hpp"

static void(*s_callback_other_record___delete)(DjinniRecordHandle * );
void other_record_add_callback___delete(void(* ptr)(DjinniRecordHandle * )) {
    s_callback_other_record___delete = ptr;
}

void other_record___delete(DjinniRecordHandle * drh) {
    s_callback_other_record___delete(drh);
}
void optional_other_record___delete(DjinniOptionalRecordHandle * drh) {
    s_callback_other_record___delete((DjinniRecordHandle *) drh); // can't static cast, find better way
}
static DjinniRecordHandle * ( * s_callback_other_record_get_other_record_f1)(DjinniRecordHandle *);

void other_record_add_callback_get_other_record_f1(DjinniRecordHandle *( * ptr)(DjinniRecordHandle *)) {
    s_callback_other_record_get_other_record_f1 = ptr;
}

static DjinniRecordHandle * ( * s_callback_other_record_create_other_record)(DjinniRecordHandle *);

void other_record_add_callback_create_other_record(DjinniRecordHandle *( * ptr)(DjinniRecordHandle *)) {
    s_callback_other_record_create_other_record = ptr;
}

djinni::Handle<DjinniRecordHandle> DjinniOtherRecord::fromCpp(const ::OtherRecord& dr) {
    auto  _field_customDatatypeData = DjinniCustomDatatype::fromCpp(dr.customDatatypeData);

    djinni::Handle<DjinniRecordHandle> _aux(
        s_callback_other_record_create_other_record(
            _field_customDatatypeData.release()),
        other_record___delete);
    return _aux;
}

::OtherRecord DjinniOtherRecord::toCpp(djinni::Handle<DjinniRecordHandle> dh) {
    djinni::Handle<DjinniRecordHandle> _field_customDatatypeData(s_callback_other_record_get_other_record_f1(dh.get()), custom_datatype___delete);

    auto _aux = ::OtherRecord(
        DjinniCustomDatatype::toCpp(std::move( _field_customDatatypeData)));
    return _aux;
}

djinni::Handle<DjinniOptionalRecordHandle> DjinniOtherRecord::fromCpp(std::optional<::OtherRecord> dc) {
    if (!dc) {
        return nullptr;
    }
    return djinni::optionals::toOptionalHandle(DjinniOtherRecord::fromCpp(std::move(* dc)), optional_other_record___delete);
}

std::optional<::OtherRecord>DjinniOtherRecord::toCpp(djinni::Handle<DjinniOptionalRecordHandle> dh) {
     if (dh) {
        return std::optional<::OtherRecord>(DjinniOtherRecord::toCpp(djinni::optionals::fromOptionalHandle(std::move(dh), other_record___delete)));
    }
    return {};
}

