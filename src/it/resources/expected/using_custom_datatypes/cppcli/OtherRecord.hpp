// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from using_custom_datatypes.djinni

#pragma once

#include "../cpp-headers/other_record.hpp"
#include "CustomDatatype.hpp"

[System::Serializable]
public ref class OtherRecord {
public:

    property CustomDatatype^ CustomDatatypeData
    {
        CustomDatatype^ get();
    }

    OtherRecord(CustomDatatype^ customDatatypeData);

    System::String^ ToString() override;

internal:
    using CppType = ::OtherRecord;
    using CsType = OtherRecord^;

    static CppType ToCpp(CsType cs);
    static CsType FromCpp(const CppType& cpp);

private:
    CustomDatatype^ _customDatatypeData;
};
