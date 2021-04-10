// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from my_record.djinni

#include "MyRecord.hpp"  // my header
#include "Marshal.hpp"
#include <memory>

MyRecord::MyRecord(int id,
                   System::String^ info,
                   System::Collections::Generic::HashSet<System::String^>^ store,
                   System::Collections::Generic::Dictionary<System::String^, int>^ hash)
: _id(id)
, _info(info)
, _store(store)
, _hash(hash)
{}

int MyRecord::Id::get()
{
    return _id;
}

System::String^ MyRecord::Info::get()
{
    return _info;
}

System::Collections::Generic::HashSet<System::String^>^ MyRecord::Store::get()
{
    return _store;
}

System::Collections::Generic::Dictionary<System::String^, int>^ MyRecord::Hash::get()
{
    return _hash;
}

System::String^ MyRecord::ToString()
{
    return System::String::Format("MyRecord {{Id{0}, Info{1}, Store{2}, Hash{3}}}",
                                  Id,
                                  Info,
                                  Store,
                                  Hash);
}

MyRecord::CppType MyRecord::ToCpp(MyRecord::CsType cs)
{
    ASSERT(cs != nullptr);
    return {::djinni::I32::ToCpp(cs->Id),
            ::djinni::String::ToCpp(cs->Info),
            ::djinni::Set<::djinni::String>::ToCpp(cs->Store),
            ::djinni::Map<::djinni::String, ::djinni::I32>::ToCpp(cs->Hash)};
}

MyRecord::CsType MyRecord::FromCpp(const MyRecord::CppType& cpp)
{
    return gcnew MyRecord(::djinni::I32::FromCpp(cpp.id),
                          ::djinni::String::FromCpp(cpp.info),
                          ::djinni::Set<::djinni::String>::FromCpp(cpp.store),
                          ::djinni::Map<::djinni::String, ::djinni::I32>::FromCpp(cpp.hash));
}
