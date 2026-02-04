package com.crogin.playandroidcompose.data.repository

import com.crogin.playandroidcompose.data.api.ApiService
import com.crogin.playandroidcompose.data.api.ApiResponse
import com.crogin.playandroidcompose.data.model.ArticleListResponse

class ArticleRepository(private val apiService: ApiService) {
    suspend fun getArticleList(page: Int): ApiResponse<ArticleListResponse> {
        return apiService.getArticleList(page)
    }

     suspend fun getCollectArticle(page: Int): ApiResponse<ArticleListResponse> {
        return apiService.getCollectArticle(page)
    }

     suspend fun collectArticle(id: Int): ApiResponse<ArticleListResponse> {
        return apiService.collectArticle(id)
    }
}
