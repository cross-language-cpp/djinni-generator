// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from using_custom_datatypes.djinni

#include "other_record.hpp"  // my header
#include "custom_datatype.hpp"

namespace djinni_generated {

auto OtherRecord::toCpp(const JsType& j) -> CppType {
    return {::djinni_generated::CustomDatatype::Boxed::toCpp(j["customDatatypeData"])};
}
auto OtherRecord::fromCpp(const CppType& c) -> JsType {
    em::val js = em::val::object();
    js.set("customDatatypeData", ::djinni_generated::CustomDatatype::Boxed::fromCpp(c.customDatatypeData));
    return js;
}

}  // namespace djinni_generated