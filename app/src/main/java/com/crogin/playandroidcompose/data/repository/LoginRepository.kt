package com.crogin.playandroidcompose.data.repository

import com.crogin.playandroidcompose.data.api.ApiResponse
import com.crogin.playandroidcompose.data.api.ApiService
import com.crogin.playandroidcompose.data.model.UserInfo

class LoginRepository(private val apiService: ApiService) {
    suspend fun login(username: String, password: String): ApiResponse<UserInfo> {
        return apiService.login(username, password)
    }
}
