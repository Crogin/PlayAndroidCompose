package com.crogin.playandroidcompose.data.repository

import com.crogin.playandroidcompose.data.api.ApiService

class RegisterRepository(private val apiService: ApiService) {
    suspend fun register(username: String, password: String, rePassword: String) = apiService.register(username, password, rePassword)
}
