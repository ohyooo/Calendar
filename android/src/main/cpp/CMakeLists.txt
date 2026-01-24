cmake_minimum_required(VERSION 3.22.1)
project(calendar_jni)

add_library(calendar_jni SHARED native-lib.cpp)

add_library(calendar_native SHARED IMPORTED)
set_target_properties(calendar_native PROPERTIES
    IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libcalendar_native.so"
)

target_link_libraries(calendar_jni
    android
    log
    calendar_native
)
