#include <jni.h>
#include <android/log.h>
#include <pthread.h>

#define HACKER_JNI_VERSION    JNI_VERSION_1_6

/* 定义线程pthread */
void *pthreadtest() {
    __android_log_print(ANDROID_LOG_INFO, "mooner", "%s", "SIGSEGV");
    raise(SIGSEGV);
}

JNIEXPORT void JNICALL
Java_com_pika_mooner_MainActivity_createThreadCrash(JNIEnv *env, jobject thiz) {
    pthread_t tidp;
    if (pthread_create(&tidp, NULL, pthreadtest, NULL)) {
        __android_log_print(ANDROID_LOG_INFO, "mooner", "%s", "pthread_create fail");
    }
}

JNIEXPORT void JNICALL
Java_com_pika_mooner_MainActivity_createDestroyedPthreadMutex(JNIEnv *env, jobject thiz) {
    android_get_device_api_level();
    __android_log_print(ANDROID_LOG_INFO, "mooner", "%s", "create_pthread_mutex");
    pthread_mutex_t t;
    pthread_mutex_init(&t,NULL);
    pthread_mutex_destroy(&t);
    struct timespec timer  ={2,2};
    pthread_mutex_timedlock(&t,&timer);
    //pthread_mutex_unlock(&t);

}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    (void)reserved;

    if (NULL == vm) return JNI_ERR;
    __android_log_print(ANDROID_LOG_INFO, "mooner", "%s", "JNI_OnLoad SIGSEGV");
    raise(SIGSEGV);
    JNIEnv *env;
    if (JNI_OK != (*vm)->GetEnv(vm, (void **)&env, HACKER_JNI_VERSION)) return JNI_ERR;
    if (NULL == env || NULL == *env) return JNI_ERR;

  /*  jclass cls;
    if (NULL == (cls = (*env)->FindClass(env, HACKER_JNI_CLASS_NAME))) return JNI_ERR;

    JNINativeMethod m[] = {{"nativeHook", "(I)I", (void *)hacker_hook},
                           {"nativeUnhook", "()I", (void *)hacker_unhook},
                           {"nativeDumpRecords", "(Ljava/lang/String;)V", (void *)hacker_dump_records}};
    if (0 != (*env)->RegisterNatives(env, cls, m, sizeof(m) / sizeof(m[0]))) return JNI_ERR;*/

    return HACKER_JNI_VERSION;
}