#include <jni.h>
#include <cstdint>

extern "C" int32_t calendar_native_add(int32_t a, int32_t b);

extern "C" JNIEXPORT jint JNICALL
Java_com_ohyooo_calendar_NativeBridge_add(JNIEnv *, jobject thiz, jint a, jint b) {
    return calendar_native_add(static_cast<int32_t>(a), static_cast<int32_t>(b));
}
