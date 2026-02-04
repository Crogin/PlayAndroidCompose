package com.crogin.playandroidcompose.data.repository

import com.crogin.playandroidcompose.data.api.ApiService
import com.crogin.playandroidcompose.data.api.ApiResponse
import com.crogin.playandroidcompose.data.model.Banner

class BannerRepository(private val apiService: ApiService) {

    suspend fun getBannerList(): ApiResponse<List<Banner>> {
        return apiService.getBannerList()
    }
}

