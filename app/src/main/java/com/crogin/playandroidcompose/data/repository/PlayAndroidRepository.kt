package com.crogin.playandroidcompose.data.repository

import android.content.Context
import com.crogin.playandroidcompose.data.api.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.also
import kotlin.apply
import kotlin.jvm.java
import com.tencent.mmkv.MMKV

/**
 * 玩 Android 统一数据仓库
 *
 * 采用单例模式，负责：
 * 1. 创建并持有全局唯一的 [ApiService] 实例
 * 2. 自动拦截请求/响应，完成 Cookie 持久化（登录态同步）
 * 3. 对外暴露 `apiService`，供 ViewModel 层直接调用
 *
 * 线程安全：双重校验锁（DCL）实现单例
 *
 * @property appContext ApplicationContext，避免内存泄漏
 * @property apiService 懒加载的 Retrofit 代理接口，已配置日志、超时、Cookie 自动读写
 */
class PlayAndroidRepository private constructor(context: Context) {

    companion object {
        /** 玩 Android 官方域名 */
        private const val BASE_URL = "https://www.wanandroid.com/"

        /** MMKV 中保存 Cookie 的 key */
        private const val KEY_SESSION = "session"

        /** 单例缓存，@Volatile 保证可见性 */
        @Volatile
        private var INSTANCE: PlayAndroidRepository? = null

        /**
         * 获取单例
         *
         * @param context 任意上下文，内部使用 ApplicationContext
         * @return 全局唯一的仓库实例
         */
        fun getInstance(context: Context): PlayAndroidRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlayAndroidRepository(context.applicationContext).also { INSTANCE = it }
            }
    }

    private val appContext = context.applicationContext

    init {
        // 初始化腾讯 MMKV，用于本地持久化 Cookie
        MMKV.initialize(appContext)
    }

    /**
     * 请求拦截器：在请求头中注入本地保存的 Cookie，实现登录态透传
     */
    private class SessionRequestInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val mmkv = MMKV.defaultMMKV()
            val session = mmkv.decodeString(KEY_SESSION, null)
            val request = chain.request().newBuilder().apply {
                if (!session.isNullOrEmpty()) addHeader("Cookie", session)
            }.build()
            return chain.proceed(request)
        }
    }

    /**
     * 响应拦截器：把服务器返回的 Set-Cookie 持久化到 MMKV，更新登录态
     */
    private inner class SessionResponseInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            val setCookies = response.headers("Set-Cookie")
            if (setCookies.isNotEmpty()) {
                val cookieHeader = setCookies.joinToString("; ")
                MMKV.defaultMMKV().encode(KEY_SESSION, cookieHeader)
            }
            return response
        }
    }

    /**
     * 全局唯一的 Retrofit 接口实例，已配置：
     * - HttpLoggingInterceptor（BODY 级别日志）
     * - 请求/响应 Cookie 拦截器
     * - 15s 连接/读/写超时
     * - Gson 转换器
     */
    val apiService: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)                       // 日志
            .addInterceptor(SessionRequestInterceptor())   // 写 Cookie
            .addInterceptor(SessionResponseInterceptor())  // 读 Cookie
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}