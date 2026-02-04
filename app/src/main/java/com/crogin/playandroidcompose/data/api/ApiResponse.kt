package com.crogin.playandroidcompose.data.api

data class ApiResponse<T>(
    val data: T,
    val errorCode: Int,
    val errorMsg: String
)