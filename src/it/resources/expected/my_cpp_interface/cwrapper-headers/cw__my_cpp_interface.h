// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_cpp_interface.djinni

#pragma once // python_cdef_ignore
#include <stdbool.h>  // python_cdef_ignore

#include <stdint.h> // python_cdef_ignore

struct DjinniWrapperMyCppInterface;
void my_cpp_interface___wrapper_dec_ref(struct DjinniWrapperMyCppInterface * dh);
void my_cpp_interface___wrapper_add_ref(struct DjinniWrapperMyCppInterface * dh);

void cw__my_cpp_interface_method_returning_nothing(struct DjinniWrapperMyCppInterface * djinni_this, int32_t value);

int32_t cw__my_cpp_interface_method_returning_some_type(struct DjinniWrapperMyCppInterface * djinni_this, struct DjinniString * key);

int32_t cw__my_cpp_interface_method_changing_nothing(struct DjinniWrapperMyCppInterface * djinni_this);

int32_t cw__my_cpp_interface_get_version(void);

