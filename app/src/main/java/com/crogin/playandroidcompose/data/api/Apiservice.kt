package com.crogin.playandroidcompose.data.api

import com.crogin.playandroidcompose.data.model.ArticleListResponse
import com.crogin.playandroidcompose.data.model.Banner
import com.crogin.playandroidcompose.data.model.CoinInfo
import com.crogin.playandroidcompose.data.model.Info
import com.crogin.playandroidcompose.data.model.TabTree
import com.crogin.playandroidcompose.data.model.UserInfo
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API服务接口
 * 
 * 定义与玩Android开放API进行通信的所有网络请求接口
 * 使用Retrofit2进行网络请求，支持协程suspend函数
 */
interface ApiService {
    /**
     * 获取首页轮播图列表
     * 
     * @return 包含Banner列表的ApiResponse
     */
    @GET("banner/json")
    suspend fun getBannerList(): ApiResponse<List<Banner>>

    /**
     * 获取首页文章列表
     * 
     * @param page 页码，从0开始
     * @return 包含文章列表的ApiResponse
     */
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): ApiResponse<ArticleListResponse>

    /**
     * 获取项目分类列表
     * 
     * @return 包含项目分类树的ApiResponse
     */
    @GET("project/tree/json")
    suspend fun getTabItemTree(): ApiResponse<List<TabTree>>

    /**
     * 获取指定项目的文章列表
     * 
     * @param page 页码，从1开始
     * @param cid 项目分类ID
     * @return 包含项目文章列表的ApiResponse
     */
    @GET("project/list/{page}/json")
    suspend fun getProjectArticleList(
        @Path("page") page: Int,
        @Query("cid") cid: Int
    ): ApiResponse<ArticleListResponse>

    /**
     * 获取微信公众号分类列表
     * 
     * @return 包含微信公众号分类的ApiResponse
     */
    @GET("wxarticle/chapters/json")
    suspend fun getWeChatTabTree(): ApiResponse<List<TabTree>>

    /**
     * 获取指定微信公众号的文章列表
     * 
     * @param id 微信公众号ID
     * @param page 页码，从1开始
     * @return 包含微信公众号文章列表的ApiResponse
     */
    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getWeChatArticleList(
        @Path("id") id: Int,
        @Path("page") page: Int
    ): ApiResponse<ArticleListResponse>

    /**
     * 获取用户个人信息（需要登录）
     * 
     * @return 包含用户信息的ApiResponse
     */
    @GET("user/lg/userinfo/json")
    suspend fun getProfile(): ApiResponse<Info<UserInfo>>

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 包含用户信息的ApiResponse
     */
    @POST("user/login")
    suspend fun login(@Query("username") username: String, @Query("password") password: String): ApiResponse<UserInfo>

    /**
     * 用户注册
     * 
     * @param username 用户名
     * @param password 密码
     * @param repassword 确认密码
     * @return 包含用户信息的ApiResponse
     */
    @POST("user/register")
    suspend fun register(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("repassword") repassword: String
    ): ApiResponse<UserInfo>

    /**
     * 收藏文章列表（需要登录）
     *
     * @param id 文章ID
     * @return 包含收藏状态的ApiResponse
     */
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectArticle(@Path("page") page: Int): ApiResponse<ArticleListResponse>

    /**
     * 收藏站内文章
     *
     * @param id 文章ID
     * @return 包含收藏状态的ApiResponse
     */
    @POST("lg/collect/{id}/json")
    suspend fun collectArticle(@Path("id") id: Int): ApiResponse<ArticleListResponse>

}