// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from my_record.djinni

#pragma once // python_cdef_ignore
#include <stdbool.h>  // python_cdef_ignore

#include <stdint.h> // python_cdef_ignore

struct DjinniMyRecord;
void my_record___delete(struct DjinniRecordHandle * );
void optional_my_record___delete(struct DjinniOptionalRecordHandle * );
void my_record_add_callback_get_my_record_f1(int32_t( * ptr)(struct DjinniRecordHandle *));
void my_record_add_callback_get_my_record_f2(struct DjinniString *( * ptr)(struct DjinniRecordHandle *));
void my_record_add_callback_get_my_record_f3(struct DjinniObjectHandle *( * ptr)(struct DjinniRecordHandle *));
void my_record_add_callback_get_my_record_f4(struct DjinniObjectHandle *( * ptr)(struct DjinniRecordHandle *));
void my_record_add_callback_get_my_record_f5(int( * ptr)(struct DjinniRecordHandle *));
void my_record_add_callback_create_my_record(struct DjinniRecordHandle *( * ptr)(int32_t, struct DjinniString *, struct DjinniObjectHandle *, struct DjinniObjectHandle *, int));
void my_record_add_callback___delete(void( * ptr)(struct DjinniRecordHandle *));
