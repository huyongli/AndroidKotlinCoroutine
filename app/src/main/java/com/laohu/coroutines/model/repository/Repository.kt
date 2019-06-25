package com.laohu.coroutines.model.repository

import com.laohu.coroutines.model.ApiSource
import com.laohu.coroutines.model.await
import com.laohu.coroutines.pojo.Gank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

const val TAG = "TestCoroutine"
object Repository {

    /**
     * 两个请求在子线程中顺序执行，非同时并发
     */
    suspend fun querySyncWithContext(): List<Gank> {
        return withContext(Dispatchers.Main) {
            try {
                val androidResult = ApiSource.instance.getAndroidGank().await()

                val iosResult = ApiSource.instance.getIOSGank().await()

                val result = mutableListOf<Gank>().apply {
                    addAll(iosResult.results)
                    addAll(androidResult.results)
                }
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
            val androidResult = ApiSource.instance.getAndroidGank().await()

            val iosResult = ApiSource.instance.getIOSGank().await()

            val result = mutableListOf<Gank>().apply {
                addAll(iosResult.results)
                addAll(androidResult.results)
            }
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
        return withContext(Dispatchers.Main) {
            try {
                val androidDeferred = async {
                    val androidResult = ApiSource.instance.getAndroidGank().await()
                    androidResult
                }

                val iosDeferred = async {
                    val iosResult = ApiSource.instance.getIOSGank().await()
                    iosResult
                }

                val androidResult = androidDeferred.await().results
                val iosResult = iosDeferred.await().results

                val result = mutableListOf<Gank>().apply {
                    addAll(iosResult)
                    addAll(androidResult)
                }
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
                val androidDeferred = async {
                    val androidResult = ApiSource.instance.getAndroidGank().execute()
                    if(androidResult.isSuccessful) {
                        androidResult.body()!!
                    } else {
                        throw Throwable("android request failure")
                    }
                }

                val iosDeferred = async {
                    val iosResult = ApiSource.instance.getIOSGank().execute()
                    if(iosResult.isSuccessful) {
                        iosResult.body()!!
                    } else {
                        throw Throwable("ios request failure")
                    }
                }

                val androidResult = androidDeferred.await().results
                val iosResult = iosDeferred.await().results

                val result = mutableListOf<Gank>().apply {
                    addAll(iosResult)
                    addAll(androidResult)
                }
                result
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
    }

    suspend fun adapterCoroutineQuery(): List<Gank> {
        return withContext(Dispatchers.Main) {
            try {
                val androidDeferred = ApiSource.callAdapterInstance.getAndroidGank()

                val iosDeferred = ApiSource.callAdapterInstance.getIOSGank()

                val androidResult = androidDeferred.await().results

                val iosResult = iosDeferred.await().results

                val result = mutableListOf<Gank>().apply {
                    addAll(iosResult)
                    addAll(androidResult)
                }
                result
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
    }

    suspend fun retrofitSuspendQuery(): List<Gank> {
        return withContext(Dispatchers.Main) {
            try {
                val androidResult = ApiSource.instance.getSuspendAndroidGank()
                val iosResult = ApiSource.instance.getSuspendIOSGank()
                mutableListOf<Gank>().apply {
                    addAll(iosResult.results)
                    addAll(androidResult.results)
                }
            } catch (e: Throwable) {
                throw e
            }
        }
    }
}