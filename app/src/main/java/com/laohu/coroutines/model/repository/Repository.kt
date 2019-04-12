package com.laohu.coroutines.model.repository

import android.util.Log
import com.laohu.coroutines.model.ApiSource
import com.laohu.coroutines.model.await
import com.laohu.coroutines.pojo.Gank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

const val TAG = "TestCoroutine"
object Repository {

    /**
     * 两个请求在子线程中顺序执行，非同时并发
     */
    suspend fun querySyncWithContext(): List<Gank> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "withContext thread: ${Thread.currentThread().name}")

                val androidResult = ApiSource.instance.getAndroidGank().await()
                Log.d(TAG, "android: $androidResult")

                val iosResult = ApiSource.instance.getIOSGank().await()
                Log.d(TAG, "ios: $iosResult")

                val result = mutableListOf<Gank>().apply {
                    addAll(iosResult.results)
                    addAll(androidResult.results)
                }
                Log.d(TAG, "network request finish")
                result
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
    }

    /**
     * 两个请求在主线程中顺序执行，非同时并发
     */
    suspend fun querySyncNoneWithContext(): List<Gank> {
        return try {
            Log.d(TAG, "noneWithContext thread: ${Thread.currentThread().name}")
            val androidResult = ApiSource.instance.getAndroidGank().await()
            Log.d(TAG, "android: $androidResult")

            val iosResult = ApiSource.instance.getIOSGank().await()
            Log.d(TAG, "ios: $iosResult")

            val result = mutableListOf<Gank>().apply {
                addAll(iosResult.results)
                addAll(androidResult.results)
            }
            Log.d(TAG, "network request finish")
            result
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * 两个请求在子线程中并发执行
     */
    suspend fun queryAsyncWithContextForAwait(): List<Gank> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "withContext thread: ${Thread.currentThread().name}")

                val androidDeferred = async {
                    Log.d(TAG, "withContext async android thread: ${Thread.currentThread().name}")
                    val androidResult = ApiSource.instance.getAndroidGank().await()
                    Log.d(TAG, "android inner deferred: $androidResult")
                    androidResult
                }
                Log.d(TAG, "already call androidDeferred")

                val iosDeferred = async {
                    Log.d(TAG, "withContext async ios thread: ${Thread.currentThread().name}")
                    val iosResult = ApiSource.instance.getIOSGank().await()
                    Log.d(TAG, "ios inner deferred: $iosResult")
                    iosResult
                }
                Log.d(TAG, "already call iosDeferred")

                val androidResult = androidDeferred.await().results
                Log.d(TAG, "android: $androidResult")
                val iosResult = iosDeferred.await().results
                Log.d(TAG, "ios: $iosResult")

                val result = mutableListOf<Gank>().apply {
                    addAll(iosResult)
                    addAll(androidResult)
                }
                Log.d(TAG, "network request finish")
                result
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
    }

    /**
     * 两个请求在子线程中并发执行
     */
    suspend fun queryAsyncWithContextForNoAwait(): List<Gank> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "withContext thread: ${Thread.currentThread().name}")

                val androidDeferred = async {
                    Log.d(TAG, "withContext async android thread: ${Thread.currentThread().name}")
                    val androidResult = ApiSource.instance.getAndroidGank().execute()
                    if(androidResult.isSuccessful) {
                        Log.d(TAG, "android inner deferred: " + androidResult.body()!!.toString())
                        androidResult.body()!!
                    } else {
                        throw Throwable("android request failure")
                    }
                }
                Log.d(TAG, "already call androidDeferred")

                val iosDeferred = async {
                    Log.d(TAG, "withContext async ios thread: ${Thread.currentThread().name}")
                    val iosResult = ApiSource.instance.getIOSGank().execute()
                    if(iosResult.isSuccessful) {
                        Log.d(TAG, "ios inner deferred: " + iosResult.body()!!.toString())
                        iosResult.body()!!
                    } else {
                        throw Throwable("ios request failure")
                    }
                }
                Log.d(TAG, "already call iosDeferred")

                val androidResult = androidDeferred.await().results
                Log.d(TAG, "android: $androidResult")
                val iosResult = iosDeferred.await().results
                Log.d(TAG, "ios: $iosResult")

                val result = mutableListOf<Gank>().apply {
                    addAll(iosResult)
                    addAll(androidResult)
                }
                Log.d(TAG, "network request finish")
                result
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
    }

    suspend fun adapterCoroutineQuery(): List<Gank> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "withContext thread: ${Thread.currentThread().name}")

                val androidDeferred = ApiSource.callAdapterInstance.getAndroidGank()
                Log.d(TAG, "already call androidDeferred")

                val iosDeferred = ApiSource.callAdapterInstance.getIOSGank()
                Log.d(TAG, "already call iosDeferred")

                val androidResult = androidDeferred.await().results
                Log.d(TAG, "android: $androidResult")

                val iosResult = iosDeferred.await().results
                Log.d(TAG, "ios: $iosResult")

                val result = mutableListOf<Gank>().apply {
                    addAll(iosResult)
                    addAll(androidResult)
                }
                Log.d(TAG, "network request finish")
                result
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
    }
}