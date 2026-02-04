package com.crogin.playandroidcompose.data.repository

import com.crogin.playandroidcompose.data.api.ApiResponse
import com.crogin.playandroidcompose.data.api.ApiService
import com.crogin.playandroidcompose.data.model.Info
import com.crogin.playandroidcompose.data.model.UserInfo

class ProfileRepository(private val apiService: ApiService) {
    suspend fun getProfile(): ApiResponse<Info<UserInfo>> {
        return apiService.getProfile()
    }
}