package com.laohu.coroutines.model

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.laohu.coroutines.model.repository.TAG
import com.laohu.coroutines.pojo.GankResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface CallAdapterApiService {
    @GET("data/iOS/2/1")
    fun getIOSGank(): Deferred<GankResult>

    @GET("data/Android/2/1")
    fun getAndroidGank(): Deferred<GankResult>
}

interface ApiService {
    @GET("data/iOS/2/1")
    fun getIOSGank(): Call<GankResult>

    @GET("data/Android/2/1")
    fun getAndroidGank(): Call<GankResult>

    @GET("data/Android/2/1")
    suspend fun getSuspendAndroidGank(): GankResult

    @GET("data/iOS/2/1")
    suspend fun getSuspendIOSGank(): GankResult
}

class ApiSource {
    companion object {
        @JvmField
        val callAdapterInstance = Retrofit.Builder()
            .baseUrl("http://gank.io/api/")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(CallAdapterApiService::class.java)

        @JvmField
        val instance = Retrofit.Builder()
            .baseUrl("http://gank.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }
}

suspend fun <T> Call<T>.await(): T {
    return suspendCancellableCoroutine {
        it.invokeOnCancellation {
            Log.d(TAG, "request cancel")
            it?.printStackTrace()
            cancel()
        }
        enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                it.resumeWithException(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if(response.isSuccessful) {
                    it.resume(response.body()!!)
                } else{
                    it.resumeWithException(Throwable(response.toString()))
                }
            }
        })
    }
}