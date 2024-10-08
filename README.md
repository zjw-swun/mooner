# fork from https://github.com/TestPlanB/mooner
适配16kb-page-size

# mooner
mooner 是一系列疑难native crash解决手段与黑科技实践的集合体，现包括以下功能：

功能1. 捕获 Android 基于“pthread_create” 产生的子线程中异常业务逻辑产生信号，导致的native crash

功能2. 突破Java虚拟机堆大小限制，实现LargeObjectSpace的内存隐藏，仅在OOM发生时生效，最小力度的影响

（demo演示：前一次是正常的OOM，后一次是开启mSponge方案后）



https://user-images.githubusercontent.com/65278264/231100416-deb94fdd-8a46-493f-a0fd-ca338ea8abc2.mp4


功能3. 用于排查pthread_mutex释放后使用的情况，当发生释放后使用时，会打印释放时的堆栈信息


## 功能1 捕获范围
1.由pthread_create 创建的线程中，执行的异常业务逻辑

2.捕获sigaction所支持的信号

3.监听信号时采用的是回溯处理，因此不像java 层try catch一样，而是将本次操作“清除”，如果业务强依赖这次操作，请做好开关降级处理

## 功能2 生效范围[目前作者用的是android11 手机，其他版本待todo适配，主要以学习为主，线上需要做好测试兼容]
隐藏LargeObjectSpace的内存使用（FreeListSpace），提高堆内存的上限，提升大小为当前（FreeListSpace的使用内存大小），且只在OOM时生效，挽救OOM

## 功能3 pthread_mutex 释放跟踪
当出现锁释放后使用时，给出释放时的堆栈信息，用于异常排查

## 详细介绍
功能1: https://juejin.cn/post/7178341941480783931/

功能2: https://juejin.cn/post/7218379300505059365

## 使用说明
已发布到mavencenter仓库
gradle 导入 已包含全部功能
```
implementation 'io.github.TestPlanB:mooner:2.0.2'
```
### 功能1使用

使用方式非常简单：

参数1:需要进行兜底的so库名，全名称 

参数2:需要捕获的信号，比如11 SIGSEGV 

参数3:回调，当异常捕获后就会回调该参数


```
Mooner.initMooner("libmooner.so",11){
                Log.e("mooner","catch exception")
            }

```

### 功能2使用


合适的时候调用以下即可实现堆内存限制突破（只是初始化），会在OOM的时候生效

关于demo：demo模拟了一下内存申请，分别是大对象申请与多个小对象申请，验证效果请先点击几下小对象申请，然后再点大对象申请，因为如果OOM发生时，多次申请小对象，也就会造成多次gc（仅实验条件下）

```
 Mooner.initMSponge()

```

### 功能3使用
参数1 是需要监控的so名称，此时框架内会hook所有pthread_mutex相关的调用，比如：
```
Mooner.startMutexMonitor("libmooner.so")
```



## 项目层级介绍
* **app下是使用例子**
* **mooner-core 是mooner的核心实现**

## 环境准备
建议直接用最新的稳定版本Android Studio打开工程。目前项目已适配`Android Studio Arctic Fox | 2022.3.1`
### 
