// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_cpp_interface.djinni

#include "my_cpp_interface.hpp"
#include <memory>

static_assert(__has_feature(objc_arc), "Djinni requires ARC to be enabled for this file");

@class ITMyCppInterface;

namespace djinni_generated {

class MyCppInterface
{
public:
    using CppType = std::shared_ptr<::MyCppInterface>;
    using CppOptType = std::shared_ptr<::MyCppInterface>;
    using ObjcType = ::ITMyCppInterface*;

    using Boxed = MyCppInterface;

    static CppType toCpp(ObjcType objc);
    static ObjcType fromCppOpt(const CppOptType& cpp);
    static ObjcType fromCpp(const CppType& cpp) { return fromCppOpt(cpp); }

private:
    class ObjcProxy;
};

}  // namespace djinni_generated

