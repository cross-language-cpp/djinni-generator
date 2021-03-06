// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_client_interface.djinni

#import "ITMyClientInterface+Private.h"
#import "ITMyClientInterface.h"
#import "djinni/objc/DJIMarshal+Private.h"
#import "djinni/objc/DJIObjcWrapperCache+Private.h"
#include <stdexcept>

static_assert(__has_feature(objc_arc), "Djinni requires ARC to be enabled for this file");

namespace djinni_generated {

class MyClientInterface::ObjcProxy final
: public ::MyClientInterface
, private ::djinni::ObjcProxyBase<ObjcType>
{
    friend class ::djinni_generated::MyClientInterface;
public:
    using ObjcProxyBase::ObjcProxyBase;
    bool log_string(const std::string & c_str) override
    {
        @autoreleasepool {
            auto objcpp_result_ = [djinni_private_get_proxied_objc_object() logString:(::djinni::String::fromCpp(c_str))];
            return ::djinni::Bool::toCpp(objcpp_result_);
        }
    }
};

}  // namespace djinni_generated

namespace djinni_generated {

auto MyClientInterface::toCpp(ObjcType objc) -> CppType
{
    if (!objc) {
        return nullptr;
    }
    return ::djinni::get_objc_proxy<ObjcProxy>(objc);
}

auto MyClientInterface::fromCppOpt(const CppOptType& cpp) -> ObjcType
{
    if (!cpp) {
        return nil;
    }
    return dynamic_cast<ObjcProxy&>(*cpp).djinni_private_get_proxied_objc_object();
}

}  // namespace djinni_generated
