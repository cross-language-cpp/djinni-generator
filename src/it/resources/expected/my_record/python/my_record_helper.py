# AUTOGENERATED FILE - DO NOT MODIFY!
# This file was generated by Djinni from my_record.djinni

from djinni.support import MultiSet # default imported in all files
from djinni.exception import CPyException # default imported in all files
from djinni.pycffi_marshal import CPyObject, CPyObjectProxy, CPyPrimitive, CPyRecord, CPyString

from dh__map_string_int32_t import MapStringInt32THelper
from dh__map_string_int32_t import MapStringInt32TProxy
from dh__set_string import SetStringHelper
from dh__set_string import SetStringProxy
from _cffi import ffi, lib

from djinni import exception # this forces run of __init__.py which gives cpp option to call back into py to create exception

from my_record import MyRecord

class MyRecordHelper:
    @staticmethod
    def release(c_ptr):
        assert c_ptr in c_data_set
        c_data_set.remove(ffi.cast("void*", c_ptr))

    @ffi.callback("int32_t(struct DjinniRecordHandle *)")
    def get_my_record_f1(cself):
        try:
            _ret = CPyPrimitive.fromPy(CPyRecord.toPy(None, cself).id)
            return _ret
        except Exception as _djinni_py_e:
            CPyException.setExceptionFromPy(_djinni_py_e)
            return ffi.NULL

    @ffi.callback("struct DjinniString *(struct DjinniRecordHandle *)")
    def get_my_record_f2(cself):
        try:
            with CPyString.fromPy(CPyRecord.toPy(None, cself).info) as py_obj:
                _ret = py_obj.release_djinni_string()
                assert _ret != ffi.NULL
                return _ret
        except Exception as _djinni_py_e:
            CPyException.setExceptionFromPy(_djinni_py_e)
            return ffi.NULL

    @ffi.callback("struct DjinniObjectHandle *(struct DjinniRecordHandle *)")
    def get_my_record_f3(cself):
        try:
            _ret = CPyObjectProxy.fromPy(SetStringHelper.c_data_set, SetStringProxy(CPyRecord.toPy(None, cself).store))
            assert _ret != ffi.NULL
            return _ret
        except Exception as _djinni_py_e:
            CPyException.setExceptionFromPy(_djinni_py_e)
            return ffi.NULL

    @ffi.callback("struct DjinniObjectHandle *(struct DjinniRecordHandle *)")
    def get_my_record_f4(cself):
        try:
            _ret = CPyObjectProxy.fromPy(MapStringInt32THelper.c_data_set, MapStringInt32TProxy(CPyRecord.toPy(None, cself).hash))
            assert _ret != ffi.NULL
            return _ret
        except Exception as _djinni_py_e:
            CPyException.setExceptionFromPy(_djinni_py_e)
            return ffi.NULL

    @ffi.callback("struct DjinniRecordHandle *(int32_t,struct DjinniString *,struct DjinniObjectHandle *,struct DjinniObjectHandle *)")
    def create_my_record(id,info,store,hash):
        py_rec = MyRecord(
            CPyPrimitive.toPy(id),
            CPyString.toPy(info),
            CPyObjectProxy.toPyObj(SetStringHelper.c_data_set, store),
            CPyObjectProxy.toPyObj(MapStringInt32THelper.c_data_set, hash))
        return CPyRecord.fromPy(MyRecord.c_data_set, py_rec) #to do: can be optional?

    @ffi.callback("void (struct DjinniRecordHandle *)")
    def __delete(dh):
        assert dh in MyRecord.c_data_set
        MyRecord.c_data_set.remove(dh)

    @staticmethod
    def _add_callbacks():
        lib.my_record_add_callback_get_my_record_f4(MyRecordHelper.get_my_record_f4)
        lib.my_record_add_callback_get_my_record_f1(MyRecordHelper.get_my_record_f1)
        lib.my_record_add_callback_get_my_record_f2(MyRecordHelper.get_my_record_f2)
        lib.my_record_add_callback_get_my_record_f3(MyRecordHelper.get_my_record_f3)
        lib.my_record_add_callback_create_my_record(MyRecordHelper.create_my_record)
        lib.my_record_add_callback___delete(MyRecordHelper.__delete)


MyRecordHelper._add_callbacks()

