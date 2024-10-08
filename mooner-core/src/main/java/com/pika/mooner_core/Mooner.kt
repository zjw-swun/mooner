package com.pika.mooner_core

import android.util.Log
import androidx.annotation.Keep
import com.bytedance.android.bytehook.ByteHook
import com.bytedance.shadowhook.ShadowHook





// @author: pika

@Keep
object Mooner {
    private var hasInit = false
    const val TAG = "mooner"

    var invoker: (() -> Unit)? = null

    init {
        ByteHook.init(
            ByteHook.ConfigBuilder()
                .setMode(ByteHook.Mode.AUTOMATIC)
                .setDebug(false)
                .build()
        )

        ShadowHook.init(
            ShadowHook.ConfigBuilder()
                .setMode(ShadowHook.Mode.UNIQUE)
                .build()
        )

        try {
            System.loadLibrary("mooner_core")
            hasInit = true
        } catch (e: Exception) {
            e.printStackTrace()
            hasInit = false
        }
        Log.e(TAG, "init state is $hasInit")
    }

    fun initPreventPthreadCrash(soName: String, signal: Int, invoker:()->Unit) {
        this.invoker = invoker
        if (hasInit) {
            preventPthreadCrash(soName, signal)
        } else {
            Log.e(TAG, "init fail")
        }
    }

    fun catchSoCrash(soName: String,signal: Int, invoker:()->Unit) {
        this.invoker = invoker
        if (hasInit) {
            catchSoCrash(soName, signal)
        } else {
            Log.e(TAG, "init fail")
        }
    }


    @JvmStatic
    fun onError() {
        throw Exception("Mooner init error")
    }

    @JvmStatic
    fun onHandleSignal() {
        invoker?.invoke()
    }

    fun initMSponge(){
        if (hasInit) {
           memorySponge()
        }
    }

    fun startMutexMonitor(hookSoNmae:String){
        if (hasInit) {
            mutexMonitor(hookSoNmae)
        }
    }

    private external fun mutexMonitor(hookSoNmae:String)


    private external fun preventPthreadCrash(soName: String, signal: Int)

    private external fun catchSoCrash(soName: String, signal: Int)


    private external fun memorySponge()

}