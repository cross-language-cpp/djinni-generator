// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_record.djinni

#import "ITMyRecord.h"
#include "my_record.hpp"

static_assert(__has_feature(objc_arc), "Djinni requires ARC to be enabled for this file");

@class ITMyRecord;

namespace djinni_generated {

struct MyRecord
{
    using CppType = ::MyRecord;
    using ObjcType = ::ITMyRecord*;

    using Boxed = MyRecord;

    static CppType toCpp(ObjcType objc);
    static ObjcType fromCpp(const CppType& cpp);
};

}  // namespace djinni_generated
