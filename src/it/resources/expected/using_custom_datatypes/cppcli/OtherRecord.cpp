// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from using_custom_datatypes.djinni

#include "OtherRecord.hpp"  // my header
#include "djinni/cppcli/Marshal.hpp"
#include <memory>

OtherRecord::OtherRecord(CustomDatatype^ customDatatypeData)
: _customDatatypeData(customDatatypeData)
{}

CustomDatatype^ OtherRecord::CustomDatatypeData::get()
{
    return _customDatatypeData;
}

System::String^ OtherRecord::ToString()
{
    return System::String::Format("OtherRecord {{CustomDatatypeData{0}}}",
                                  CustomDatatypeData);
}

OtherRecord::CppType OtherRecord::ToCpp(OtherRecord::CsType cs)
{
    ASSERT(cs != nullptr);
    return {::CustomDatatype::ToCpp(cs->CustomDatatypeData)};
}

OtherRecord::CsType OtherRecord::FromCpp(const OtherRecord::CppType& cpp)
{
    return gcnew OtherRecord(::CustomDatatype::FromCpp(cpp.customDatatypeData));
}
