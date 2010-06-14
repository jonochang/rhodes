#include "rhodes/JNIRhodes.h"

#include <android/log.h>

#include "rhodes/jni/com_rhomobile_rhodes_barcode_Barcode.h"
#include <common/RhodesApp.h>

#undef  DEFAULT_LOGCATEGORY
#define DEFAULT_LOGCATEGORY "Barcode"

JNIEXPORT void JNICALL Java_com_rhomobile_rhodes_barcode_Barcode_callback
(JNIEnv *, jclass, jstring callback, jstring result, jstring error, jboolean cancelled)
{
    rho_rhodesapp_callBarcodeScanImageCallback(rho_cast<std::string>(callback).c_str(),
					       rho_cast<std::string>(result).c_str(), 
					       rho_cast<std::string>(error).c_str(), 
					       cancelled);
}

RHO_GLOBAL void rho_barcode_scan_image(const char* callback_url, const char *file_path, int withPreview)
{
  __android_log_print (ANDROID_LOG_INFO, DEFAULT_LOGCATEGORY, "%s(): %s, %s. %d", __FUNCTION__, callback_url, file_path, withPreview);
    JNIEnv *env = jnienv();
    jclass cls = getJNIClass(RHODES_JAVA_CLASS_BARCODE);
    if (!cls) return;
    jmethodID mid = getJNIClassStaticMethod(env, cls, "scanImage", 
					    "(Ljava/lang/String;Ljava/lang/String;)V");
    if (!mid) return;

    jstring jstrCallbackUrl = rho_cast<jstring>(callback_url);
    jstring jstrFilePath = rho_cast<jstring>(file_path);

    env->CallStaticVoidMethod(cls, mid, jstrCallbackUrl, jstrFilePath);
    env->DeleteLocalRef(jstrCallbackUrl);
    env->DeleteLocalRef(jstrFilePath);
}

RHO_GLOBAL void rho_barcode_scan_camera(const char* callback_url)
{
}
