package com.crogin.playandroidcompose.data.repository

import com.crogin.playandroidcompose.data.api.ApiService
import com.crogin.playandroidcompose.data.api.ApiResponse
import com.crogin.playandroidcompose.data.model.ArticleListResponse
import com.crogin.playandroidcompose.data.model.TabTree

class TabTreeRepository(private val apiService: ApiService) {
    suspend fun getTabItemTree(): ApiResponse<List<TabTree>> {
        return apiService.getTabItemTree()
    }

    suspend fun getWeChatTabTree(): ApiResponse<List<TabTree>> {
        return apiService.getWeChatTabTree()
    }

    suspend fun getWeChatArticleList(id: Int, page: Int): ApiResponse<ArticleListResponse> {
        return apiService.getWeChatArticleList(id, page)
    }

    suspend fun getProjectArticleList(cid: Int, page: Int): ApiResponse<ArticleListResponse> {
        return apiService.getProjectArticleList(page, cid)
    }
}
