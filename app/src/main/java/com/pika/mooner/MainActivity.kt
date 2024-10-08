package com.pika.mooner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.pika.mooner.databinding.ActivityMainBinding
import com.pika.mooner_core.Mooner

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val bigObject = ArrayList<LargeObjectTest>()
    private val normalObject = ArrayList<SmallObjectTest>()
    private var bigObjectClickTime = 0
    private var normalObjectClickTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // pthread create 防crash demo
        binding.crashText.setOnClickListener {
            createThreadCrash()
        }

        binding.fixText.setOnClickListener {
            Mooner.initPreventPthreadCrash("libmooner.so", 11) {
                Log.e("mooner", "catch exception")
            }
        }

        // msponge 突破虚拟机堆大小限制 这里可以根据自己的手机，模拟点击几次，对比开启方案后的效果
        binding.oomAlloc.setOnClickListener {
            bigObject.add(LargeObjectTest())
            bigObjectClickTime++
            binding.oomAlloc.text = "大对象分配了 $bigObjectClickTime 次"
        }

        binding.oomAllocSmall.setOnClickListener {
            normalObject.add(SmallObjectTest())
            normalObjectClickTime++
            binding.oomAllocSmall.text = "普通对象分配了 $normalObjectClickTime 次"
        }

        binding.msponge.setOnClickListener {
            Mooner.initMSponge()
        }

        // 开启监控销毁锁 传入需要监控的so名称
        Mooner.startMutexMonitor("libmooner.so")
        binding.destroyedMutexMonitor.setOnClickListener {
            // 制造一个crash，查看log mooner
            createDestroyedPthreadMutex()
        }
    }


    companion object {
        init {
            Mooner.catchSoCrash("libmooner.so", 11){
                Log.e("mooner", "catchSoCrash exception")
            }
           /* Mooner.initPreventPthreadCrash("libmooner_core.so", 6){
                Log.e("mooner", "catchSoCrash exception")
            }*/
            System.loadLibrary("mooner")
        }
    }

    external fun createThreadCrash()

    // 测试锁释放后使用
    external fun createDestroyedPthreadMutex()
}